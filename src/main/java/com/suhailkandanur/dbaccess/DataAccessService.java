package com.suhailkandanur.dbaccess;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQBasicProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by suhail on 2016-11-19.
 */
@Service
public class DataAccessService {

    private static final Logger logger = LoggerFactory.getLogger(DataAccessService.class);
    private Connection connection;
    private Channel channel;
    private  Consumer consumer;

    @PostConstruct
    public void init() throws IOException, TimeoutException {
        logger.info("initializing dataaccess service");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare("fresco", "fanout");
        final String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "fresco", "");
        consumer = new DataAccessRequestConsumer(channel);
        channel.basicConsume(queueName, true, consumer);
    }

    private static class DataAccessRequestConsumer extends DefaultConsumer {

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public DataAccessRequestConsumer(Channel channel) {
            super(channel);
        }

        @Override
        @Async
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
            logger.info("received message: {}", new String(body));
        }
    }

}
