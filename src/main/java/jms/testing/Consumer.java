package jms.testing;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import jms.service.JmsService;
import jms.service.JmsServiceImpl;

import static jms.testing.Config.BROKER_URL;
import static jms.testing.Config.QUEUE_NAME;

public class Consumer {

    public static void main(String[] args) throws JMSException, IOException {
        JmsService jmsService = new JmsServiceImpl(BROKER_URL);
        jmsService.initialize();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        PerpetualRunnable listener = createListener(jmsService);
        executorService.submit(listener);

        new Scanner(System.in).nextLine();
        System.out.println("trying to stop");
        listener.stop();
        jmsService.deinitialize();
        executorService.shutdown();
    }

    private static PerpetualRunnable createListener(JmsService jmsService) {
        return new PerpetualRunnable(() -> {
            try {
                Message message = jmsService.readMessage(QUEUE_NAME);
                System.out.println("Received: " + ((TextMessage) message).getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }
}
