package edu.thi.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class TopicMessageListener implements MessageListener {
    private long count = 1;
    private boolean[] finishReceiving = null;
    
    public TopicMessageListener(boolean[] finishReceiving) {
        this.finishReceiving = finishReceiving;
    }
    @Override
    public void onMessage(Message msg) {
        if (msg instanceof TextMessage) {
            String body = null;
            try {
                body = ((TextMessage) msg).getText();
            } catch (JMSException e) {
                e.printStackTrace();
            }
            
            if ("SHUTDOWN".equals(body)) {
                System.out.println(String.format("TopicMessageListener received %d messages in total.", count));
                this.finishReceiving[0] = true;
            } else {
                if (count % 100 == 0) {
                    System.out.println(String.format("Received %d messages.", count));
                }
            }
            count++;
        } else {
            System.out.println("Unexpected message type: " + msg.getClass());
        }
    }
}
