package jms.testing.topic;

import java.lang.invoke.MethodHandles;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jms.service.JmsService;
import jms.service.JmsServiceImpl;

import static jms.testing.Config.BROKER_URL;
import static jms.testing.Config.TOPIC_NAME;

public class Producer {

    private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws JMSException {
        JmsService jmsService = null;
        try {
            jmsService = new JmsServiceImpl(BROKER_URL);
            jmsService.initialize();

            publishText(jmsService, "A");
            publishText(jmsService, "B");
            publishText(jmsService, "C");
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if (jmsService != null) {
                jmsService.deinitialize();
            }
        }
    }

    private static void publishText(JmsService jmsService, String message) throws JMSException {
        TextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText(message);
        jmsService.publishMessage(textMessage, TOPIC_NAME);
        LOGGER.info("Send : {}", message);
    }
}

