package com.reedelk.rabbitmq.internal.attribute;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributeKey;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.io.Serializable;
import java.util.HashMap;

import static com.reedelk.rabbitmq.internal.attribute.RabbitMQConsumerAttributes.ENVELOPE;
import static com.reedelk.rabbitmq.internal.attribute.RabbitMQConsumerAttributes.PROPERTIES;

@Type
@TypeProperty(name = PROPERTIES, type = PropertiesAttributes.class)
@TypeProperty(name = ENVELOPE, type = EnvelopeAttributes.class)
public class RabbitMQConsumerAttributes extends MessageAttributes {

    static final String PROPERTIES = "properties";
    static final String ENVELOPE = "envelope";

    public RabbitMQConsumerAttributes(Delivery delivery) {
        Envelope envelope = delivery.getEnvelope();
        HashMap<String, Serializable> envelopeAttrs = new EnvelopeAttributes(envelope);

        AMQP.BasicProperties properties = delivery.getProperties();
        HashMap<String, Serializable> propertiesAttrs = new PropertiesAttributes(properties);


        put(ENVELOPE, envelopeAttrs);
        put(PROPERTIES, propertiesAttrs);

        // We copy the AMQP correlationId to the Internal Correlation ID and we set it
        // as a Message Attribute with the given key: MessageAttributeKey.CORRELATION_ID.
        // This allows to access the correlation ID from the flow context using 'context.correlationId'
        // from a script or dynamic value.
        if (propertiesAttrs.containsKey(PropertiesAttributes.CORRELATION_ID)) {
            put(MessageAttributeKey.CORRELATION_ID,
                    propertiesAttrs.get(PropertiesAttributes.CORRELATION_ID));
        }
    }
}
