package cc.triffic.wc.kafkamonitor.service;

import cc.triffic.wc.kafkamonitor.utils.KafkaClusterUtils;
import cc.triffic.wc.kafkamonitor.utils.ZKCliUtils;

import com.alibaba.fastjson.JSONObject;

public class ClusterService {
	public static String getCluster() {
		String zk = KafkaClusterUtils.getZkInfo();
		String kafka = KafkaClusterUtils.getAllBrokersInfo();
		JSONObject obj = new JSONObject();
		obj.put("zk", zk);
		obj.put("kafka", kafka);
		return obj.toJSONString();
	}

	public static JSONObject zkCliIsLive() {
		return KafkaClusterUtils.zkCliIsLive();
	}

	public static String getZKMenu(String cmd, String type) {
		JSONObject object;
		String ret = "";
		if ("ls".equals(type)) {
			object = new JSONObject();
			object.put("result", ls(cmd));
			ret = object.toJSONString();
		} else if ("delete".equals(type)) {
			object = new JSONObject();
			object.put("result", delete(cmd));
			ret = object.toJSONString();
		} else if ("get".equals(type)) {
			object = new JSONObject();
			object.put("result", get(cmd));
			ret = object.toJSONString();
		} else {
			ret = "Invalid command";
		}
		return ret;
	}

	private static Object delete(String cmd) {
		String ret = "";
		String[] len = cmd.replaceAll(" ", "").split("delete");
		if (len.length == 0) {
			return cmd + " has error";
		}
		String command = len[1];
		ret = ZKCliUtils.delete(command);

		return ret;
	}

	private static Object get(String cmd) {
		String ret = "";
		String[] len = cmd.replaceAll(" ", "").split("get");
		if (len.length == 0) {
			return cmd + " has error";
		}
		String command = len[1];
		ret = ZKCliUtils.get(command);

		return ret;
	}

	private static String ls(String cmd) {
		String ret = "";
		String[] len = cmd.replaceAll(" ", "").split("ls");
		if (len.length == 0) {
			return cmd + " has error";
		}
		String command = len[1];
		ret = ZKCliUtils.ls(command);

		return ret;
	}
}