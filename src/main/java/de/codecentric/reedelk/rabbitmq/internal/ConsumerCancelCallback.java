package de.codecentric.reedelk.rabbitmq.internal;

import com.rabbitmq.client.CancelCallback;

import java.io.IOException;

public class ConsumerCancelCallback implements CancelCallback {
    @Override
    public void handle(String consumerTag) throws IOException {
        // nothing to do
    }
}
