package com.reedelk.rabbitmq.internal.attribute;

import com.rabbitmq.client.AMQP;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.commons.SerializableUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.rabbitmq.internal.attribute.RabbitMQConsumerPropertiesAttributes.*;

@Type
@TypeProperty(name = CONTENT_ENCODING, type = String.class)
@TypeProperty(name = CORRELATION_ID, type = String.class)
@TypeProperty(name = DELIVERY_MODE, type = Integer.class)
@TypeProperty(name = CONTENT_TYPE, type = String.class)
@TypeProperty(name = EXPIRATION, type = String.class)
@TypeProperty(name = CLUSTER_ID, type = String.class)
@TypeProperty(name = MESSAGE_ID, type = String.class)
@TypeProperty(name = TIMESTAMP, type = long.class)
@TypeProperty(name = PRIORITY, type = Integer.class)
@TypeProperty(name = REPLY_TO, type = String.class)
@TypeProperty(name = HEADERS, type = Map.class)
@TypeProperty(name = USER_ID, type = String.class)
@TypeProperty(name = APP_ID, type = String.class)
@TypeProperty(name = TYPE, type = String.class)
public class RabbitMQConsumerPropertiesAttributes extends HashMap<String, Serializable> {

    static final String CONTENT_ENCODING = "contentEncoding";
    static final String CORRELATION_ID = "correlationId";
    static final String DELIVERY_MODE = "deliveryMode";
    static final String CONTENT_TYPE = "contentType";
    static final String EXPIRATION = "expiration";
    static final String CLUSTER_ID = "clusterId";
    static final String MESSAGE_ID = "messageId";
    static final String TIMESTAMP = "timestamp";
    static final String PRIORITY = "priority";
    static final String REPLY_TO = "replyTo";
    static final String HEADERS = "headers";
    static final String USER_ID = "userId";
    static final String APP_ID = "appId";
    static final String TYPE = "type";

    public RabbitMQConsumerPropertiesAttributes(AMQP.BasicProperties properties) {
        put(CONTENT_ENCODING, properties.getContentEncoding());
        put(CORRELATION_ID, properties.getCorrelationId());
        put(DELIVERY_MODE, properties.getDeliveryMode());
        put(CONTENT_TYPE, properties.getContentType());
        put(EXPIRATION, properties.getExpiration());
        put(CLUSTER_ID, properties.getClusterId());
        put(MESSAGE_ID, properties.getMessageId());
        put(TIMESTAMP, properties.getTimestamp().getTime());
        put(PRIORITY, properties.getPriority());
        put(REPLY_TO, properties.getReplyTo());
        put(HEADERS, SerializableUtils.asSafeSerializableMap(properties.getHeaders()));
        put(USER_ID, properties.getUserId());
        put(APP_ID, properties.getAppId());
        put(TYPE, properties.getType());
    }
}
