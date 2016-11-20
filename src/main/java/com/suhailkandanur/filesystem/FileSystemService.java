package com.suhailkandanur.filesystem;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by suhail on 2016-11-19.
 */
@Service
public class FileSystemService {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemService.class);
    private Connection connection;
    private Channel channel;
    private Consumer consumer;

    @PostConstruct
    public void init() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        connection = factory.newConnection("localhost");
        channel = connection.createChannel();
        channel.exchangeDeclare("fresco", "fanout");
        final String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue, "fresco", "");
        consumer = new FileSystemRequestConsumer(channel);
        channel.basicConsume(queue, true, consumer);
    }

    @PreDestroy
    public void release() throws IOException, TimeoutException {
        if (channel != null && channel.isOpen()) channel.close();
        if (connection != null && connection.isOpen()) connection.close();
    }

    private static final class FileSystemRequestConsumer extends DefaultConsumer {

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public FileSystemRequestConsumer(Channel channel) {
            super(channel);
        }

        @Override
        @Async
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
            logger.info("received message: {}", new String(body));
        }
    }
}
