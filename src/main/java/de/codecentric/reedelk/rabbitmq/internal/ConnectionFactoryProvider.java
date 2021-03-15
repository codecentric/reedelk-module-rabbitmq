package de.codecentric.reedelk.rabbitmq.internal;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import de.codecentric.reedelk.rabbitmq.component.ConnectionConfiguration;
import de.codecentric.reedelk.runtime.api.exception.PlatformException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class ConnectionFactoryProvider {

    public static Connection from(String uri) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            connectionFactory.setUri(uri);
            return connectionFactory.newConnection();
        } catch (IOException |
                TimeoutException |
                NoSuchAlgorithmException |
                KeyManagementException |
                URISyntaxException exception) {
            throw new PlatformException(exception);
        }
    }

    public static Connection from(ConnectionConfiguration configuration) {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUsername(ConnectionConfiguration.userName(configuration));
            factory.setPassword(ConnectionConfiguration.password(configuration));
            factory.setVirtualHost(ConnectionConfiguration.virtualHost(configuration));
            factory.setHost(ConnectionConfiguration.hostName(configuration));
            factory.setPort(ConnectionConfiguration.port(configuration));
            factory.setAutomaticRecoveryEnabled(ConnectionConfiguration.isAutomaticRecovery(configuration));
            return factory.newConnection();
        } catch (IOException | TimeoutException e) {
            throw new PlatformException(e);
        }
    }
}
