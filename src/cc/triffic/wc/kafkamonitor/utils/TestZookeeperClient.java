package cc.triffic.wc.kafkamonitor.utils;


public class TestZookeeperClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/*
		ZkClient zkClient = new ZkClient("localhost:2181");
		
		
		List<String> childList = zkClient.getChildren("/");
		for(String tempChild : childList){
			System.out.println("---" + tempChild);
		}
		*/
		
		String result = KafkaClusterUtils.getAllBrokersInfo();
		System.out.println("----"+result);
		
		/*
		try {
			ZooKeeper clKeeper = new ZooKeeper("localhost:2181", 10000, null);
			System.out.println("***"+clKeeper.getSessionId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}

}
