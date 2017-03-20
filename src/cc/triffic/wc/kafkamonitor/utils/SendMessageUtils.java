package cc.triffic.wc.kafkamonitor.utils;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

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
	
	
	public static void sendSMS(String smsMob, String smsText){
		HttpClient client = new HttpClient();
        PostMethod post = new PostMethod("http://gbk.sms.webchinese.cn");
        
        // 在头文件中设置转码
        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");
        NameValuePair[] data = { 
        		new NameValuePair("Uid", SystemConfigUtils.getProperty("anotherkafkamonitor.mobile.username")), //中国网建sms平台注册的用户名
                new NameValuePair("Key", SystemConfigUtils.getProperty("anotherkafkamonitor.mobile.password")), //中国网建sms平台注册的用户密钥
                new NameValuePair("smsMob", smsMob),                //将要发送到的手机号码
                new NameValuePair("smsText", smsText) };            //要发送的短信内容
        post.setRequestBody(data);

        try {
			client.executeMethod(post);
//			int statusCode = post.getStatusCode();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
	}
}