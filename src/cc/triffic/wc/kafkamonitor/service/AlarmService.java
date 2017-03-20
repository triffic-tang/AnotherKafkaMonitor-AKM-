package cc.triffic.wc.kafkamonitor.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.triffic.wc.kafkamonitor.domain.AlarmDomain;
import cc.triffic.wc.kafkamonitor.domain.TupleDomain;
import cc.triffic.wc.kafkamonitor.utils.DBZKDataUtils;
import cc.triffic.wc.kafkamonitor.utils.KafkaClusterUtils;
import cc.triffic.wc.kafkamonitor.utils.LRUCacheUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AlarmService {
	private static LRUCacheUtils<String, TupleDomain> map = new LRUCacheUtils<String, TupleDomain>(100000);

	public static String getTopics(String ip) {
		String key = "alarm_topic_" + ip;
		String ret = "";
		if (map.containsKey(key)) {
			TupleDomain tuple = (TupleDomain) map.get(key);
			ret = tuple.getRet();
			long end = System.currentTimeMillis();
			if ((end - tuple.getTimespan()) / 60000.0D > 1.0D)
				map.remove(key);
		} else {
			Map<String, List<String>> tmp = KafkaClusterUtils.getConsumers();
			JSONArray retArray = new JSONArray();
			for (Map.Entry<String, List<String>> entry : tmp.entrySet()) {
				JSONObject retObj = new JSONObject();
				retObj.put("group", entry.getKey());
				retObj.put("topics", entry.getValue());
				retArray.add(retObj);
			}
			ret = retArray.toJSONString();
			TupleDomain tuple = new TupleDomain();
			tuple.setRet(ret);
			tuple.setTimespan(System.currentTimeMillis());
			map.put(key, tuple);
		}
		return ret;
	}

	public static Map<String, Object> addAlarm(AlarmDomain alarm) {
		Map<String, Object> map = new HashMap<String, Object>();
		int status = DBZKDataUtils.insertAlarmConfigure(alarm);
		if (status == -1) {
			map.put("status", "error");
			map.put("info", "insert [" + alarm + "] has error!");
		} else {
			map.put("status", "success");
			map.put("info", "insert success!");
		}
		return map;
	}

	public static String list() {
		return DBZKDataUtils.getAlarm();
	}

	public static void delete(String group, String topic) {
		DBZKDataUtils.delete(group, topic, "alarm");
	}
}