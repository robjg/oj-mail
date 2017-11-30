package org.oddjob.mail;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaText;

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
	private String message;
	
	/**
	 * @oddjob.property
	 * @oddjob.description An list of attachment files.
	 * @oddjob.required No.
	 */
	private File[] files;
	

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
		    Session session = Session.getInstance(properties);
//			session.setDebug(true);

		    String[] toList = to.split("\\s*;\\s*");
		    
		    InternetAddress[] addresses = new InternetAddress[toList.length];
			for (int i = 0; i < addresses.length; ++i) {
				addresses[i] = new InternetAddress(toList[i]);
			}
	
			logger.info("Sending mail.");
			
			MimeMessage messsage = new MimeMessage(session);
			messsage.setFrom(new InternetAddress(from));
			
			messsage.setRecipients(Message.RecipientType.TO, addresses);
			messsage.setSubject(subject);
			messsage.setSentDate(new Date());
					
			Multipart multipart = new MimeMultipart();

			if (message != null) {
				TextPart part = new TextPart();
				part.setText(message);
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
			
			messsage.setContent(multipart);
			
			messsage.saveChanges();
	
			Transport transport = session.getTransport("smtp");
			transport.connect();
	
			transport.addTransportListener(new DebugListener());		
			transport.sendMessage(messsage, addresses);
	    }
	    catch (ArooaConversionException e) {
	    	throw new RuntimeException(e);
	    }
	    catch (MessagingException e) {
	    	throw new RuntimeException(e);
	    }
	    
		logger.info("Mail sent.");
 	}

	class DebugListener implements TransportListener {
		
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

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getMessage() {
		return message;
	}

	@ArooaText
	public void setMessage(String message) {
		this.message = message;
	}

	public File[] getFiles() {
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
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
