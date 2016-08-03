package com.ctm.bolt;

import org.apache.storm.cassandra.client.SimpleClient;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;

public class CassandraClient implements SimpleClient {

	private static Session session = null;
	private Cluster cluster = null;

	@Override
	public Session connect() {
		cluster = Cluster.builder().addContactPoint("52.209.68.249").build();
		System.out.println("load cassandra ip");
		session = cluster.connect();
		Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n", metadata.getClusterName().toString());
		System.out.println("CassandraCounterBolt prepare method ended");
		return session;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isClose() {
		// TODO Auto-generated method stub
		return false;
	}

}
