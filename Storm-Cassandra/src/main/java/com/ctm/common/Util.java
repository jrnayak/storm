package com.ctm.common;

/**
 * This is an utility class. It contains the keys that should be present in the
 * input config-file
 */
public class Util {

	public static final String TOPOLOGY_NAME = "topology";

	// kafka spout
	public static final String KAFKA_SPOUT_ID = "kafka_spout";
	public static final String KAFKA_ZOOKEEPER = "kafka_zookeeper_address";
	public static final String KAFKA_TOPIC = "kafa_topic";
	public static final String KAFKA_ZKROOT = "kafka_zkRoot";
	public static final String KAFKA_CONSUMERGROUP = "kafka_consumer_group";
	public static final String KAFKA_SPOUT_COUNT = "kafkaspout_count";

	// Bolts
	public static final String CASSANDRA_BOLT_ID = "cassandra_bolt";
	public static final String CASSANDRA_BOLT_COUNT = "cassandrabolt_count";
	// public static final String SPLIT_BOLT_ID = "split-bolt";
	// public static final String SPLIT_BOLT_COUNT = "splitbolt.count";
}
