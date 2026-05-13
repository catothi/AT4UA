import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Demo 3 - Producer fuer at-least-once + Idempotenz.
 *
 * Schickt 5 Aufgaben an "calc.tasks.idem" und empfaengt Ergebnisse aus
 * "calc.results.idem". Da der Worker bei einem simulierten Crash eine
 * Re-Delivery ausloest, koennen Ergebnisse DOPPELT in der Result-Queue
 * landen. Der Producer dedupliziert anhand der msgId.
 */
public class CalcProducerIdem {

    public static final String TASK_QUEUE   = "calc.tasks.idem";
    public static final String RESULT_QUEUE = "calc.results.idem";
    public static final int    TASK_COUNT   = 5;

    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        MessageProducer producer       = session.createProducer(session.createQueue(TASK_QUEUE));
        MessageConsumer resultConsumer = session.createConsumer(session.createQueue(RESULT_QUEUE));

        for (int id = 1; id <= TASK_COUNT; id++) {
            int a = id, b = id;
            TextMessage msg = session.createTextMessage(a + "+" + b);
            msg.setIntProperty("msgId", id);
            msg.setIntProperty("a", a);
            msg.setIntProperty("b", b);
            producer.send(msg);
            System.out.println("[Producer] gesendet  msgId=" + id + "  task=" + a + "+" + b);
        }

        System.out.println();
        System.out.println("[Producer] warte auf Ergebnisse (dedupliziere ueber msgId) ...");

        Set<Integer> seen = new HashSet<>();
        int duplicates = 0;
        // Wir warten auf bis zu TASK_COUNT*2 Nachrichten (Doubles moeglich),
        // brechen aber ab, sobald alle TASK_COUNT eindeutigen IDs gesehen wurden
        // oder ein Timeout greift.
        for (int n = 0; n < TASK_COUNT * 2 && seen.size() < TASK_COUNT; n++) {
            Message m = resultConsumer.receive(5000);
            if (m == null) {
                System.out.println("[Producer] TIMEOUT - keine weiteren Antworten.");
                break;
            }
            int rid = m.getIntProperty("msgId");
            int sum = m.getIntProperty("result");
            if (!seen.add(rid)) {
                duplicates++;
                System.out.println("[Producer] DOUBLE   msgId=" + rid + "  - dupliziert, ignoriert");
                continue;
            }
            System.out.println("[Producer] empfangen msgId=" + rid + "  result=" + sum);
        }

        System.out.println();
        System.out.println("[Producer] Fertig: " + seen.size() + "/" + TASK_COUNT + " eindeutige Ergebnisse, "
                + duplicates + " Duplikate verworfen.");
        System.out.println("[Producer] -> at-least-once: jede Aufgabe MINDESTENS einmal verarbeitet,");
        System.out.println("[Producer]    Doubles werden vom Producer per msgId-Set herausgefiltert.");
        connection.close();
    }
}
