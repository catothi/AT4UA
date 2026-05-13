import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Demo 3 - Worker mit at-least-once + Idempotenz-Cache.
 *
 * Ablauf pro Nachricht:
 *   1. receive()
 *   2. Idempotenz-Pruefung: schon im cache?
 *      ja  -> Re-Delivery, gespeichertes Ergebnis erneut senden, ACK
 *      nein-> rechnen, in cache speichern, senden, ACK
 *
 * Re-Delivery wird einmalig fuer msgId=3 simuliert: nach dem Senden des
 * Ergebnisses rufen wir session.recover() auf statt msg.acknowledge().
 * Damit bekommt der Broker kein ACK und liefert msgId=3 erneut.
 *
 * Wichtig: der cache (msgId -> result) ist hier eine simple HashMap im
 * RAM. In Produktion waere das eine DB-Tabelle / Redis mit msgId als
 * Primary Key, damit der Cache auch Worker-Neustarts ueberlebt.
 */
public class CalcWorkerIdem {

    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(session.createQueue(CalcProducerIdem.TASK_QUEUE));
        MessageProducer producer = session.createProducer(session.createQueue(CalcProducerIdem.RESULT_QUEUE));

        Map<Integer, Integer> cache = new HashMap<>();   // msgId -> berechnetes result
        boolean crashSimulated = false;

        System.out.println("[Worker] bereit (idempotent), warte auf Aufgaben aus '"
                + CalcProducerIdem.TASK_QUEUE + "' ...");

        while (true) {
            Message msg = consumer.receive(10000);
            if (msg == null) {
                System.out.println("[Worker] keine weiteren Aufgaben - beende. Cache-Inhalt: " + cache);
                break;
            }

            int id = msg.getIntProperty("msgId");
            int a  = msg.getIntProperty("a");
            int b  = msg.getIntProperty("b");

            // (1) Idempotenz-Pruefung
            if (cache.containsKey(id)) {
                int cached = cache.get(id);
                System.out.println("[Worker] RE-DELIVERY msgId=" + id + " - aus Cache, NICHT neu berechnet (result=" + cached + ")");
                sendResult(session, producer, id, cached);
                msg.acknowledge();
                continue;
            }

            // (2) erstmalige Verarbeitung
            int result = a + b;
            cache.put(id, result);
            System.out.println("[Worker] berechnet  msgId=" + id + "  " + a + "+" + b + "=" + result + "  (Cache-Groesse=" + cache.size() + ")");
            sendResult(session, producer, id, result);

            // (3) bei msgId=3 Crash simulieren: kein ACK -> recover() -> Re-Delivery
            if (id == 3 && !crashSimulated) {
                crashSimulated = true;
                System.out.println("[Worker] !! Simuliere Crash vor ACK fuer msgId=" + id + ", session.recover() ...");
                session.recover();
                continue;
            }

            // (4) ACK NACH Senden -> at-least-once
            msg.acknowledge();
            System.out.println("[Worker] ACK msgId=" + id);
        }

        connection.close();
    }

    private static void sendResult(Session s, MessageProducer p, int id, int result) throws JMSException {
        TextMessage out = s.createTextMessage(String.valueOf(result));
        out.setIntProperty("msgId", id);
        out.setIntProperty("result", result);
        p.send(out);
    }
}
