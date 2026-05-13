package edu.thi.messaging;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/*
 * Example based on code delivered with ActiveMQ, located in folder:
 *  .../apache-activemq-5.14.0/examples/openwire/java/src/main/java/example
 */
public class TopicAsyncConsumer {

    public static void main(String[] args) throws JMSException, InterruptedException {
        String user = ActiveMQConnection.DEFAULT_USER;
        String password = ActiveMQConnection.DEFAULT_PASSWORD;
        String url = ActiveMQConnection.DEFAULT_BROKER_URL;
        Topic topic;
        Connection connection = null;
        boolean[] finishReceiving = { false };

        ActiveMQConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory(user, password, url);
        connection = connectionFactory.createConnection();
        connection.start();
        Session subSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        topic = subSession.createTopic("ActiveMQTestTopic");

        MessageConsumer subscriber = subSession.createConsumer(topic);

        
        
        // Setting MessageListener!!!
        subscriber.setMessageListener(new TopicMessageListener(finishReceiving));

        System.out.println("Async Subscriber is ready, waiting for messages...");  
        System.out.println("press Ctrl+c to shutdown..."); 
        

        // Now the server stops whenever a SHUTDOWN message is received (see TopicMessageListener.java)
        while(!finishReceiving[0]){                  
            Thread.sleep(1000);  
            System.out.println("AsyncConsumer still working...");
        } 
        System.out.println("AsyncConsumer ends!");
        System.exit(0);
    }
}
