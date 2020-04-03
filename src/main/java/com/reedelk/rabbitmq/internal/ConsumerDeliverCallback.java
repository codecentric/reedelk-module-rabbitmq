package com.reedelk.rabbitmq.internal;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import com.reedelk.rabbitmq.component.RabbitMQConsumer;
import com.reedelk.runtime.api.message.*;
import com.reedelk.runtime.api.message.content.MimeType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.runtime.api.commons.Preconditions.checkArgument;

abstract class ConsumerDeliverCallback implements DeliverCallback {

    private final MimeType consumedMessageMimeType;

    ConsumerDeliverCallback(MimeType consumedMessageMimeType) {
        checkArgument(consumedMessageMimeType != null, "consumedMessageMimeType");
        this.consumedMessageMimeType = consumedMessageMimeType;
    }

    @Override
    public void handle(String consumerTag, Delivery delivery) {
        // Message Content
        byte[] content = delivery.getBody();

        // Message Attributes
        MessageAttributes attributes = createAttributes(delivery);

        Message inboundMessage;

        // Convert the payload to a suitable type according to the mime type.
        if (String.class == consumedMessageMimeType.javaType()) {

            inboundMessage = MessageBuilder.get()
                    .withString(new String(content), consumedMessageMimeType)
                    .attributes(attributes)
                    .build();
        } else {
            inboundMessage = MessageBuilder.get()
                    .withBinary(content, consumedMessageMimeType)
                    .attributes(attributes)
                    .build();
        }

        // Notify event
        onEvent(inboundMessage, delivery);
    }

    protected abstract void onEvent(Message message, Delivery delivery);

    private MessageAttributes createAttributes(Delivery delivery) {
        Envelope envelope = delivery.getEnvelope();
        HashMap<String, Serializable> envelopeAttrs = new HashMap<>();
        setIfNotNull(RabbitMQConsumerAttribute.Envelope.deliveryTag(), envelope.getDeliveryTag(), envelopeAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Envelope.exchange(), envelope.getExchange(), envelopeAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Envelope.routingKey(), envelope.getRoutingKey(), envelopeAttrs);


        AMQP.BasicProperties properties = delivery.getProperties();
        HashMap<String, Serializable> propertiesAttrs = new HashMap<>();
        setIfNotNull(RabbitMQConsumerAttribute.Properties.contentType(), properties.getContentType(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.contentEncoding(), properties.getContentEncoding(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.headers(), properties.getHeaders(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.deliveryMode(), properties.getDeliveryMode(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.priority(), properties.getPriority(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.correlationId(), properties.getCorrelationId(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.replyTo(), properties.getReplyTo(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.expiration(), properties.getExpiration(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.messageId(), properties.getMessageId(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.timestamp(), properties.getTimestamp(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.type(), properties.getType(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.userId(), properties.getUserId(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.appId(), properties.getAppId(), propertiesAttrs);
        setIfNotNull(RabbitMQConsumerAttribute.Properties.clusterId(), properties.getClusterId(), propertiesAttrs);

        Map<String, Serializable> attributes = new HashMap<>();
        attributes.put(RabbitMQConsumerAttribute.Envelope.name(), envelopeAttrs);
        attributes.put(RabbitMQConsumerAttribute.Properties.name(), propertiesAttrs);

        // We copy the AMQP correlationId to the Internal Correlation ID and we set it
        // as a Message Attribute with the given key: MessageAttributeKey.CORRELATION_ID.
        // This allows to access the correlation ID from the flow context using 'context.correlationId'
        // from a script or dynamic value.
        if (propertiesAttrs.containsKey(RabbitMQConsumerAttribute.Properties.correlationId())) {
            attributes.put(MessageAttributeKey.CORRELATION_ID,
                    propertiesAttrs.get(RabbitMQConsumerAttribute.Properties.correlationId()));
        }

        return new DefaultMessageAttributes(RabbitMQConsumer.class, attributes);
    }

    private void setIfNotNull(String key, Serializable value, Map<String, Serializable> map) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private void setIfNotNull(String key, Map<String,Object> value, Map<String, Serializable> map) {
        if (value != null) {
            HashMap<String, Serializable> serializableOnlyValues = new HashMap<>();
            value.forEach((key1, value1) -> {
                if (value1 instanceof Serializable) {
                    serializableOnlyValues.put(key1, (Serializable) value1);
                }
            });
            map.put(key, serializableOnlyValues);
        }
    }
}
