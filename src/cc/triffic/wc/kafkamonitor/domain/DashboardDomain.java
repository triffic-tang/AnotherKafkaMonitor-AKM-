package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;

public class DashboardDomain {
	private int brokers;
	private int topics;
	private int zks;
	private int consumers;

	public int getBrokers() {
		return this.brokers;
	}

	public void setBrokers(int brokers) {
		this.brokers = brokers;
	}

	public int getTopics() {
		return this.topics;
	}

	public void setTopics(int topics) {
		this.topics = topics;
	}

	public int getZks() {
		return this.zks;
	}

	public void setZks(int zks) {
		this.zks = zks;
	}

	public int getConsumers() {
		return this.consumers;
	}

	public void setConsumers(int consumers) {
		this.consumers = consumers;
	}

	public String toString() {
		return new Gson().toJson(this);
	}
}