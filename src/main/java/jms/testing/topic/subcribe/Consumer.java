package jms.testing.topic.subcribe;

import java.lang.invoke.MethodHandles;
import java.util.Scanner;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jms.service.JmsService;
import jms.service.JmsServiceImpl;

import static jms.testing.Config.BROKER_URL;
import static jms.testing.Config.TOPIC_NAME;

public class Consumer {

    private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws Exception {
        JmsService jmsService = new JmsServiceImpl(BROKER_URL);
        jmsService.initialize();

        AutoCloseable closeable = jmsService.subscribeToTopic(createMessageListener(), TOPIC_NAME);

        new Scanner(System.in).nextLine();
        LOGGER.info("trying to stop");
        closeable.close();
        jmsService.deinitialize();
    }

    private static MessageListener createMessageListener() {
        return message -> {
            try {
                LOGGER.info("Received: {}", ((TextMessage) message).getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        };
    }
}
