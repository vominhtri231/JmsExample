package jms.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public interface JmsService {

    void initialize() throws JMSException;

    void sendMessage(Message message, String queueName) throws JMSException;

    Message readQueueMessage(String queueName) throws JMSException;

    Message readQueueMessage(String queueName, long timeout) throws JMSException;

    AutoCloseable subscribeToQueue(MessageListener messageListener, String queueName) throws JMSException;

    void publishMessage(Message message, String topicName) throws JMSException;

    Message readTopicMessage(String topicName) throws JMSException;

    Message readTopicMessage(String topicName, long timeout) throws JMSException;

    AutoCloseable subscribeToTopic(MessageListener messageListener, String topicName) throws JMSException;

    void deinitialize() throws JMSException;
}
