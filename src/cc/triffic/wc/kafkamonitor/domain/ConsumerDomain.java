package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;
import java.util.List;

public class ConsumerDomain {
	private int id;
	private String group;
	private List<String> topic;
	private int consumerNumber;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroup() {
		return this.group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public List<String> getTopic() {
		return this.topic;
	}

	public void setTopic(List<String> topic) {
		this.topic = topic;
	}

	public int getConsumerNumber() {
		return this.consumerNumber;
	}

	public void setConsumerNumber(int consumerNumber) {
		this.consumerNumber = consumerNumber;
	}

	public String toString() {
		return new Gson().toJson(this);
	}
}