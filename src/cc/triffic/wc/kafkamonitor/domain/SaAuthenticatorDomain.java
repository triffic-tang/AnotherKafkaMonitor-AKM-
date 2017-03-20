package cc.triffic.wc.kafkamonitor.domain;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SaAuthenticatorDomain extends Authenticator {
	String userName = null;
	String password = null;

	public SaAuthenticatorDomain() {
	}

	public SaAuthenticatorDomain(String username, String password) {
		this.userName = username;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(this.userName, this.password);
	}
}