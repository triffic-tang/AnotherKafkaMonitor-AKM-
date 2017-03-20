package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;

public class OffsetDomain {
	private int partition;
	private long logSize;
	private long offset;
	private long lag;
	private String owner;
	private String create;
	private String modify;

	public int getPartition() {
		return this.partition;
	}

	public void setPartition(int partition) {
		this.partition = partition;
	}

	public long getLogSize() {
		return this.logSize;
	}

	public void setLogSize(long logSize) {
		this.logSize = logSize;
	}

	public long getOffset() {
		return this.offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getLag() {
		return this.lag;
	}

	public void setLag(long lag) {
		this.lag = lag;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCreate() {
		return this.create;
	}

	public void setCreate(String create) {
		this.create = create;
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