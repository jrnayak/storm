package com.ctm.bolt;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class is used for building bolts
 */
public class BoltBuilder {

	public Properties configs = null;
	@SuppressWarnings("rawtypes")
	protected Map stormConfig;

	public BoltBuilder(Properties configs) {
		this.configs = configs;
	}

	private String host;
	private String cassandraKeyspace;

	@SuppressWarnings("unchecked")
	public BaseCassandraBolt buildCassandraBolt() {
		stormConfig = new HashMap<String, String>();
		stormConfig.put(host, "52.209.68.249:9042");
		stormConfig.put(cassandraKeyspace, "test");
		return new BaseCassandraBolt(stormConfig);
	}

	// public HdfsBolt buildHdfsBolt() {
	// RecordFormat format = new
	// DelimitedRecordFormat().withFieldDelimiter("|");
	// SyncPolicy syncPolicy = new CountSyncPolicy(1);
	// FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(5.0f,
	// Units.MB);
	// FileNameFormat fileNameFormat = new
	// DefaultFileNameFormat().withPath(configs.getProperty(Keys.HDFS_FOLDER));
	// String port = configs.getProperty((Keys.HDFS_PORT));
	// String host = configs.getProperty((Keys.HDFS_HOST));
	// HdfsBolt bolt = new HdfsBolt()
	// .withFsUrl("hdfs://"+host+":"+port)
	// .withFileNameFormat(fileNameFormat)
	// .withRecordFormat(format)
	// .withRotationPolicy(rotationPolicy)
	// .withSyncPolicy(syncPolicy);
	// return bolt;
	// }

}
