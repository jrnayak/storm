package com.ctm.storm;

import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

import com.ctm.bolt.BaseCassandraBolt;
import com.ctm.bolt.BoltBuilder;
import com.ctm.spout.SpoutBuilder;
import com.ctm.common.Util;

/**
 * @author Ranjan This is the main topology class. All the spouts and bolts are
 *         linked together and is submitted on to the cluster
 */
public class Topology {

	public Properties config;
	public SpoutBuilder spoutBuilder;
	public BoltBuilder boltBuilder;

	public Topology(String configFile) throws Exception {
		config = new Properties();
		try {
			config.load(Topology.class.getResourceAsStream("/config.properties"));
			spoutBuilder = new SpoutBuilder(config);
			boltBuilder = new BoltBuilder(config);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}

	private void submitTopology() throws Exception {
		// Build Storm Topology
		TopologyBuilder builder = new TopologyBuilder();
		KafkaSpout kafkaSpout = spoutBuilder.buildKafkaSpout();
		BaseCassandraBolt cassandraBolt = boltBuilder.buildCassandraBolt();
		// CountBolt countBolt = boltBuilder.buildCountBolt();

		// set the kafkaSpout to topology
		// parallelism-hint for kafkaSpout - defines number of executors/threads
		// to be spawn per container
		int kafkaSpoutCount = Integer.parseInt(config.getProperty(Util.KAFKA_SPOUT_COUNT));
		builder.setSpout(config.getProperty(Util.KAFKA_SPOUT_ID), kafkaSpout, kafkaSpoutCount);

		// // set the lineSplit bolt
		// int wordSplitBoltCount =
		// Integer.parseInt(config.getProperty(Util.SPLIT_BOLT_COUNT));
		// builder.setBolt(config.getProperty(Util.SPLIT_BOLT_ID),
		// wordSplitBolt, wordSplitBoltCount)
		// .shuffleGrouping(config.getProperty(Util.KAFKA_SPOUT_ID));

		// set the Cassandra Count bolt
		int cassandraBoltCount = Integer.parseInt(config.getProperty(Util.CASSANDRA_BOLT_COUNT));
		builder.setBolt(config.getProperty(Util.CASSANDRA_BOLT_ID), cassandraBolt, cassandraBoltCount)
				.shuffleGrouping(config.getProperty(Util.KAFKA_SPOUT_ID));

		// // set the mongodb bolt
		// int mongoBoltCount =
		// Integer.parseInt(config.getProperty(Keys.MONGO_BOLT_COUNT));
		// builder.setBolt(config.getProperty(Keys.MONGO_BOLT_ID), mongoBolt,
		// mongoBoltCount)
		// .shuffleGrouping(config.getProperty(Keys.SINK_TYPE_BOLT_ID),
		// MONGODB_STREAM);

		Config conf = new Config();
		// conf.put("solr.zookeeper.hosts",
		// config.getProperty(Util.SOLR_ZOOKEEPER_HOSTS));
		String topologyName = config.getProperty(Util.TOPOLOGY_NAME);
		// Defines how many worker processes have to be created for the topology
		// in the cluster.
		// conf.setNumWorkers(1);
		StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
	}

	public static void main(String[] args) throws Exception {
		String configFile;
		if (args.length == 0) {
			System.out.println("Missing input : config file location, using default");
			configFile = "config.properties";

		} else {
			configFile = args[0];
		}

		// Submit Storm Topology
		Topology ingestionTopology = new Topology(configFile);
		ingestionTopology.submitTopology();
	}
}
