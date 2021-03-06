package io.confluent.kafka.jms;

import org.apache.kafka.common.serialization.ByteArraySerializer;

import javax.jms.Connection;
import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MyKafkaConnectionFactory extends KafkaConnectionFactory {

    public MyKafkaConnectionFactory(Properties properties) {
        super(properties);
    }

    @Override
    public Connection createConnection() throws JMSException {
        KafkaConnection connection = (KafkaConnection) super.createConnection();
        Map<String, Object> producerProperties = new HashMap<>(this.config.producer);
        producerProperties.put("value.serializer", ByteArraySerializer.class);
        producerProperties.put("key.serializer", ByteArraySerializer.class);
        connection.producerFactory = new ProducerFactoryImpl(producerProperties);
        return connection;
    }

}
