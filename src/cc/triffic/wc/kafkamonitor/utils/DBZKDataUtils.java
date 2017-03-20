package cc.triffic.wc.kafkamonitor.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import kafka.utils.ZkUtils;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Option;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.Seq;
import cc.triffic.wc.kafkamonitor.domain.AlarmDomain;
import cc.triffic.wc.kafkamonitor.domain.OffsetsSQLiteDomain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DBZKDataUtils
{
  private static final Logger LOG = LoggerFactory.getLogger(DBZKDataUtils.class);
  private static ZKPoolUtils zkPool = ZKPoolUtils.getInstance();
  private static ZkClient zkc = null;
  private static final String ANOTHERKAFKAMONITOR_PATH = "/anotherkafkamonitor";
//  private static final String TAB_OFFSETS = "offsets";
//  private static final String TAB_ALARM = "alarm";

  public static String getAlarm()
  {
    Iterator<?> localIterator1;
    String group;
    JSONArray array = new JSONArray();
    if (zkc == null) {
      zkc = zkPool.getZkClient();
    }
    String path = ANOTHERKAFKAMONITOR_PATH+"/alarm";
    if (ZkUtils.pathExists(zkc, path)) {
      Seq<?> seq = ZkUtils.getChildren(zkc, path);
      List<?> listSeq = JavaConversions.seqAsJavaList(seq);
      for (localIterator1 = listSeq.iterator(); localIterator1.hasNext(); ) { group = (String)localIterator1.next();
        Seq<String> seq2 = ZkUtils.getChildren(zkc, path + "/" + group);
        List<String> listSeq2 =JavaConversions.seqAsJavaList(seq2);
        for (String topic : listSeq2) {
          try {
            JSONObject object = new JSONObject();
            object.put("group", group);
            object.put("topic", topic);
            Tuple2<?, ?> tuple = ZkUtils.readDataMaybeNull(zkc, path + "/" + group + "/" + topic);
            object.put("created", CalendarUtils.timeSpan2StrDate(((Stat)tuple._2).getCtime()));
            object.put("modify", CalendarUtils.timeSpan2StrDate(((Stat)tuple._2).getMtime()));
            long lag = JSON.parseObject((String)((Option<?>)tuple._1).get()).getLong("lag").longValue();
            String owner = JSON.parseObject((String)((Option<?>)tuple._1).get()).getString("owner");
            object.put("lag", Long.valueOf(lag));
            object.put("owner", owner);
            array.add(object);
          } catch (Exception ex) {
            LOG.error("[ZK.getAlarm] has error,msg is ",ex);
          }
        }
      }
    }
    if (zkc != null) {
      zkPool.release(zkc);
      zkc = null;
    }
    return array.toJSONString();
  }

  public static String getOffsets(String group, String topic) {
    String data = "";
    if (zkc == null) {
      zkc = zkPool.getZkClient();
    }
    String path = ANOTHERKAFKAMONITOR_PATH+"/offsets/" + group + "/" + topic;
    if (ZkUtils.pathExists(zkc, path)) {
      try {
        Tuple2<?, ?> tuple = ZkUtils.readDataMaybeNull(zkc, path);
        JSONObject obj = JSON.parseObject((String)((Option<?>)tuple._1).get());
        if (CalendarUtils.getZkHour().equals(obj.getString("hour")))
          data = obj.toJSONString();
      }
      catch (Exception ex) {
        LOG.error("[ZK.getOffsets] has error,msg is " + ex.getMessage());
      }
    }
    if (zkc != null) {
      zkPool.release(zkc);
      zkc = null;
    }
    return data;
  }

  private static void update(String data, String path) {
    if (zkc == null) {
      zkc = zkPool.getZkClient();
    }
    if (!(ZkUtils.pathExists(zkc, ANOTHERKAFKAMONITOR_PATH+"/" + path))) {
      ZkUtils.createPersistentPath(zkc, ANOTHERKAFKAMONITOR_PATH+"/" + path, "");
    }
    if (ZkUtils.pathExists(zkc, ANOTHERKAFKAMONITOR_PATH+"/" + path)) {
      ZkUtils.updatePersistentPath(zkc, ANOTHERKAFKAMONITOR_PATH+"/" + path, data);
    }
    if (zkc != null) {
      zkPool.release(zkc);
      zkc = null;
    }
  }

  public static void insert(List<OffsetsSQLiteDomain> list) {
    String hour = CalendarUtils.getZkHour();
    for (OffsetsSQLiteDomain offset : list) {
      JSONObject obj = new JSONObject();
      obj.put("hour", hour);

      JSONObject object = new JSONObject();
      object.put("lag", Long.valueOf(offset.getLag()));
      object.put("lagsize", Long.valueOf(offset.getLogSize()));
      object.put("offsets", Long.valueOf(offset.getOffsets()));
      object.put("created", offset.getCreated());
      String json = getOffsets(offset.getGroup(), offset.getTopic());
      JSONObject tmp = JSON.parseObject(json);
      JSONArray zkArrayData = new JSONArray();
      if ((tmp != null) && (tmp.size() > 0)) {
        String zkHour = tmp.getString("hour");
        if (hour.equals(zkHour)) {
          String zkData = tmp.getString("data");
          zkArrayData = JSON.parseArray(zkData);
        }
      }
      if (zkArrayData.size() > 0) {
        zkArrayData.add(object);
        obj.put("data", zkArrayData);
      } else {
        obj.put("data", Arrays.asList(new JSONObject[] { object }));
      }
      update(obj.toJSONString(), "offsets/" + offset.getGroup() + "/" + offset.getTopic());
    }
  }

  public static void delete(String group, String topic, String theme) {
    if (zkc == null) {
      zkc = zkPool.getZkClient();
    }
    String path = theme + "/" + group + "/" + topic;
    if (ZkUtils.pathExists(zkc, ANOTHERKAFKAMONITOR_PATH+"/" + path)) {
      ZkUtils.deletePath(zkc, ANOTHERKAFKAMONITOR_PATH+"/" + path);
    }
    if (zkc != null) {
      zkPool.release(zkc);
      zkc = null;
    }
  }

  public static int insertAlarmConfigure(AlarmDomain alarm) {
    JSONObject object = new JSONObject();
    object.put("lag", Long.valueOf(alarm.getLag()));
    object.put("owner", alarm.getOwners());
    try {
      update(object.toJSONString(), "alarm/" + alarm.getGroup() + "/" + alarm.getTopics());
    } catch (Exception ex) {
      LOG.error("[ZK.insertAlarm] has error,msg is " + ex.getMessage());
      return -1;
    }
    return 0;
  }
}