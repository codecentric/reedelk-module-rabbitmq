package com.reedelk.rabbitmq.configuration;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;

import static java.util.Optional.ofNullable;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@Collapsible
@Component(service = ConsumerQueueConfiguration.class, scope = PROTOTYPE)
public class ConsumerQueueConfiguration implements Implementor {

    @Example("true")
    @DefaultRenameMe("false")
    @When(propertyName = "queueName", propertyValue = When.NOT_SCRIPT)
    @Property("Create new queue")
    @PropertyDescription("If true, a queue with the name provided in the 'Queue Name' field will be created in the broker. " +
            "If false the queue is considered already defined in the broker and an error will be thrown if the" +
            " queue does not exists.")
    private Boolean create;

    @Example("true")
    @DefaultRenameMe("false")
    @When(propertyName = "create", propertyValue = "true")
    @Property("Durable after restart")
    @PropertyDescription("If true the queue will survive a server restart.")
    private Boolean durable;

    @Example("true")
    @DefaultRenameMe("false")
    @When(propertyName = "create", propertyValue = "true")
    @Property("Exclusive to connection")
    @PropertyDescription("If true the use of the queue will be restricted to this connection.")
    private Boolean exclusive;

    @Example("true")
    @DefaultRenameMe("false")
    @When(propertyName = "create", propertyValue = "true")
    @Property("Auto Delete")
    @PropertyDescription("If true the server will delete the queue when it is no longer in use.")
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

    public static boolean isCreateNew(ConsumerQueueConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.create))
                .orElse(false);
    }

    public static boolean isDurable(ConsumerQueueConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.durable))
                .orElse(false);
    }

    public static boolean isExclusive(ConsumerQueueConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.exclusive))
                .orElse(false);
    }

    public static boolean isAutoDelete(ConsumerQueueConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.autoDelete))
                .orElse(false);
    }
}
