package cc.triffic.wc.kafkamonitor.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.triffic.wc.kafkamonitor.domain.OffsetDomain;
import cc.triffic.wc.kafkamonitor.domain.OffsetZkDomain;
import cc.triffic.wc.kafkamonitor.domain.TupleDomain;
import cc.triffic.wc.kafkamonitor.utils.DBZKDataUtils;
import cc.triffic.wc.kafkamonitor.utils.KafkaClusterUtils;
import cc.triffic.wc.kafkamonitor.utils.LRUCacheUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class OffsetService
{
  private static LRUCacheUtils<String, TupleDomain> lruCache = new LRUCacheUtils<String, TupleDomain>(100000);

  public static String getLogSize(String topic, String group, String ip) {
    List<String> hosts = getBrokers(topic, group, ip);
    List<String> partitions = KafkaClusterUtils.findTopicPartition(topic);
    List<OffsetDomain> list = new ArrayList<OffsetDomain>();
    for (String partition : partitions) {
      int partitionInt = Integer.parseInt(partition);
      OffsetZkDomain offsetZk = KafkaClusterUtils.getOffset(topic, group, partitionInt);
      OffsetDomain offset = new OffsetDomain();
      long logSize = KafkaClusterUtils.getLogSize(hosts, topic, partitionInt);
      offset.setPartition(partitionInt);
      offset.setLogSize(logSize);
      offset.setCreate(offsetZk.getCreate());
      offset.setModify(offsetZk.getModify());
      offset.setOffset(offsetZk.getOffset());
      offset.setLag((offsetZk.getOffset() == -1L) ? 0L : logSize - offsetZk.getOffset());
      offset.setOwner(offsetZk.getOwners());
      list.add(offset);
    }
    return list.toString();
  }

  private static List<String> getBrokers(String topic, String group, String ip)
  {
    TupleDomain tuple;
    String key = group + "_" + topic + "_consumer_brokers_" + ip;
    String brokers = "";
    if (lruCache.containsKey(key)) {
      tuple = (TupleDomain)lruCache.get(key);
      brokers = tuple.getRet();
      long end = System.currentTimeMillis();
      if ((end - tuple.getTimespan()) / 60000.0D > 3.0D)
        lruCache.remove(key);
    }
    else {
      brokers = KafkaClusterUtils.getAllBrokersInfo();
      tuple = new TupleDomain();
      tuple.setRet(brokers);
      tuple.setTimespan(System.currentTimeMillis());
      lruCache.put(key, tuple);
    }
    JSONArray arr = JSON.parseArray(brokers);
    List<String> list = new ArrayList<String>();
    for (Iterator<?> localIterator = arr.iterator(); localIterator.hasNext(); ) { Object object = localIterator.next();
      JSONObject obj = (JSONObject)object;
      String host = obj.getString("host");
      int port = obj.getInteger("port").intValue();
      list.add(host + ":" + port);
    }
    return list;
  }

  public static boolean isGroupTopic(String group, String topic, String ip) {
    TupleDomain tuple;
    String key = group + "_" + topic + "_consumer_owners_" + ip;
    boolean status = false;
    if (lruCache.containsKey(key)) {
      tuple = (TupleDomain)lruCache.get(key);
      status = tuple.isStatus();
      long end = System.currentTimeMillis();
      if ((end - tuple.getTimespan()) / 60000.0D > 3.0D)
        lruCache.remove(key);
    }
    else {
      status = KafkaClusterUtils.findTopicIsConsumer(topic, group);
      tuple = new TupleDomain();
      tuple.setStatus(status);
      tuple.setTimespan(System.currentTimeMillis());
      lruCache.put(key, tuple);
    }
    return status;
  }

  public static String getOffsetsGraph(String group, String topic) {
    String ret = DBZKDataUtils.getOffsets(group, topic);
    if (ret.length() > 0) {
      ret = JSON.parseObject(ret).getString("data");
    }
    return ret;
  }
}