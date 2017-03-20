package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;

public class OffsetsSQLiteDomain {
	private String group;
	private String topic;
	private long logSize;
	private long offsets;
	private long lag;
	private String created;

	public OffsetsSQLiteDomain() {
		this.group = "";
		this.topic = "";
		this.logSize = 0L;
		this.offsets = 0L;
		this.lag = 0L;
		this.created = "";
	}

	public String getGroup() {
		return this.group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public long getLogSize() {
		return this.logSize;
	}

	public void setLogSize(long logSize) {
		this.logSize = logSize;
	}

	public long getOffsets() {
		return this.offsets;
	}

	public void setOffsets(long offsets) {
		this.offsets = offsets;
	}

	public long getLag() {
		return this.lag;
	}

	public void setLag(long lag) {
		this.lag = lag;
	}

	public String getCreated() {
		return this.created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String toString() {
		return new Gson().toJson(this);
	}
}