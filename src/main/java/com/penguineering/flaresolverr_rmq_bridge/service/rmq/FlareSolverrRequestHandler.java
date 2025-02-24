package com.penguineering.flaresolverr_rmq_bridge.service.rmq;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.penguineering.flaresolverr_rmq_bridge.service.flaresolverr.FlareSolverrBackend;
import com.penguineering.flaresolverr_rmq_bridge.service.flaresolverr.FlareSolverrResponse;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

@Component
public class FlareSolverrRequestHandler implements ChannelAwareMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(FlareSolverrRequestHandler.class);

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final FlareSolverrBackend flareSolverrBackend;

    public FlareSolverrRequestHandler(ObjectMapper objectMapper,
                                      RabbitTemplate rabbitTemplate,
                                      FlareSolverrBackend flareSolverrBackend) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.flareSolverrBackend = flareSolverrBackend;
    }

    @Override
    public void onMessage(Message message, Channel channel) {
        try {
            logger.info("Received message: {}", message);

            // Extract the correlation ID
            final Optional<String> correlationId = getCorrelationId(message);
            correlationId.ifPresentOrElse(
                    id -> logger.info("Received a message with Correlation ID: {}", id),
                    () -> logger.warn("Received a message without Correlation ID")
            );

            // Extract the "reply_to" property
            String replyTo = Optional
                    .ofNullable(message.getMessageProperties())
                    .map(MessageProperties::getReplyTo)
                    .orElseThrow(() -> new IllegalArgumentException("Reply_to property is missing"));
            logger.info("Reply-to: {}", replyTo);

            // Convert the message body to a FlareSolverr Request Message
            FlareSolverrRequestMessage requestMessage;
            try {
                requestMessage = objectMapper.readValue(message.getBody(), FlareSolverrRequestMessage.class);

                if (Objects.isNull(requestMessage.request()))
                    throw new IllegalArgumentException("Request not specified");
            } catch (JsonParseException e) {
                throw new IllegalArgumentException("Failed to parse message body", e);
            }

            FlareSolverrResponse response = flareSolverrBackend
                    .request(requestMessage.request())
                    .blockOptional()
                    .orElseThrow(() -> new IOException("FlareSolverr backend failed to process request"));

            String responseJson = objectMapper.writeValueAsString(response);

            // Send the result
            MessageProperties messageProperties = new MessageProperties();
            correlationId.ifPresent(messageProperties::setCorrelationId);
            messageProperties.setContentType("text/json");
            Message responseMessage = new Message(responseJson.getBytes(StandardCharsets.UTF_8), messageProperties);

            rabbitTemplate.send(replyTo, responseMessage);

            // Done
            ackMessage(message, channel);
        } catch (IllegalArgumentException e) {
            logger.info("Illegal argument exception on message handling: ", e);
            nackMessage(message, channel, false);
        } catch (IOException e) {
            logger.error("IO Exception on message handling: ", e);
            nackMessage(message, channel, true);
        }
    }

    private Optional<String> getCorrelationId(Message message) {
        return Optional
                .ofNullable(message.getMessageProperties())
                .map(MessageProperties::getCorrelationId);
    }

    private void ackMessage(Message message, Channel channel) {
        if (Objects.nonNull(channel))
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                channel.txCommit();
            } catch (IOException e) {
                logger.error("Failed to ack message", e);
            }
    }

    private void nackMessage(Message message, Channel channel, boolean requeue) {
        if (Objects.nonNull(channel))
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, requeue);
                channel.txCommit();
            } catch (IOException e) {
                logger.error("Failed to nack message", e);
            }
    }
}
