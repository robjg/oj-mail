package org.oddjob.mail;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.deploy.annotations.ArooaText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

/**
 * @oddjob.description Send a mail message.
 * 
 * @oddjob.example
 * 
 * Send a simple message.
 * 
 * {@oddjob.xml.resource org/oddjob/mail/SendMailExample.xml}
 * 
 * @oddjob.example
 * 
 * Send attachements. This example rather unimagineatively sends the build
 * file as two seperate attachements.
 * 
 * {@oddjob.xml.resource org/oddjob/mail/SendMailAttachments.xml}
 * 
 * @author rob
 *
 */
public class SendMailJob 
		implements Runnable {

	private static final Logger logger = 
		LoggerFactory.getLogger(SendMailJob.class);

	/**
	 * @oddjob.property
	 * @oddjob.description The name of the job. Any text.
	 * @oddjob.required No.
	 */
	private String name;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The host name of the server.
	 * @oddjob.required Yes.
	 */
	private String host;

	/**
	 * @oddjob.property
	 * @oddjob.description The port of the server.
	 * @oddjob.required Yes.
	 */
	private int port;

	/**
	 * @oddjob.property
	 * @oddjob.description The from address.
	 * @oddjob.required Yes.
	 */
	private String from;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The subject.
	 * @oddjob.required No. Defaults to (no subject).
	 */
	private String subject = "(no subject)";
	
	/**
	 * @oddjob.property
	 * @oddjob.description The to addresses. A semicolon delimited list.
	 * @oddjob.required Yes.
	 */
	private String to;

	/**
	 * @oddjob.property
	 * @oddjob.description The message body as text.
	 * @oddjob.required No.
	 */
	@ArooaText
	private String message;
	
	/**
	 * @oddjob.property
	 * @oddjob.description A list of attachment files.
	 * @oddjob.required No.
	 */
	private File[] files;

	/**
	 * @oddjob.property
	 * @oddjob.description The Username for authentication.
	 * @oddjob.required No.
	 */
	private String username;

	/**
	 * @oddjob.property
	 * @oddjob.description The Password for authentication.
	 * @oddjob.required No.
	 */
	@ArooaAttribute
	private byte[] password;

	/**
	 * @oddjob.property
	 * @oddjob.description Use SSL for communication with the mail host.
	 * @oddjob.required No.
	 */
	private boolean ssl;

	/**
	 * @oddjob.property
	 * @oddjob.description Turn detailed debug messages on.
	 * @oddjob.required No.
	 */
	private boolean debug;

  	public void run() {

	    if (to == null) {
	    	throw new IllegalStateException("No To set.");
	    }
	    if (host == null) {
	    	throw new IllegalStateException("No host set.");
	    }

	    try {
	  		Properties properties = new Properties();
		    properties.put("mail.smtp.host", host);
		    if (port > 0) {
				properties.put("mail.smtp.port", Integer.toString(port));
			}
			if (ssl) {
				properties.put("mail.smtp.ssl.enable", "true");
			}

		    Session session;
			if (username == null) {
				session = Session.getInstance(properties);
			}
			else {
				logger.info("Using username [{}] and {}.", username,
						password == null ? "no password" : "a password");

				properties.put("mail.smtp.auth", "true");
				session = Session.getInstance(properties,
						new Authenticator() {
							@Override
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(username,
										Optional.of(password).
												map(p -> new String(p, StandardCharsets.UTF_8))
												.orElse(null));
							}
						});
			}

			if (debug) {
				session.setDebug(true);
			}

		    String[] toList = to.split("\\s*;\\s*");
		    
		    InternetAddress[] addresses = new InternetAddress[toList.length];
			for (int i = 0; i < addresses.length; ++i) {
				addresses[i] = new InternetAddress(toList[i]);
			}
	
			logger.info("Sending mail from [{}] to [{}] using host {}:{}.", from, to, host,
					port == 0 ? "(default port)" : String.valueOf(port));
			
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			
			message.setRecipients(Message.RecipientType.TO, addresses);
			message.setSubject(subject);
			message.setSentDate(new Date());
					
			Multipart multipart = new MimeMultipart();

			if (this.message != null) {
				TextPart part = new TextPart();
				part.setText(this.message);
				multipart.addBodyPart(part.toValue());
			}
			
			if (files != null) {
				for (File file : files) {
					logger.info("Adding attachment: " + file);
					AttachmentPart part = new AttachmentPart();
					part.setFile(file);
					multipart.addBodyPart(part.toValue());
				}
			}
			
			message.setContent(multipart);
			
			message.saveChanges();
	
			Transport transport = session.getTransport("smtp");
			transport.connect();
	
			transport.addTransportListener(new DebugListener());
			transport.sendMessage(message, addresses);
	    }
	    catch (ArooaConversionException | MessagingException e) {
	    	throw new RuntimeException(e);
	    }

		logger.info("Mail sent.");
 	}

	static class DebugListener implements TransportListener {
		
		@Override
		public void messagePartiallyDelivered(TransportEvent e) {
			logger.info("mail partially delivered: " + e);		
		}
		
		@Override
		public void messageNotDelivered(TransportEvent e) {
			logger.info("mail not delivered: " + e);		
		}
		
		@Override
		public void messageDelivered(TransportEvent e) {
			logger.info("mail delivered: " + e);		
		}
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public File[] getFiles() {
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public byte[] getPassword() {
		return password;
	}

	public void setPassword(byte[] password) {
		this.password = password;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		if (name == null) {
			return getClass().getSimpleName();
		}
		else {
			return name;
		}
	}


}
