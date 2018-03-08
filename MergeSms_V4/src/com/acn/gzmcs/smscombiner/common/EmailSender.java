package com.acn.gzmcs.smscombiner.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.acn.gzmcs.smscombiner.bean.MailBean;

public class EmailSender {
	
	private static final Logger logger = Logger.getLogger(EmailSender.class);
	private static final PropertyLoader propLoader = new PropertyLoader();
	private static final String HK_HOST = "hk.host";
	private static final String HK_SENDFROM = "hk.sendfrom";
	private static final String HK_SENDTO = "hk.sendto";
	private static final String HK_SENDCC = "hk.sendcc";
	private static final String HK_SUBJECT = "hk.subject";
	private static final String HK_CONTENT = "hk.content";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final SimpleDateFormat emailDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private Date date = null;

	public EmailSender(Date date) {
		this.date = date;
	}
	
	public void refresh(Date date) {
		this.date = date;
	}
	
	public void send() {
		String content = "Hi, there is no SMS generated & Merged.";
		try {
			MailBean mb = getMailBean();
			mb.setContent(content);
			logger.info("Sending Email...");
			if (sendMail(mb))
				logger.info("Send Email successfully!");
			else
				logger.info("Send Email failed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String attachment) {
		
		try {
			MailBean mb = getMailBean();
			mb.attachFile(attachment);
			logger.info("Sending Email...");
			if (sendMail(mb))
				logger.info("Send Email successfully!");
			else
				logger.info("Send Email failed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private  MailBean getMailBean() {
		String host = propLoader.getValue(HK_HOST);
		String sentFrom = propLoader.getValue(HK_SENDFROM);
		String sentTo = propLoader.getValue(HK_SENDTO);
		String sentCc = propLoader.getValue(HK_SENDCC);
		String subject = propLoader.getValue(HK_SUBJECT);
		String content = propLoader.getValue(HK_CONTENT);
		String username = propLoader.getValue(USERNAME);
		String password = propLoader.getValue(PASSWORD);
		MailBean mb = new MailBean();
		mb.setHost(host);
		mb.setFrom(sentFrom);
		mb.setTo(sentTo);
		mb.setCc(sentCc);
		mb.setSubject(subject + " " + emailDateFormat.format(date));
		mb.setContent(content);
		mb.setPassword(password);
		mb.setUsername(username);
		return mb;
	}
	
	private static boolean sendMail(MailBean mb) {
		String host = mb.getHost();
		final String username = mb.getUsername();
		final String password = mb.getPassword();
		String from = mb.getFrom();
		String to = mb.getTo();
		String subject = mb.getSubject();
		String content = mb.getContent();
		String fileName = mb.getFilename();
		Vector file = mb.getFile();
		Properties props = System.getProperties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getInstance(props, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));

			InternetAddress[] address = InternetAddress.parse(to, true);

			msg.setRecipients(RecipientType.TO, address);

			msg.setSubject(subject);
			Multipart mp = new MimeMultipart();
			MimeBodyPart mbpContent = new MimeBodyPart();
			mbpContent.setText(content);
			mp.addBodyPart(mbpContent);

			if (file != null) {
				Enumeration efile = file.elements();
				while (efile.hasMoreElements()) {
					MimeBodyPart mbpFile = new MimeBodyPart();
					fileName = ((String) efile.nextElement()).toString();
					FileDataSource fds = new FileDataSource(fileName);
					mbpFile.setDataHandler(new DataHandler(fds));
					mbpFile.setFileName(fds.getName());
					mp.addBodyPart(mbpFile);
				}
			}
			msg.setContent(mp);
			msg.setSentDate(new Date());
			Transport.send(msg);
		} catch (MessagingException me) {
			me.printStackTrace();
			return false;
		}
		return true;
	}
}
