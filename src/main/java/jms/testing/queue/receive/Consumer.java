package jms.testing.queue.receive;

import java.lang.invoke.MethodHandles;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jms.service.JmsService;
import jms.service.JmsServiceImpl;
import jms.testing.PerpetualRunnable;

import static jms.testing.Config.BROKER_URL;
import static jms.testing.Config.QUEUE_NAME;

public class Consumer {

    private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws JMSException {
        JmsService jmsService = new JmsServiceImpl(BROKER_URL);
        jmsService.initialize();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        PerpetualRunnable listener = createListener(jmsService);
        executorService.submit(listener);

        new Scanner(System.in).nextLine();
        LOGGER.info("trying to stop");
        listener.stop();
        jmsService.deinitialize();
        executorService.shutdown();
    }

    private static PerpetualRunnable createListener(JmsService jmsService) {
        return new PerpetualRunnable(() -> {
            try {
                Message message = jmsService.readQueueMessage(QUEUE_NAME);
                LOGGER.info("Received: {}", ((TextMessage) message).getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }
}
