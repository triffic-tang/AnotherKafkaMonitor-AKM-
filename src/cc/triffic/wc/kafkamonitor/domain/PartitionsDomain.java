package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;
import java.util.HashSet;
import java.util.Set;

public class PartitionsDomain {
	private int id;
	private String topic;
	private Set<String> partitions;
	private int partitionNumbers;
	private String created;
	private String modify;

	public PartitionsDomain() {
		this.id = 0;
		this.topic = "";
		this.partitions = new HashSet<String>();
		this.partitionNumbers = 0;
		this.created = "";
		this.modify = "";
	}

	public String getCreated() {
		return this.created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getModify() {
		return this.modify;
	}

	public void setModify(String modify) {
		this.modify = modify;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPartitionNumbers() {
		return this.partitionNumbers;
	}

	public void setPartitionNumbers(int partitionNumbers) {
		this.partitionNumbers = partitionNumbers;
	}

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Set<String> getPartitions() {
		return this.partitions;
	}

	public void setPartitions(Set<String> partitions) {
		this.partitions = partitions;
	}

	public String toString() {
		return new Gson().toJson(this);
	}
}