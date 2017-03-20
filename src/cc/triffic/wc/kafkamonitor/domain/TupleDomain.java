package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;

public class TupleDomain {
	private long timespan;
	private String ret;
	private boolean status;

	public TupleDomain() {
		this.ret = "";
	}

	public boolean isStatus() {
		return this.status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public long getTimespan() {
		return this.timespan;
	}

	public void setTimespan(long timespan) {
		this.timespan = timespan;
	}

	public String getRet() {
		return this.ret;
	}

	public void setRet(String ret) {
		this.ret = ret;
	}

	public String toString() {
		return new Gson().toJson(this);
	}
}