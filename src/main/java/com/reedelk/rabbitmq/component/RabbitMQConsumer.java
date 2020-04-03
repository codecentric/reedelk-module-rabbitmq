package com.reedelk.rabbitmq.component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.reedelk.rabbitmq.internal.*;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.AbstractInbound;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.message.content.MimeType;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;

import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotBlank;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("RabbitMQ Consumer")
@Description("Consumes messages from a RabbitMQ broker queue whenever a message " +
                "is published to the subscribed queue. The component might be configured " +
                "to create the source queue if it does not exists already. " +
                "The RabbitMQ Consumer is an Inbound component and it can only be placed " +
                "at the beginning of a flow.")
@Component(service = RabbitMQConsumer.class, scope = PROTOTYPE)
public class RabbitMQConsumer extends AbstractInbound {

    @Property("Connection Config")
    private ConnectionFactoryConfiguration configuration;

    @Property("Connection URI")
    @Hint("amqp://guest:guest@localhost:5672")
    @InitValue("amqp://guest:guest@localhost:5672")
    @Example("amqp://guest:guest@localhost:5672")
    @When(propertyName = "configuration", propertyValue = When.NULL)
    @When(propertyName = "configuration", propertyValue = "{'ref': '" + When.BLANK + "'}")
    @Description("Configure a connection using the provided AMQP URI " +
            "containing the connection data.")
    private String connectionURI;

    @Property("Queue Name")
    @Hint("queue_inbound")
    @Example("queue_inbound")
    @Description("Defines the name of the queue this consumer will be consuming messages from.")
    private String queueName;

    @Property("Queue Configuration")
    @Group("Queue Configuration")
    private RabbitMQConsumerQueueConfiguration queueConfiguration;

    @Property("Content Mime Type")
    @MimeTypeCombo
    @Example(MimeType.MIME_TYPE_TEXT_PLAIN)
    @InitValue(MimeType.MIME_TYPE_TEXT_PLAIN)
    @DefaultValue(MimeType.MIME_TYPE_APPLICATION_BINARY)
    @Description("The Mime Type of the consumed content allows to create " +
            "a flow message with a suitable content type for the following flow components " +
            "(e.g a 'text/plain' mime type converts the consumed content to a string, " +
            "a 'application/octet-stream' keeps the consumed content as byte array).")
    private String messageMimeType;

    @Example("true")
    @InitValue("true")
    @DefaultValue("false")
    @Property("Auto Acknowledge")
    @Description("True to immediately consider messages delivered by the broker as soon as the flow starts." +
            " False to acknowledge the message only if the flow executed successfully.")
    private boolean autoAck;

    private Channel channel;
    private Connection connection;

    @Override
    public void onStart() {
        requireNotBlank(RabbitMQConsumer.class, queueName, "Queue Name must not be empty");
        if (configuration == null) {
            requireNotBlank(RabbitMQConsumer.class, connectionURI, "Connection URI must not be empty");
            connection = ConnectionFactoryProvider.from(connectionURI);
        } else {
            connection = ConnectionFactoryProvider.from(configuration);
        }

        try {
            channel = connection.createChannel();
            createQueueIfNeeded();
            MimeType queueMessageContentType = MimeType.parse(messageMimeType, MimeType.APPLICATION_BINARY);
            if (autoAck) {
                channel.basicConsume(queueName, true,
                        new ConsumerDeliverCallbackAutoAck( this, queueMessageContentType),
                        new ConsumerCancelCallback());
            } else {
                channel.basicConsume(queueName, false,
                        new ConsumerDeliverCallbackExplicitAck( this, queueMessageContentType, channel),
                        new ConsumerCancelCallback());
            }

        } catch (IOException e) {
            throw new ESBException(e);
        }
    }

    @Override
    public void onShutdown() {
        ChannelUtils.closeSilently(channel);
        ChannelUtils.closeSilently(connection);
    }

    public void setConfiguration(ConnectionFactoryConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public void setMessageMimeType(String messageMimeType) {
        this.messageMimeType = messageMimeType;
    }

    public void setQueueConfiguration(RabbitMQConsumerQueueConfiguration queueConfiguration) {
        this.queueConfiguration = queueConfiguration;
    }

    public void setConnectionURI(String connectionURI) {
        this.connectionURI = connectionURI;
    }

    public void setAutoAck(Boolean autoAck) {
        this.autoAck = autoAck;
    }

    private boolean shouldDeclareQueue() {
        return ofNullable(queueConfiguration)
                .flatMap(queueConfiguration ->
                        of(RabbitMQConsumerQueueConfiguration.isCreateNew(queueConfiguration)))
                .orElse(false);
    }

    private void createQueueIfNeeded() throws IOException {
        boolean shouldDeclareQueue = shouldDeclareQueue();
        if (shouldDeclareQueue) {
            boolean durable = RabbitMQConsumerQueueConfiguration.isDurable(queueConfiguration);
            boolean exclusive = RabbitMQConsumerQueueConfiguration.isExclusive(queueConfiguration);
            boolean autoDelete = RabbitMQConsumerQueueConfiguration.isAutoDelete(queueConfiguration);
            channel.queueDeclare(queueName, durable, exclusive, autoDelete, null);
        }
    }
}
