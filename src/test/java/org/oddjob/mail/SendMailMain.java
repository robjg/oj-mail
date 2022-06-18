package org.oddjob.mail;

import org.oddjob.Main;
import org.oddjob.Oddjob;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class SendMailMain {

	public void sendText() throws IOException {

		Properties properties = Objects.requireNonNull(Main.loadUserProperties());

		Oddjob oddjob = new Oddjob();
		oddjob.setProperties(properties);
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
	
	public static void main(String... args) throws IOException {
		
		SendMailMain send = new SendMailMain();
		send.sendText();
	}
	
}
