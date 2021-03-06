package com.idea.tools.jms;

import com.idea.tools.dto.MessageDto;
import com.idea.tools.dto.ServerDto;
import com.idea.tools.dto.ZookeeperConfiguration;
import com.idea.tools.utils.Assert;
import io.confluent.kafka.jms.JMSClientConfig;
import io.confluent.kafka.jms.MyKafkaConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import java.util.Optional;
import java.util.Properties;

import static com.idea.tools.dto.ServerType.KAFKA;

public class KafkaConnectionStrategy extends AbstractConnectionStrategy {

	@Override
	public Connection connect(ServerDto server) throws Exception {
		validate(server);
		ZookeeperConfiguration zookeeper = server.getZookeeperConfiguration();
		Assert.equals(server.getType(), KAFKA, "Unsupported server type %s", server.getType());
		Assert.notNull(zookeeper, "Zookeeper configuration must not be null");
		Assert.notBlank(zookeeper.getClientId(), "Client id must not be empty");
		Assert.notBlank(zookeeper.getZookeeperHost(), "Zookeeper host must not be empty");
		Assert.notNull(zookeeper.getZookeeperPort(), "Zookeeper port must not be null");

		String kafkaUrl = String.format("%s:%s", server.getHost(), server.getPort());
		String zookeeperUrl = String.format("%s:%s", zookeeper.getZookeeperHost(), zookeeper.getZookeeperPort());

		Properties settings = new Properties();
		settings.put(JMSClientConfig.CLIENT_ID_CONFIG, zookeeper.getClientId());
		settings.put(JMSClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
		settings.put(JMSClientConfig.ZOOKEEPER_CONNECT_CONF, zookeeperUrl);

		ConnectionFactory factory = new MyKafkaConnectionFactory(settings);

		return factory.createConnection();
	}


	@Override
	public Optional<MessageDto> map(Message message) {
		return Optional.empty();
	}

}
