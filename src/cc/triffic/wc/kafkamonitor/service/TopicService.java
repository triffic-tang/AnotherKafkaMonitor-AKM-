package cc.triffic.wc.kafkamonitor.service;

import java.util.Iterator;

import cc.triffic.wc.kafkamonitor.domain.TupleDomain;
import cc.triffic.wc.kafkamonitor.utils.KafkaClusterUtils;
import cc.triffic.wc.kafkamonitor.utils.KafkaMetaUtils;
import cc.triffic.wc.kafkamonitor.utils.LRUCacheUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TopicService {
	private static LRUCacheUtils<String, TupleDomain> map = new LRUCacheUtils<String, TupleDomain>(100000);

	public static String list() {
		return KafkaClusterUtils.getAllPartitions();
	}

	public static String topicMeta(String topicName, String ip) {
		TupleDomain tuple;
		String key = topicName + "_meta_" + ip;
		String ret = "";
		if (map.containsKey(key)) {
			tuple = (TupleDomain) map.get(key);
			ret = tuple.getRet();
			long end = System.currentTimeMillis();
			if ((end - tuple.getTimespan()) / 60000.0D > 1.0D)
				map.remove(key);
		} else {
			ret = KafkaMetaUtils.findLeader(topicName).toString();
			tuple = new TupleDomain();
			tuple.setRet(ret);
			tuple.setTimespan(System.currentTimeMillis());
			map.put(key, tuple);
		}
		return ret;
	}

	public static boolean findTopicName(String topicName, String ip) {
		long end;
		String key = topicName + "_check_" + ip;
		boolean ret = false;
		if (map.containsKey(key)) {
			TupleDomain tuple = (TupleDomain) map.get(key);
			ret = tuple.isStatus();
			end = System.currentTimeMillis();
			if ((end - tuple.getTimespan()) / 60000.0D > 1.0D)
				map.remove(key);
		} else {
			JSONArray arr = JSON.parseArray(KafkaClusterUtils
					.getAllPartitions());
			Iterator<?> iter = arr.iterator();
			while (iter.hasNext()) {
				Object object = iter.next();
				JSONObject obj = (JSONObject) object;
				String topic = obj.getString("topic");
				if (topicName.equals(topic)) {
					ret = true;
					break;
				}
			}
			TupleDomain tuple = new TupleDomain();
			tuple.setStatus(ret);
			tuple.setTimespan(System.currentTimeMillis());
			map.put(key, tuple);
		}
		return ret;
	}
}