package org.oddjob.mail;

import org.oddjob.Oddjob;
import org.oddjob.arooa.xml.XMLConfiguration;

public class SendMailMain {

	public void sendText() {
	
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				"org/oddjob/mail/SendMailExample.xml", 
				getClass().getClassLoader()));
	
		oddjob.run();
		
		oddjob.destroy();
			
	}

	public void sendAttachments() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				"org/oddjob/mail/SendMailAttachments.xml", 
				getClass().getClassLoader()));
	
		oddjob.run();
		
		oddjob.destroy();
	}
	
	public static void main(String... args) {
		
		SendMailMain send = new SendMailMain();
		send.sendAttachments();
	}
	
}
