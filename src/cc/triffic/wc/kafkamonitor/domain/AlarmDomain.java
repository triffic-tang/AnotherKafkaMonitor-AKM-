package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;

public class AlarmDomain {
	private String group;
	private String topics;
	private long lag;
	private String owners;
	private String modifyDate;

	public AlarmDomain() {
		this.group = "";
		this.topics = "";
		this.lag = 0L;
		this.owners = "";
		this.modifyDate = "";
	}

	public String getGroup() {
		return this.group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getTopics() {
		return this.topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getOwners() {
		return this.owners;
	}

	public void setOwners(String owners) {
		this.owners = owners;
	}

	public long getLag() {
		return this.lag;
	}

	public void setLag(long lag) {
		this.lag = lag;
	}

	public String getModifyDate() {
		return this.modifyDate;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String toString() {
		return new Gson().toJson(this);
	}
}