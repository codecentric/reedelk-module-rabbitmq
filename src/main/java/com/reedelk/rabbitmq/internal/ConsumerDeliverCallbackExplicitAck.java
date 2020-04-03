package com.reedelk.rabbitmq.internal;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import com.reedelk.runtime.api.component.InboundEventListener;
import com.reedelk.runtime.api.component.OnResult;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.reedelk.runtime.api.commons.Preconditions.checkArgument;

public class ConsumerDeliverCallbackExplicitAck extends ConsumerDeliverCallback {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerDeliverCallbackExplicitAck.class);

    private final Channel channel;
    private final InboundEventListener listener;

    public ConsumerDeliverCallbackExplicitAck(InboundEventListener listener, MimeType consumedMessageMimeType, Channel channel) {
        super(consumedMessageMimeType);
        checkArgument(listener != null, "listener");
        checkArgument(channel != null, "channel");
        this.listener = listener;
        this.channel = channel;
    }

    @Override
    protected void onEvent(Message message, Delivery delivery) {
        final long deliveryTag = delivery.getEnvelope().getDeliveryTag();
        listener.onEvent(message, new OnResult() {
            @Override
            public void onResult(FlowContext flowContext, Message message) {
                try {
                    channel.basicAck(deliveryTag, false);
                } catch (IOException exchange) {
                    String errorMessage = String.format("An error occurred while sending ack for tag=[%d]: %s", deliveryTag, exchange.getMessage());
                    logger.error(errorMessage, exchange);
                }
            }
        });
    }
}
