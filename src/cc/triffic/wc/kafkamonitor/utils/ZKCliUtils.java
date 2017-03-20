package cc.triffic.wc.kafkamonitor.utils;

import kafka.utils.ZkUtils;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;

import scala.Option;
import scala.Tuple2;

public class ZKCliUtils {
	private static ZKPoolUtils zkPool = ZKPoolUtils.getInstance();

	public static String ls(String cmd) {
		String ret = "";
		ZkClient zkc = zkPool.getZkClient();
		boolean status = ZkUtils.pathExists(zkc, cmd);
		if (status) {
			ret = zkc.getChildren(cmd).toString();
		}
		if (zkc != null) {
			zkPool.release(zkc);
			zkc = null;
		}
		return ret;
	}

	public static String delete(String cmd) {
		String ret = "";
		ZkClient zkc = zkPool.getZkClient();
		boolean status = ZkUtils.pathExists(zkc, cmd);
		if (status) {
			if (zkc.delete(cmd))
				ret = "[" + cmd + "] has delete success";
			else {
				ret = "[" + cmd + "] has delete failed";
			}
		}
		if (zkc != null) {
			zkPool.release(zkc);
			zkc = null;
		}
		return ret;
	}

	public static String get(String cmd) {
		String ret = "";
		ZkClient zkc = zkPool.getZkClientSerializer();
		boolean status = ZkUtils.pathExists(zkc, cmd);
		if (status) {
			Tuple2<?, ?> tuple2 = ZkUtils.readDataMaybeNull(zkc, cmd);

			ret = ret + ((String) ((Option<?>) tuple2._1).get()) + "\n";
			ret = ret + "cZxid = " + ((Stat) tuple2._2).getCzxid() + "\n";
			ret = ret + "ctime = " + ((Stat) tuple2._2).getCtime() + "\n";
			ret = ret + "mZxid = " + ((Stat) tuple2._2).getMzxid() + "\n";
			ret = ret + "mtime = " + ((Stat) tuple2._2).getMtime() + "\n";
			ret = ret + "pZxid = " + ((Stat) tuple2._2).getPzxid() + "\n";
			ret = ret + "cversion = " + ((Stat) tuple2._2).getCversion() + "\n";
			ret = ret + "dataVersion = " + ((Stat) tuple2._2).getVersion()
					+ "\n";
			ret = ret + "aclVersion = " + ((Stat) tuple2._2).getAversion()
					+ "\n";
			ret = ret + "ephemeralOwner = "
					+ ((Stat) tuple2._2).getEphemeralOwner() + "\n";
			ret = ret + "dataLength = " + ((Stat) tuple2._2).getDataLength()
					+ "\n";
			ret = ret + "numChildren = " + ((Stat) tuple2._2).getNumChildren()
					+ "\n";
		}
		if (zkc != null) {
			zkPool.releaseZKSerializer(zkc);
			zkc = null;
		}
		return ret;
	}

	public static void main(String[] args) {
		System.out.println(get("/kafka_eagle/offsets/group2/ke_test1"));
	}
}