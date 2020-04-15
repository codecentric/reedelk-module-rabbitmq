package com.reedelk.rabbitmq.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;

import static java.util.Optional.ofNullable;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@Shared
@Component(service = ConnectionConfiguration.class, scope = PROTOTYPE)
public class ConnectionConfiguration implements Implementor {

    @Property("Username")
    @Hint("guest")
    @Example("guest")
    @DefaultValue("guest")
    @Description("The AMQP user name to use when connecting to the broker.")
    private String userName;

    @Property("Password")
    @Hint("guest")
    @Example("guest")
    @DefaultValue("guest")
    @Description("The AMQP password to use when connecting to the broker.")
    private String password;

    @Property("Virtual Host")
    @Hint("/")
    @Example("/")
    @DefaultValue("/")
    @Description("The virtual host to use when connecting to the broker.")
    private String virtualHost;

    @Property("Host Name")
    @Hint("localhost")
    @DefaultValue("localhost")
    @Example("rabbitmq.domain.com")
    @Description("The host to use for connections to the broker.")
    private String hostName;

    @Property("Port Number")
    @Hint("5672")
    @Example("5672")
    @DefaultValue("5672")
    @Description("The port to use for connections to the broker.")
    private Integer portNumber;

    @Property("Automatic Recovery")
    @Example("true")
    @InitValue("true")
    @DefaultValue("false")
    @Description("If true automatic recovery of this connection " +
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

    public static String userName(ConnectionConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.userName))
                .orElse("guest");
    }

    public static String password(ConnectionConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.password))
                .orElse("guest");
    }

    public static String virtualHost(ConnectionConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.virtualHost))
                .orElse("/");
    }

    public static String hostName(ConnectionConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.hostName))
                .orElse("localhost");
    }

    public static int port(ConnectionConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.portNumber))
                .orElse(5672);
    }

    public static boolean isAutomaticRecovery(ConnectionConfiguration configuration) {
        return ofNullable(configuration)
                .flatMap(config -> ofNullable(config.automaticRecovery))
                .orElse(false);
    }
}
