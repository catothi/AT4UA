import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Demo 2 - Worker mit at-least-once-Semantik (ACK NACH Berechnung).
 *
 * Ablauf pro Nachricht:
 *   1. receive() - Aufgabe abholen
 *   2. compute() - rechnen
 *   3. send result - Ergebnis zurueck
 *   4. acknowledge() - ACK NUR wenn alles erfolgreich
 *
 * Falls Crash zwischen send() und ACK():
 *   - ACK kommt nie an
 *   - Broker schickt Aufgabe nochmal
 *   - at-least-once: Aufgabe wird mindestens 1x verarbeitet
 *   - Mit Idempotenz-Cache (Demo 3):
 */
public class CalcWorkerAck {

    public static void main(String[] args) throws JMSException {
        String url = ActiveMQConnection.DEFAULT_BROKER_URL;
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);

        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Destination tasks   = session.createQueue(CalcProducerAck.TASK_QUEUE);
        Destination results = session.createQueue(CalcProducerAck.RESULT_QUEUE);

        MessageConsumer consumer = session.createConsumer(tasks);
        MessageProducer producer = session.createProducer(results);

        System.out.println("[Worker] bereit (at-least-once), warte auf Aufgaben aus '"
                + CalcProducerAck.TASK_QUEUE + "' ...");

        while (true) {
            Message msg = consumer.receive(10000);
            if (msg == null) {
                System.out.println("[Worker] keine weiteren Aufgaben - beende.");
                break;
            }

            int id = msg.getIntProperty("msgId");
            int a  = msg.getIntProperty("a");
            int b  = msg.getIntProperty("b");

            try {
                // (1) ZUERST berechnen
                int result = a + b;
                System.out.println("[Worker] berechnet  msgId=" + id + "  " + a + "+" + b + "=" + result);

                // (2) DANN Ergebnis senden
                TextMessage out = session.createTextMessage(String.valueOf(result));
                out.setIntProperty("msgId", id);
                out.setIntProperty("result", result);
                producer.send(out);
                System.out.println("[Worker] gesendet   msgId=" + id);

                // (3) NUR WENN alles ok: acknowledge
                msg.acknowledge();
                System.out.println("[Worker] ACK msgId=" + id);

            } catch (Exception e) {
                System.err.println("[Worker] FEHLER bei msgId=" + id + ": " + e.getMessage());
                // kein acknowledge() -> Aufgabe bleibt in Queue -> Re-Delivery
            }
        }

        connection.close();
    }
}
