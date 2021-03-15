package de.codecentric.reedelk.rabbitmq.internal.exception;

import de.codecentric.reedelk.runtime.api.exception.PlatformException;

public class RabbitMQConsumerException extends PlatformException {

    public RabbitMQConsumerException(String message, Throwable exception) {
        super(message, exception);
    }
}
