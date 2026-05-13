package edu.thi.messaging;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/*
 * Example based on code delivered with ActiveMQ, located in folder:
 *  .../apache-activemq-5.14.0/examples/openwire/java/src/main/java/example
 */
public class QueueConsumer1 {

    public static void main(String[] args) throws JMSException {
        String user = ActiveMQConnection.DEFAULT_USER;
        String password = ActiveMQConnection.DEFAULT_PASSWORD;
        String url = ActiveMQConnection.DEFAULT_BROKER_URL;
        Destination destination;
        Connection connection = null;

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        destination = session.createQueue("ActiveMQTestQueue3");

        MessageConsumer consumer = session.createConsumer(destination);

        long start = System.currentTimeMillis();
        long count = 1;
        System.out.println("QueueConsumer1 waiting for messages...");
        while (true) {
            Message msg = consumer.receive();
            if (msg instanceof TextMessage) {
                String body = ((TextMessage) msg).getText();
                if ("SHUTDOWN".equals(body)) {
                    long diff = System.currentTimeMillis() - start;
                    System.out.println(String.format("QueueConsumer1 received %d in %.2f seconds", count, (1.0 * diff / 1000.0)));
                    break;
                } else {
                    System.out.println("Message with id " + msg.getIntProperty("id") + " received!");
                    if (count % 100 == 0) {
                        System.out.println(String.format("Received %d messages.", count));
                    }
                    count++;
                }

            } else {
                System.out.println("Unexpected message type: " + msg.getClass());
            }
        }
        connection.close();
    }

}
