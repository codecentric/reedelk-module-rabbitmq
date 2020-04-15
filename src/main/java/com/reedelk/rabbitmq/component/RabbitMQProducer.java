package com.reedelk.rabbitmq.component;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.reedelk.rabbitmq.internal.ChannelUtils;
import com.reedelk.rabbitmq.internal.ConnectionFactoryProvider;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.exception.PlatformException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.UUID;

import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotBlank;
import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("RabbitMQ Producer")
@Description("Sends the message payload to a RabbitMQ broker queue. " +
                "The component might be configured to create " +
                "the destination queue if it does not exists already.")
@Component(service = RabbitMQProducer.class, scope = PROTOTYPE)
public class RabbitMQProducer implements ProcessorSync {

    @DialogTitle("RabbitMQ Connection Factory")
    @Property("Connection Config")
    private ConnectionConfiguration configuration;

    @Property("Connection URI")
    @Description("Configure a connection using the provided AMQP URI " +
            "containing the connection data.")
    @Hint("amqp://guest:guest@localhost:5672")
    @InitValue("amqp://guest:guest@localhost:5672")
    @When(propertyName = "configuration", propertyValue = When.NULL)
    @When(propertyName = "configuration", propertyValue = "{'ref': '" + When.BLANK + "'}")
    private String connectionURI;

    @Property("Exchange Name")
    @Description("The name of the exchange to publish the message to. It might be a dynamic property.")
    @Hint("amq.fanout")
    private DynamicString exchangeName;

    @Property("Queue Name")
    @Hint("queue_outbound")
    private DynamicString queueName;

    @Property("Queue Configuration")
    @When(propertyName = "queueName", propertyValue = When.NOT_SCRIPT)
    private RabbitMQProducerQueueConfiguration queueConfiguration;

    @Reference
    private ConverterService converter;
    @Reference
    private ScriptEngineService scriptEngine;

    private Channel channel;
    private Connection connection;

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        String queueName = scriptEngine.evaluate(this.queueName, flowContext, message)
                .orElseThrow(() -> new PlatformException("Queue name not found"));

        String exchangeName = scriptEngine.evaluate(this.exchangeName, flowContext, message)
                .orElse(StringUtils.EMPTY);

        Object payload = message.payload();
        byte[] payloadAsBytes = converter.convert(payload, byte[].class);

        AMQP.BasicProperties messageProperties = createMessageProperties();

        try {
            synchronized (this) {
                // Only one Thread should publish data, otherwise we might have
                // out of order arrivals.
                channel.basicPublish(exchangeName, queueName, messageProperties, payloadAsBytes);
                return message;
            }
        } catch (IOException exception) {
            throw new PlatformException(exception);
        }
    }

    @Override
    public void initialize() {
        requireNotNull(RabbitMQProducer.class, queueName, "Queue Name must not be null");
        requireNotBlank(RabbitMQProducer.class, queueName.value(), "Queue Name must not be empty");
        if (configuration == null) {
            requireNotBlank(RabbitMQProducer.class, connectionURI, "Connection URI must not be empty");
            connection = ConnectionFactoryProvider.from(connectionURI);
        } else {
            connection = ConnectionFactoryProvider.from(configuration);
        }
        try {
            channel = connection.createChannel();
            createQueueIfNeeded();
        } catch (IOException exception) {
            throw new PlatformException(exception);
        }
    }

    @Override
    public void dispose() {
        ChannelUtils.closeSilently(channel);
        ChannelUtils.closeSilently(connection);
    }

    public void setConfiguration(ConnectionConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setConnectionURI(String connectionURI) {
        this.connectionURI = connectionURI;
    }

    public void setQueueName(DynamicString queueName) {
        this.queueName = queueName;
    }

    public void setExchangeName(DynamicString exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void setQueueConfiguration(RabbitMQProducerQueueConfiguration queueConfiguration) {
        this.queueConfiguration = queueConfiguration;
    }

    private boolean shouldDeclareQueue() {
        return ofNullable(queueConfiguration)
                .flatMap(producerQueueConfiguration ->
                        of(RabbitMQProducerQueueConfiguration.isCreateNew(producerQueueConfiguration)))
                .orElse(false);
    }

    private AMQP.BasicProperties createMessageProperties() {
        final String correlationId = UUID.randomUUID().toString();
        return new AMQP.BasicProperties
                .Builder()
                .correlationId(correlationId)
                .build();
    }

    private void createQueueIfNeeded() throws IOException {
        // If it is a script we cannot create it.
        if (queueName.isScript()) return;

        boolean shouldDeclareQueue = shouldDeclareQueue();
        if (shouldDeclareQueue) {
            boolean durable = RabbitMQProducerQueueConfiguration.isDurable(queueConfiguration);
            boolean exclusive = RabbitMQProducerQueueConfiguration.isExclusive(queueConfiguration);
            boolean autoDelete = RabbitMQProducerQueueConfiguration.isAutoDelete(queueConfiguration);
            channel.queueDeclare(queueName.value(), durable, exclusive, autoDelete, null);
        }
    }
}
