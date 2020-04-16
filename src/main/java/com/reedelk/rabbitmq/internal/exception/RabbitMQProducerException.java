package com.reedelk.rabbitmq.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class RabbitMQProducerException extends PlatformException {

    public RabbitMQProducerException(String message) {
        super(message);
    }

    public RabbitMQProducerException(String message, Throwable cause) {
        super(message, cause);
    }
}
