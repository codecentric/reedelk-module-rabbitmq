package com.reedelk.rabbitmq.internal.commons;

import com.reedelk.runtime.api.commons.FormattedMessage;

public class Messages {

    private Messages() {
    }

    public enum RabbitMQProducer implements FormattedMessage {

        CREATE_CHANNEL_ERROR("The channel could not be created, cause=[%s]."),
        PUBLISH_MESSAGE_ERROR("The message could not be published to the queue (Queue name=[%s])."),
        QUEUE_EMPTY_ERROR("The queue name must not be empty (DynamicValue=[%s]).");

        private final String message;

        RabbitMQProducer(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }

    public enum RabbitMQConsumer implements FormattedMessage {

        CONSUME_ERROR("An error occurred while consuming message from queue=[%s], cause=[%s].");

        private final String message;

        RabbitMQConsumer(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }
}
