package com.reedelk.rabbitmq.component;

import com.reedelk.runtime.api.annotation.Collapsible;
import com.reedelk.runtime.api.annotation.Property;
import com.reedelk.runtime.api.annotation.Description;
import com.reedelk.runtime.api.annotation.When;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;

import static java.util.Optional.ofNullable;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@Collapsible
@Component(service = RabbitMQProducerQueueConfiguration.class, scope = PROTOTYPE)
public class RabbitMQProducerQueueConfiguration implements Implementor {

    @Property("Create new queue")
    @When(propertyName = "queueName", propertyValue = When.NOT_SCRIPT)
    @Description("If true, a queue with the name provided in the 'Queue Name' field will be created in the broker. " +
            "If false the queue is considered already defined in the broker and an error will be thrown if the" +
            " queue does not exists (default: false).")
    private Boolean create;

    @Property("Durable after restart")
    @When(propertyName = "create", propertyValue = "true")
    @Description("If true the queue will survive a server restart (default: false).")
    private Boolean durable;

    @Property("Exclusive to connection")
    @When(propertyName = "create", propertyValue = "true")
    @Description("If true the use of the queue will be restricted to this connection (default: false).")
    private Boolean exclusive;

    @Property("Auto Delete")
    @When(propertyName = "create", propertyValue = "true")
    @Description("If true the server will delete the queue when it is no longer in use (default: false).")
    private Boolean autoDelete;

    public void setDurable(Boolean durable) {
        this.durable = durable;
    }

    public void setExclusive(Boolean exclusive) {
        this.exclusive = exclusive;
    }

    public void setAutoDelete(Boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    public void setCreate(Boolean create) {
        this.create = create;
    }

    public static boolean isCreateNew(RabbitMQProducerQueueConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.create))
                .orElse(false);
    }

    public static boolean isDurable(RabbitMQProducerQueueConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.durable))
                .orElse(false);
    }

    public static boolean isExclusive(RabbitMQProducerQueueConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.exclusive))
                .orElse(false);
    }

    public static boolean isAutoDelete(RabbitMQProducerQueueConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.autoDelete))
                .orElse(false);
    }
}
