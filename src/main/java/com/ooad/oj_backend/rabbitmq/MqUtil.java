package com.ooad.oj_backend.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MqUtil {
    public static final String REQUEST_QUEUE = "requestQueue";
    public static final String RESPONSE_QUEUE = "responseQueue";
    private static Connection connection = null;

//    public static Connection getConnection(String host, int port, String name, String password) throws IOException, TimeoutException {
//        if (connection == null) {
//            ConnectionFactory factory = new ConnectionFactory();
//            factory.setHost(host);
//            factory.setPort(port);
//            factory.setUsername(name);
//            factory.setPassword(password);
//            connection = factory.newConnection();
//        }
//        return connection;
//    }

    public static Connection getConnection(String host, int port) throws IOException, TimeoutException {
        if (connection == null) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername("root");
            factory.setPassword("123456");
            connection = factory.newConnection();
        }
        return connection;
    }

}
