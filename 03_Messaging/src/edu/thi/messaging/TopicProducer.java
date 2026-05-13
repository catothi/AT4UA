package edu.thi.messaging;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/*
 * Example based on code delivered with ActiveMQ, located in folder:
 *  .../apache-activemq-5.14.0/examples/openwire/java/src/main/java/example
 */
public class TopicProducer {

    public static void main(String[] args) throws JMSException {
        int messages = 1000;
        int size = 256;

        String DATA = "abcdefghijklmnopqrstuvwxyz";
        String body = "";
        for( int i=0; i < size; i ++) {
            body += DATA.charAt(i%DATA.length());
        }
        
        String user = ActiveMQConnection.DEFAULT_USER;
        String password = ActiveMQConnection.DEFAULT_PASSWORD;
        String url = ActiveMQConnection.DEFAULT_BROKER_URL;
        Topic topic;
        Connection connection = null;
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        connection = connectionFactory.createConnection();
        connection.start();
        Session pubSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        topic = pubSession.createTopic("ActiveMQTestTopic");
        MessageProducer publisher = pubSession.createProducer(topic);
        publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        for( int i=1; i <= messages; i ++) {
            TextMessage msg = pubSession.createTextMessage(body);
            msg.setIntProperty("id", i);
            publisher.send(msg);
            if( (i % 1000) == 0) {
                System.out.println(String.format("Sent %d messages", i));
            }
        }

        publisher.send(pubSession.createTextMessage("SHUTDOWN"));
        System.out.println(String.format("Sent %d messages in total.", messages+1));
        connection.close();
    }

}
