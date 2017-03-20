package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;

public class ConsumerDetailDomain {
	private int id;
	private String topic;
	private boolean isConsumering;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public boolean isConsumering() {
		return this.isConsumering;
	}

	public void setConsumering(boolean isConsumering) {
		this.isConsumering = isConsumering;
	}

	public String toString() {
		return new Gson().toJson(this);
	}
}