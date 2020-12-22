package jms.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public interface JmsService {

    void initialize() throws JMSException;

    void sendMessage(Message message, String queueName) throws JMSException;

    Message readMessage(String queueName) throws JMSException;

    Message readMessage(String queueName, long timeout) throws JMSException;

    void publishMessage(Message message, String topicName) throws JMSException;

    AutoCloseable subscribeToTopic(MessageListener messageListener, String topicName) throws JMSException;

    void deinitialize() throws JMSException;
}
