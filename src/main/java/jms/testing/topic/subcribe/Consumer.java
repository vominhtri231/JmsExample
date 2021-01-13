package jms.testing.topic.subcribe;

import java.util.Scanner;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import jms.service.JmsService;
import jms.service.JmsServiceImpl;

import static jms.testing.Config.BROKER_URL;
import static jms.testing.Config.TOPIC_NAME;

public class Consumer {

    public static void main(String[] args) throws Exception {
        JmsService jmsService = new JmsServiceImpl(BROKER_URL);
        jmsService.initialize();

        AutoCloseable closeable = jmsService.subscribeToTopic(createMessageListener(), TOPIC_NAME);

        new Scanner(System.in).nextLine();
        System.out.println("trying to stop");
        closeable.close();
        jmsService.deinitialize();
    }

    private static MessageListener createMessageListener() {
        return message -> {
            try {
                System.out.println("Received: " + ((TextMessage) message).getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        };
    }
}
