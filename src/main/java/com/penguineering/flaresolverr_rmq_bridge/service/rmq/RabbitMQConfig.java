package com.penguineering.flaresolverr_rmq_bridge.service.rmq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    @Value("${flaresolverr-rmq-bridge.service.queue-flaresolverr-requests}")
    private String queueFlaresolverrRequests;

    @Bean
    public Queue flareSolverrQueue() {
        return QueueBuilder.durable(queueFlaresolverrRequests)
                .build();
    }

    @Bean
    public SimpleMessageListenerContainer flareSolverrContainer(ConnectionFactory connectionFactory,
                                                                FlareSolverrRequestHandler handler) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueFlaresolverrRequests);
        container.setMessageListener(handler);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setChannelTransacted(true);
        return container;
    }
}
