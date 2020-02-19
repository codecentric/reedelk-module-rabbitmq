package com.reedelk.rabbitmq.configuration;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;

import static java.util.Optional.ofNullable;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@Shared
@Component(service = ConnectionFactoryConfiguration.class, scope = PROTOTYPE)
public class ConnectionFactoryConfiguration implements Implementor {

    @Hint("guest")
    @Example("guest")
    @DefaultRenameMe("guest")
    @Property("Username")
    @PropertyDescription("The AMQP user name to use when connecting to the broker.")
    private String userName;

    @Hint("guest")
    @Example("guest")
    @DefaultRenameMe("guest")
    @Property("Password")
    @PropertyDescription("The AMQP password to use when connecting to the broker.")
    private String password;

    @Hint("/")
    @Example("/")
    @DefaultRenameMe("/")
    @Property("Virtual Host")
    @PropertyDescription("The virtual host to use when connecting to the broker.")
    private String virtualHost;

    @Hint("localhost")
    @Example("rabbitmq.domain.com")
    @DefaultRenameMe("localhost")
    @Property("Host Name")
    @PropertyDescription("The host to use for connections to the broker.")
    private String hostName;

    @Hint("5672")
    @Example("5672")
    @DefaultRenameMe("5672")
    @Property("Port Number")
    @PropertyDescription("The port to use for connections to the broker.")
    private Integer portNumber;

    @InitValue("true")
    @Example("true")
    @DefaultRenameMe("false")
    @Property("Automatic Recovery")
    @PropertyDescription("If true automatic recovery of this connection " +
            "is performed when the network connection with the server fails.")
    private Boolean automaticRecovery;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }

    public void setAutomaticRecovery(Boolean automaticRecovery) {
        this.automaticRecovery = automaticRecovery;
    }

    public static String userName(ConnectionFactoryConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.userName))
                .orElse("guest");
    }

    public static String password(ConnectionFactoryConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.password))
                .orElse("guest");
    }

    public static String virtualHost(ConnectionFactoryConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.virtualHost))
                .orElse("/");
    }

    public static String hostName(ConnectionFactoryConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.hostName))
                .orElse("localhost");
    }

    public static int port(ConnectionFactoryConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.portNumber))
                .orElse(5672);
    }

    public static boolean isAutomaticRecovery(ConnectionFactoryConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.automaticRecovery))
                .orElse(false);
    }
}
