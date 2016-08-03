package com.ctm.bolt;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * A base cassandra bolt.
 *
 * Default {@link org.apache.storm.topology.base.BaseRichBolt}
 */
public class BaseCassandraBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	protected OutputCollector outputCollector;
	protected Map stormConfig;
	private static final Logger logger = Logger.getLogger(BaseCassandraBolt.class);
	private static Session session = null;
	String cassandraURL;
	JSONObject eventJson = null;
	String eventName = null;
	String eventId = null;
	com.datastax.driver.core.ResultSet segmentlistResult = null;
	com.datastax.driver.core.ResultSet newCountUpdatedResult = null;

	Row row = null;

	@SuppressWarnings("rawtypes")
	protected BaseCassandraBolt(Map stormConfig) {
		this.stormConfig = stormConfig;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConfig, TopologyContext topologyContext,
			OutputCollector outputCollector) {
		System.out.println("BaseCassandraBolt prepare method called" + outputCollector.toString());

		this.outputCollector = outputCollector;
		CassandraClient cassClient = new CassandraClient();
		session = cassClient.connect();
		logger.info("Inside Cassandra Bolt prepare method");
	}
	// INSERT INTO cycling.cyclist_category JSON '{
	// "category" : "Sprint",
	// "points" : 700,
	// "id" : "829aa84a-4bba-411f-a4fb-38167a987cda"
	//// }';

	@Override
	public void execute(Tuple arg0) {
		// Get the names of the fields in the tuple
		Fields fields = arg0.getFields();
		logger.info("Inside Cassandra Bolt execute method");
		logger.info("All fields " + fields.size());
		try {
			String tuple = (String) arg0.getValueByField(fields.get(0));
			// Retrieving each line as a tuple like below
			// {"eventName": "EnquiryRequested","eventId":
			// "fbc51be7-83f4-49dd-b80d-f1861bc3c878"}
			logger.info("Input tuple value " + tuple);
			JSONObject eventNameJson = (JSONObject) JSONSerializer.toJSON(tuple);
			logger.info("json objects are in variable eventNameJson " + eventNameJson);
			eventName = (String) eventNameJson.get("eventName");
			eventId = (String) eventNameJson.get("eventId");

			logger.info("eventName" + eventName);
			logger.info("eventId" + eventId);

			if (eventName.equals("EnquiryRequested")) {
				logger.info("Found EnquiryRequested Event and about to insert into Cassandra " + eventNameJson);
				segmentlistResult = session.execute("insert into test.event JSON '" + eventNameJson + "'");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// segmentlistResult = session
		// .execute("insert into test.event(eventname,eventid) values('Ram',
		// '100')");
		// }
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub
	}
}
