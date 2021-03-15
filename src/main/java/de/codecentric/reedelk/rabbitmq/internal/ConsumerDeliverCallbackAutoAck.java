package de.codecentric.reedelk.rabbitmq.internal;

import com.rabbitmq.client.Delivery;
import de.codecentric.reedelk.runtime.api.component.InboundEventListener;
import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.message.content.MimeType;

import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkArgument;

public class ConsumerDeliverCallbackAutoAck extends ConsumerDeliverCallback {

    private final InboundEventListener listener;

    public ConsumerDeliverCallbackAutoAck(InboundEventListener listener, MimeType consumedMessageMimeType) {
        super(consumedMessageMimeType);
        checkArgument(listener != null, "listener");
        this.listener = listener;
    }

    @Override
    protected void onEvent(Message message, Delivery delivery) {
        // Notify Event
        listener.onEvent(message);
    }
}
