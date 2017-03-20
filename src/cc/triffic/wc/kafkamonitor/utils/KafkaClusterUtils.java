package cc.triffic.wc.kafkamonitor.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Option;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.Seq;
import cc.triffic.wc.kafkamonitor.domain.BrokersDomain;
import cc.triffic.wc.kafkamonitor.domain.OffsetZkDomain;
import cc.triffic.wc.kafkamonitor.domain.PartitionsDomain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class KafkaClusterUtils {
	private static ZKPoolUtils zkPool = ZKPoolUtils.getInstance();

	private static final Logger LOG = LoggerFactory.getLogger(KafkaClusterUtils.class);

	public static JSONObject zkCliIsLive() {
		JSONObject object = new JSONObject();
		ZkClient zkc = zkPool.getZkClient();
		if (zkc != null) {
			object.put("live", Boolean.valueOf(true));
			object.put("list", SystemConfigUtils.getProperty("kafka.zk.list"));
		} else {
			object.put("live", Boolean.valueOf(false));
			object.put("list", SystemConfigUtils.getProperty("kafka.zk.list"));
		}
		if (zkc != null) {
			zkPool.release(zkc);
			zkc = null;
		}
		return object;
	}

	public static List<String> findTopicPartition(String topic) {
		ZkClient zkc = zkPool.getZkClient();
		Seq<String> seq = ZkUtils.getChildren(zkc, "/brokers/topics/" + topic + "/partitions");
		
		List<String> listSeq = JavaConversions.seqAsJavaList(seq);
		if (zkc != null) {
			zkPool.release(zkc);
			zkc = null;
			seq = null;
		}
		return listSeq;
	}

	public static boolean findTopicIsConsumer(String topic, String group) {
		ZkClient zkc = zkPool.getZkClient();
		String ownersPath = "/consumers/" + group + "/owners/" + topic;
		boolean status = ZkUtils.pathExists(zkc, ownersPath);
		if (zkc != null) {
			zkPool.release(zkc);
			zkc = null;
		}
		return status;
	}

	public static OffsetZkDomain getOffset(String topic, String group,
			int partition) {
		ZkClient zkc = zkPool.getZkClientSerializer();
		OffsetZkDomain offsetZk = new OffsetZkDomain();
		String offsetPath = "/consumers/" + group + "/offsets/" + topic
				+ "/" + partition;
		String ownersPath = "/consumers/" + group + "/owners/" + topic
				+ "/" + partition;

		Tuple2<?, ?> tuple = null;
		try {
			if (ZkUtils.pathExists(zkc, offsetPath)) {
				tuple = ZkUtils.readDataMaybeNull(zkc, offsetPath);
			} else {
				LOG.info("partition[" + partition + "],offsetPath["
						+ offsetPath + "] is not exist!");

				if (zkc != null) {
					zkPool.releaseZKSerializer(zkc);
					zkc = null;
				}
				return offsetZk;
			}
		} catch (Exception ex) {
			LOG.error("partition[" + partition
					+ "],get offset has error,msg is " + ex.getMessage());
			if (zkc != null) {
				zkPool.releaseZKSerializer(zkc);
				zkc = null;
			}
			return offsetZk;
		}
		long offsetSize = Long.parseLong((String) ((Option<?>) tuple._1).get());
		if (ZkUtils.pathExists(zkc, ownersPath)) {
			Tuple2<?, ?> tuple2 = ZkUtils.readData(zkc, ownersPath);
			offsetZk.setOwners((tuple2._1 == null) ? "" : (String) tuple2._1);
		} else {
			offsetZk.setOwners("");
		}
		offsetZk.setOffset(offsetSize);
		offsetZk.setCreate(CalendarUtils.timeSpan2StrDate(((Stat) tuple._2)
				.getCtime()));
		offsetZk.setModify(CalendarUtils.timeSpan2StrDate(((Stat) tuple._2)
				.getMtime()));
		if (zkc != null) {
			zkPool.releaseZKSerializer(zkc);
			zkc = null;
		}
		return offsetZk;
	}

	public static long getLogSize(List<String> hosts, String topic, int partition) {
		LOG.info("Find leader hosts [" + hosts + "]");
		PartitionMetadata metadata = findLeader(hosts, topic, partition);
		if (metadata == null) {
			LOG.error("[KafkaClusterUtils.getLogSize()] - Can't find metadata for Topic and Partition. Exiting");

			return 0L;
		}
		if (metadata.leader() == null) {
			LOG.error("[KafkaClusterUtils.getLogSize()] - Can't find Leader for Topic and Partition. Exiting");

			return 0L;
		}

		String clientName = "Client_" + topic + "_" + partition;
		String reaHost = metadata.leader().host();
		int port = metadata.leader().port();

		long ret = 0L;
		try {
			SimpleConsumer simpleConsumer = new SimpleConsumer(reaHost, port,
					100000, 65536, clientName);

			TopicAndPartition topicAndPartition = new TopicAndPartition(topic,
					partition);

			java.util.Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
			requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(
					kafka.api.OffsetRequest.LatestTime(), 1));

			kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
					requestInfo, kafka.api.OffsetRequest.CurrentVersion(),
					clientName);
			OffsetResponse response = simpleConsumer.getOffsetsBefore(request);
			if (response.hasError()) {
				LOG.error("Error fetching data Offset , Reason: "
						+ response.errorCode(topic, partition));
				return 0L;
			}
			long[] offsets = response.offsets(topic, partition);
			ret = offsets[0];
			if (simpleConsumer != null)
				simpleConsumer.close();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		}
		return ret;
	}

	private static PartitionMetadata findLeader(List<String> a_seedBrokers, String a_topic, int a_partition) {
		PartitionMetadata returnMetaData = null;
		for (String seed : a_seedBrokers) {
			SimpleConsumer consumer = null;
			try {
				String ip = seed.split(":")[0];
				String port = seed.split(":")[1];
				
				//ip = "fe80:0:0:0:ac16:44ff:fead:10d3%8";
				//port = "9092";
				
				consumer = new SimpleConsumer(ip, Integer.parseInt(port), 10000, 65536, "leaderLookup");

				List<String> topics = Collections.singletonList(a_topic);
				TopicMetadataRequest req = new TopicMetadataRequest(topics);
				TopicMetadataResponse resp = consumer.send(req);

				List<TopicMetadata> metaData = resp.topicsMetadata();
				for (TopicMetadata item : metaData){
					for (PartitionMetadata part : item.partitionsMetadata()){
						if (part.partitionId() == a_partition) {
							returnMetaData = part;

							if (consumer == null)
								break;
							consumer.close();
							break;
						}
					}
						
				}
					
			} catch (Exception e) {
				LOG.error("Error communicating with Broker [" + seed
						+ "] to find Leader for [" + a_topic + ", "
						+ a_partition + "] Reason: " + e);
			} finally {
				if (consumer != null)
					consumer.close();
			}
		}
		return returnMetaData;
	}

	public static java.util.Map<String, List<String>> getConsumers() {
		ZkClient zkc = zkPool.getZkClient();
		java.util.Map<String, List<String>> mapConsumers = new HashMap<String, List<String>>();
		try {
			Seq<String> seq = ZkUtils.getChildren(zkc, "/consumers");
			List<String> listSeq = JavaConversions.seqAsJavaList(seq);
			for (String group : listSeq) {
				Seq<String> tmp = ZkUtils.getChildren(zkc, "/consumers/"
						+ group + "/owners");

				List<String> list = JavaConversions.seqAsJavaList(tmp);
				mapConsumers.put(group, list);
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		} finally {
			if (zkc != null) {
				zkPool.release(zkc);
				zkc = null;
			}
		}
		return mapConsumers;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static java.util.Map<String, List<String>> getActiveTopic() {
		Iterator localIterator1;
		ZkClient zkc = zkPool.getZkClientSerializer();
		java.util.Map actvTopic = new HashMap();
		try {
			String group;
			Seq seq = ZkUtils.getChildren(zkc, "/consumers");
			List listSeq = JavaConversions.seqAsJavaList(seq);
			JSONArray arr = new JSONArray();
			for (localIterator1 = listSeq.iterator(); localIterator1.hasNext();) {
				group = (String) localIterator1.next();

				scala.collection.mutable.Map map = ZkUtils
						.getConsumersPerTopic(zkc, group, false);

				for (Map.Entry entry : (Set<Map.Entry<String, Object>>) JavaConversions
						.mapAsJavaMap(map).entrySet()) {
					JSONObject obj = new JSONObject();
					obj.put("topic", entry.getKey());
					obj.put("group", group);
					arr.add(obj);
				}
			}
			for (localIterator1 = arr.iterator(); localIterator1.hasNext();) {
				Object object = localIterator1.next();
				JSONObject obj = (JSONObject) object;
				group = obj.getString("group");
				String topic = obj.getString("topic");
				if (actvTopic.containsKey(group + "_" + topic)) {
					((List) actvTopic.get(group + "_" + topic)).add(topic);
				} else {
					List topics = new ArrayList();
					topics.add(topic);
					actvTopic.put(group + "_" + topic, topics);
				}
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		} finally {
			if (zkc != null) {
				zkPool.releaseZKSerializer(zkc);
				zkc = null;
			}
		}
		return actvTopic;
	}

	public static String getZkInfo() {
		String[] zks = SystemConfigUtils.getPropertyArray("kafka.zk.list", ",");
		JSONArray arr = new JSONArray();
		int id = 1;
		for (String zk : zks) {
			JSONObject obj = new JSONObject();
			obj.put("id", Integer.valueOf(id++));
			obj.put("ip", zk.split(":")[0]);
			obj.put("port", zk.split(":")[1]);
			obj.put("mode", ZookeeperUtils.serverStatus(zk.split(":")[0],
					zk.split(":")[1]));
			arr.add(obj);
		}
		return arr.toJSONString();
	}

	public static String getAllBrokersInfo() {
		int id;
		ZkClient zkc = zkPool.getZkClientSerializer();
		List<BrokersDomain> list = new ArrayList<BrokersDomain>();
		if (ZkUtils.pathExists(zkc, "/brokers/ids")) {
			Seq<String> seq = ZkUtils.getChildren(zkc, "/brokers/ids");
			List<String> listSeq = JavaConversions.seqAsJavaList(seq);
			id = 0;
			for (String ids : listSeq) {
				try {
					Tuple2<?, ?> tuple = ZkUtils.readDataMaybeNull(zkc,"/brokers/ids/" + ids);

					BrokersDomain broker = new BrokersDomain();
					broker.setCreated(CalendarUtils.timeSpan2StrDate(((Stat) tuple._2).getCtime()));
					broker.setModify(CalendarUtils.timeSpan2StrDate(((Stat) tuple._2).getMtime()));

					String host = JSON.parseObject((String) ((Option<?>) tuple._1).get()).getString("host");

					int port = JSON.parseObject((String) ((Option<?>) tuple._1).get()).getInteger("port").intValue();

					broker.setHost(host);
					broker.setPort(port);
					broker.setId(++id);
					list.add(broker);
				} catch (Exception ex) {
					LOG.error(ex.getMessage());
				}
			}
		}
		if (zkc != null) {
			zkPool.releaseZKSerializer(zkc);
			zkc = null;
		}
		return list.toString();
	}

	public static String getAllPartitions() {
		int id;
		ZkClient zkc = zkPool.getZkClientSerializer();
		List<PartitionsDomain> list = new ArrayList<PartitionsDomain>();
		if (ZkUtils.pathExists(zkc, "/brokers/topics")) {
			Seq<String> seq = ZkUtils.getChildren(zkc, "/brokers/topics");
			List<String> listSeq = JavaConversions.seqAsJavaList(seq);
			id = 0;
			for (String topic : listSeq) {
				try {
					Tuple2<?, ?> tuple = ZkUtils.readDataMaybeNull(zkc, "/brokers/topics/" + topic);
					
					

					PartitionsDomain partition = new PartitionsDomain();
					partition.setId(++id);
					partition.setCreated(CalendarUtils.timeSpan2StrDate(((Stat) tuple._2).getCtime()));
					partition.setModify(CalendarUtils.timeSpan2StrDate(((Stat) tuple._2).getMtime()));
					partition.setTopic(topic);

					JSONObject partitionObject = JSON.parseObject((String) ((Option<?>) tuple._1).get()).getJSONObject("partitions");

					partition.setPartitionNumbers(partitionObject.size());
					partition.setPartitions(partitionObject.keySet());
					list.add(partition);
				} catch (Exception ex) {
					LOG.error("Error occurs:", ex);
				}
			}
		}
		if (zkc != null) {
			zkPool.releaseZKSerializer(zkc);
			zkc = null;
		}
		return list.toString();
	}

	public static String geyReplicasIsr(String topic, int partitionid) {
		ZkClient zkc = zkPool.getZkClientSerializer();
		Seq<?> seq = ZkUtils.getInSyncReplicasForPartition(zkc, topic,
				partitionid);

		List<?> listSeq = JavaConversions.seqAsJavaList(seq);
		if (zkc != null) {
			zkPool.releaseZKSerializer(zkc);
			zkc = null;
		}
		return listSeq.toString();
	}
}