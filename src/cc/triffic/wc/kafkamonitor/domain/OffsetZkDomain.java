package cc.triffic.wc.kafkamonitor.domain;

import com.google.gson.Gson;

public class OffsetZkDomain {
	private long offset;
	private String create;
	private String modify;
	private String owners;

	public OffsetZkDomain() {
		this.offset = -1L;
		this.create = "";
		this.modify = "";
		this.owners = "";
	}

	public String getOwners() {
		return this.owners;
	}

	public void setOwners(String owners) {
		this.owners = owners;
	}

	public long getOffset() {
		return this.offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
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