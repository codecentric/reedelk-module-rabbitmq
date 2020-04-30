package com.reedelk.rabbitmq.internal.attribute;

import com.rabbitmq.client.Envelope;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;

import java.io.Serializable;
import java.util.HashMap;

import static com.reedelk.rabbitmq.internal.attribute.RabbitMQConsumerEnvelopeAttributes.*;

@Type
@TypeProperty(name = DELIVERY_TAG, type = long.class)
@TypeProperty(name = ROUTING_KEY, type = String.class)
@TypeProperty(name = EXCHANGE, type = String.class)
public class RabbitMQConsumerEnvelopeAttributes extends HashMap<String, Serializable> {

    static final String DELIVERY_TAG = "deliveryTag";
    static final String ROUTING_KEY = "routingKey";
    static final String EXCHANGE = "exchange";


    public RabbitMQConsumerEnvelopeAttributes(Envelope envelope) {
        put(DELIVERY_TAG, envelope.getDeliveryTag());
        put(ROUTING_KEY, envelope.getRoutingKey());
        put(EXCHANGE, envelope.getExchange());
    }
}
