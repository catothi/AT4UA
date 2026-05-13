import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Demo 2 - Producer fuer at-least-once (ACK NACH Berechnung).
 *
 * Schickt 5 Rechen-Aufgaben (a + b) an die Queue "calc.tasks.ack"
 * und wartet auf die Ergebnisse aus "calc.results.ack".
 *
 * Jede Nachricht bekommt eine sequenziell steigende msgId (1..5).
 * Der Producer schreibt jede Aufgabe genau EIN MAL - der Worker
 * bestaetigt erst NACH erfolgreicher Berechnung und Result-Versand.
 */
public class CalcProducerAck {

    public static final String TASK_QUEUE   = "calc.tasks.ack";
    public static final String RESULT_QUEUE = "calc.results.ack";
    public static final int    TASK_COUNT   = 5;

    public static void main(String[] args) throws JMSException {
        String url = ActiveMQConnection.DEFAULT_BROKER_URL; // tcp://localhost:61616
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);

        Connection connection = factory.createConnection();
        connection.start();

        // AUTO_ACKNOWLEDGE reicht beim Producer ==> sobald es ein Consumer hat;
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination tasks   = session.createQueue(TASK_QUEUE);
        Destination results = session.createQueue(RESULT_QUEUE);

        MessageProducer producer = session.createProducer(tasks);
        MessageConsumer resultConsumer = session.createConsumer(results);

        // 1) Aufgaben verschicken: (1+1), (2+2), ..., (5+5)
        for (int id = 1; id <= TASK_COUNT; id++) {
            int a = id;
            int b = id;
            TextMessage msg = session.createTextMessage(a + "+" + b);
            msg.setIntProperty("msgId", id);
            msg.setIntProperty("a", a);
            msg.setIntProperty("b", b);
            producer.send(msg);
            System.out.println("[Producer] gesendet  msgId=" + id + "  task=" + a + "+" + b);
        }

        // 2) Auf Ergebnisse warten (max. 3 Sekunden pro erwarteter Antwort)
        System.out.println();
        System.out.println("[Producer] warte auf " + TASK_COUNT + " Ergebnisse ...");
        int received = 0;
        for (int id = 1; id <= TASK_COUNT; id++) {
            Message m = resultConsumer.receive(3000);
            if (m == null) {
                System.out.println("[Producer] TIMEOUT - kein Ergebnis fuer msgId=" + id + " (Nachricht verloren?)");
                continue;
            }
            int rid = m.getIntProperty("msgId");
            int sum = m.getIntProperty("result");
            System.out.println("[Producer] empfangen msgId=" + rid + "  result=" + sum);
            received++;
        }

        System.out.println();
        System.out.println("[Producer] Fertig: " + received + "/" + TASK_COUNT + " Ergebnisse erhalten.");
        connection.close();
    }
}
