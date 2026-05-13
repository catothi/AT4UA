import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Demo 1 - Worker mit at-most-once-Semantik.
 *
 * Ablauf pro Nachricht:
 *   1. receive() - Aufgabe abholen
 *   2. acknowledge() - SOFORT bestaetigen (Broker loescht die Nachricht!)
 *   3. compute() - rechnen
 *   4. send result - Ergebnis zurueck an "calc.results.normal"
 *
 * Da Schritt 2 vor Schritt 3 kommt, geht eine Aufgabe verloren,
 * falls der Worker zwischen ACK und Result-Versand crasht
 * --> "at most once": jede Aufgabe wird hoechstens ein Mal verarbeitet.
 */
public class CalcWorker {

    public static void main(String[] args) throws JMSException {
        String url = ActiveMQConnection.DEFAULT_BROKER_URL;
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);

        Connection connection = factory.createConnection();
        connection.start();

        // CLIENT_ACKNOWLEDGE: wir entscheiden manuell, wann bestaetigt wird.
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Destination tasks   = session.createQueue(CalcProducer.TASK_QUEUE);
        Destination results = session.createQueue(CalcProducer.RESULT_QUEUE);

        MessageConsumer consumer = session.createConsumer(tasks);
        MessageProducer producer = session.createProducer(results);

        System.out.println("[Worker] bereit (at-most-once), warte auf Aufgaben aus '" + CalcProducer.TASK_QUEUE + "' ...");

        while (true) {
            Message msg = consumer.receive(10000);
            if (msg == null) {
                System.out.println("[Worker] keine weiteren Aufgaben - beende.");
                break;
            }

            int id = msg.getIntProperty("msgId");
            int a  = msg.getIntProperty("a");
            int b  = msg.getIntProperty("b");

            // (1) ACK SOFORT --> at-most-once: ab jetzt ist die Aufgabe weg, falls wir crashen.
            msg.acknowledge();
            System.out.println("[Worker] ACK msgId=" + id);

            // (2) Berechnung
            int result = a + b;
            System.out.println("[Worker] berechnet  msgId=" + id + "  " + a + "+" + b + "=" + result);

            // (3) Ergebnis zurueckschicken
            TextMessage out = session.createTextMessage(String.valueOf(result));
            out.setIntProperty("msgId", id);
            out.setIntProperty("result", result);
            producer.send(out);
        }

        connection.close();
    }
}
