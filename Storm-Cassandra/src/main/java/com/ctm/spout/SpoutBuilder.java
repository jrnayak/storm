package com.ctm.spout;

import java.util.Properties;

import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;

import com.ctm.common.Util;

public class SpoutBuilder {

	public Properties configs = null;

	public SpoutBuilder(Properties configs) {
		this.configs = configs;
	}

	public KafkaSpout buildKafkaSpout() {
		// ZooKeeper connection string
		BrokerHosts hosts = new ZkHosts(configs.getProperty(Util.KAFKA_ZOOKEEPER));
		String topic = configs.getProperty(Util.KAFKA_TOPIC);
		String zkRoot = configs.getProperty(Util.KAFKA_ZKROOT);
		String groupId = configs.getProperty(Util.KAFKA_CONSUMERGROUP);
		// Creating SpoutConfig Object
		SpoutConfig spoutConfig = new SpoutConfig(hosts, topic, zkRoot, groupId);

		// convert the ByteBuffer to String.
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		// Assign SpoutConfig to KafkaSpout.
		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
		return kafkaSpout;
	}
}
