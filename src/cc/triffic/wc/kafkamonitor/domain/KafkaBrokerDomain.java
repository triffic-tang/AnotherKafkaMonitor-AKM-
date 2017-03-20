package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;

public class KafkaBrokerDomain {
	private String host;
	private int port;

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

	public String toString() {
		return new Gson().toJson(this);
	}
}