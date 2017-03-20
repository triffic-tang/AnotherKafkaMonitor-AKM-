package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;

public class BrokersDomain {
	private int id;
	private String host;
	private int port;
	private String created;
	private String modify;

	public BrokersDomain() {
		this.id = 0;
		this.host = "";
		this.port = 0;
		this.created = "";
		this.modify = "";
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
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

	public String toString() {
		return new Gson().toJson(this);
	}
}