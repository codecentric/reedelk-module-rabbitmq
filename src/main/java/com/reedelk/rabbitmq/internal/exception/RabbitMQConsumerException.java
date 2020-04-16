package com.reedelk.rabbitmq.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class RabbitMQConsumerException extends PlatformException {

    public RabbitMQConsumerException(String message, Throwable exception) {
        super(message, exception);
    }
}
