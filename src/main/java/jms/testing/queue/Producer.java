package jms.testing.queue;

import java.lang.invoke.MethodHandles;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jms.service.JmsService;
import jms.service.JmsServiceImpl;

import static jms.testing.Config.BROKER_URL;
import static jms.testing.Config.QUEUE_NAME;

public class Producer {

    private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws JMSException {
        JmsService jmsService = null;
        try {
            jmsService = new JmsServiceImpl(BROKER_URL);
            jmsService.initialize();

            sendText(jmsService, "A");
            sendText(jmsService, "B");
            sendText(jmsService, "C");
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if (jmsService != null) {
                jmsService.deinitialize();
            }
        }
    }

    private static void sendText(JmsService jmsService, String message) throws JMSException {
        TextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText(message);
        jmsService.sendMessage(textMessage, QUEUE_NAME);
        LOGGER.info("Send : {}", message);
    }
}
