package de.codecentric.reedelk.rabbitmq.internal.attribute;

import com.rabbitmq.client.Envelope;
import de.codecentric.reedelk.runtime.api.annotation.Type;
import de.codecentric.reedelk.runtime.api.annotation.TypeProperty;

import java.io.Serializable;
import java.util.HashMap;

import static de.codecentric.reedelk.rabbitmq.internal.attribute.EnvelopeAttributes.*;

@Type(displayName = "EnvelopeAttributes", mapKeyType = String.class, mapValueType = Serializable.class)
@TypeProperty(name = DELIVERY_TAG, type = long.class)
@TypeProperty(name = ROUTING_KEY, type = String.class)
@TypeProperty(name = EXCHANGE, type = String.class)
public class EnvelopeAttributes extends HashMap<String, Serializable> {

    static final String DELIVERY_TAG = "deliveryTag";
    static final String ROUTING_KEY = "routingKey";
    static final String EXCHANGE = "exchange";

    public EnvelopeAttributes(Envelope envelope) {
        put(DELIVERY_TAG, envelope.getDeliveryTag());
        put(ROUTING_KEY, envelope.getRoutingKey());
        put(EXCHANGE, envelope.getExchange());
    }
}
