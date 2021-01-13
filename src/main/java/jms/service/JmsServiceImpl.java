package jms.service;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsServiceImpl implements JmsService {
    private String brokerUrl;
    private QueueConnection queueConnection;
    private TopicConnection topicConnection;

    public JmsServiceImpl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    @Override
    public void initialize() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        queueConnection = (QueueConnection) connectionFactory.createConnection();
        topicConnection = (TopicConnection) connectionFactory.createConnection();
        queueConnection.start();
        topicConnection.start();
    }

    @Override
    public void sendMessage(Message message, String queueName) throws JMSException {
        QueueSession queueSession = null;
        QueueSender queueSender = null;
        try {
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = queueSession.createQueue(queueName);
            queueSender = queueSession.createSender(queue);

            queueSender.send(message);
        } finally {
            if (queueSender != null) {
                queueSender.close();
            }
            if (queueSession != null) {
                queueSession.close();
            }
        }
    }

    @Override
    public Message readQueueMessage(String queueName) throws JMSException {
        return readQueueMessage(queueName, MessageConsumer::receive);
    }

    @Override
    public Message readQueueMessage(String queueName, long timeout) throws JMSException {
        return readQueueMessage(queueName, consumer -> consumer.receive(timeout));
    }

    private Message readQueueMessage(String queueName, MessageExtractor messageExtractor) throws JMSException {
        QueueSession queueSession = null;
        QueueReceiver queueReceiver = null;

        try {
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = queueSession.createQueue(queueName);
            queueReceiver = queueSession.createReceiver(queue);

            return messageExtractor.apply(queueReceiver);
        } finally {
            if (queueReceiver != null) {
                queueReceiver.close();
            }
            if (queueSession != null) {
                queueSession.close();
            }
        }
    }

    @Override
    public AutoCloseable subscribeToQueue(MessageListener messageListener, String queueName) throws JMSException {
        QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = queueSession.createQueue(queueName);
        QueueReceiver queueReceiver = queueSession.createReceiver(queue);
        queueReceiver.setMessageListener(messageListener);
        return new OpenSubscription(queueSession, queueReceiver);
    }

    @Override
    public void publishMessage(Message message, String topicName) throws JMSException {
        TopicSession topicSession = null;
        TopicPublisher publisher = null;
        try {
            topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = topicSession.createTopic(topicName);
            publisher = topicSession.createPublisher(topic);
            publisher.publish(message);
        } finally {
            if (publisher != null) {
                publisher.close();
            }

            if (topicSession != null) {
                topicSession.close();
            }
        }
    }

    @Override
    public Message readTopicMessage(String topicName) throws JMSException {
        return readTopicMessage(topicName, MessageConsumer::receive);
    }

    @Override
    public Message readTopicMessage(String topicName, long timeout) throws JMSException {
        return readTopicMessage(topicName, consumer -> consumer.receive(timeout));
    }

    private Message readTopicMessage(String topicName, MessageExtractor messageExtractor) throws JMSException {
        TopicSession topicSession = null;
        TopicSubscriber topicSubscriber = null;

        try {
            topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = topicSession.createTopic(topicName);
            topicSubscriber = topicSession.createSubscriber(topic);

            return messageExtractor.apply(topicSubscriber);
        } finally {
            if (topicSubscriber != null) {
                topicSubscriber.close();
            }
            if (topicSession != null) {
                topicSession.close();
            }
        }
    }

    @Override
    public AutoCloseable subscribeToTopic(MessageListener messageListener, String topicName) throws JMSException {
        TopicSession topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = topicSession.createTopic(topicName);
        TopicSubscriber subscriber = topicSession.createSubscriber(topic);
        subscriber.setMessageListener(messageListener);
        return new OpenSubscription(topicSession, subscriber);
    }

    @Override
    public void deinitialize() throws JMSException {
        if (queueConnection != null) {
            queueConnection.close();
        }

        if (topicConnection != null) {
            topicConnection.close();
        }
    }

    @FunctionalInterface
    private interface MessageExtractor {
        Message apply(MessageConsumer receiver) throws JMSException;
    }

    private static class OpenSubscription implements AutoCloseable {
        Session topicSession;
        MessageConsumer consumer;

        OpenSubscription(Session session, MessageConsumer consumer) {
            this.topicSession = session;
            this.consumer = consumer;
        }

        @Override
        public void close() throws Exception {
            if (topicSession != null) {
                topicSession.close();
            }

            if (consumer != null) {
                consumer.close();
            }
        }
    }
}
