package cc.triffic.wc.kafkamonitor.utils;

import cc.triffic.wc.kafkamonitor.domain.MailSenderDomain;
import cc.triffic.wc.kafkamonitor.service.MailSenderService;

public class SendMessageUtils {
	public static void send(String toAddress, String subject, String content) {
		MailSenderDomain mailInfo = new MailSenderDomain();
		mailInfo.setMailServerHost(SystemConfigUtils
				.getProperty("anotherkafkamonitor.mail.server.host"));
		mailInfo.setMailServerPort(SystemConfigUtils
				.getProperty("anotherkafkamonitor.mail.server.port"));
		mailInfo.setValidate(true);
		mailInfo.setUserName(SystemConfigUtils
				.getProperty("anotherkafkamonitor.mail.username"));
		mailInfo.setPassword(SystemConfigUtils
				.getProperty("anotherkafkamonitor.mail.password"));
		mailInfo.setFromAddress(SystemConfigUtils
				.getProperty("anotherkafkamonitor.mail.username"));
		mailInfo.setToAddress(toAddress);
		mailInfo.setSubject(subject);
		mailInfo.setContent(content);
		MailSenderService sms = new MailSenderService();
		sms.sendHtmlMail(mailInfo);
	}
}