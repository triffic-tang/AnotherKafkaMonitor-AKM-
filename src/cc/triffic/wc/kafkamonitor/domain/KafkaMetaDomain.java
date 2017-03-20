package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;

public class KafkaMetaDomain {
	private int partitionId;
	private int leader;
	private String isr;
	private String replicas;

	public int getPartitionId() {
		return this.partitionId;
	}

	public void setPartitionId(int partitionId) {
		this.partitionId = partitionId;
	}

	public int getLeader() {
		return this.leader;
	}

	public void setLeader(int leader) {
		this.leader = leader;
	}

	public String getIsr() {
		return this.isr;
	}

	public void setIsr(String isr) {
		this.isr = isr;
	}

	public String getReplicas() {
		return this.replicas;
	}

	public void setReplicas(String replicas) {
		this.replicas = replicas;
	}

	public String toString() {
		return new Gson().toJson(this);
	}
}