package com.suhailkandanur.messaging.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.suhailkandanur.messaging.Publisher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by suhail on 2016-11-19.
 */
@Component
public class RabbitPublisher implements Publisher, InitializingBean {

    private Connection connection;
    private Channel channel;

    @Override
    public void afterPropertiesSet() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    @Override
    public void publishDirect(String exchangeName, String routingKey, byte[] body) throws IOException {
        channel.exchangeDeclare(exchangeName, "direct");
        channel.basicPublish(exchangeName, routingKey, null, body);
    }

    @Override
    public void publishBroadcast(String exchange, byte[] body) throws IOException {
        channel.exchangeDeclare(exchange, "fanout");
        channel.basicPublish(exchange, "", null, body);
    }
}
