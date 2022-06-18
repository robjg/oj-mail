package org.oddjob.mail;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.state.ParentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.server.SMTPServer;

import java.io.*;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;


public class SendMailJobTest extends Assert {

	private static final Logger logger = LoggerFactory.getLogger(SendMailJobTest.class);

    @Test
	public void testSendMailExample() {

		MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory();

		SMTPServer smtpServer = new SMTPServer(myFactory);
		smtpServer.setPort(25000);
		smtpServer.start();

		Properties properties = new Properties();

		properties.setProperty("my.alert.from", "me@foo.com");
		properties.setProperty("my.alert.to", "you@foo.com");
		properties.setProperty("my.mail.port", "25000");
		properties.setProperty("my.mail.host", "localhost");

		File config = new File(getClass().getResource("SendMailExample.xml").getFile());

		Oddjob oddjob = new Oddjob();
		oddjob.setFile(config);
		oddjob.setProperties(properties);

		oddjob.run();

		smtpServer.stop();

		assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

		assertThat(myFactory.messageCount, is(1));
	}


	public static class MyMessageHandlerFactory implements MessageHandlerFactory {

		int messageCount;

		public MessageHandler create(MessageContext ctx) {
			return new Handler(ctx);
		}

		class Handler implements MessageHandler {
			MessageContext ctx;

			public Handler(MessageContext ctx) {
				this.ctx = ctx;
			}

			public void from(String from) throws RejectException {
				logger.info("FROM:" + from);
			}

			public void recipient(String recipient) throws RejectException {
				logger.info("RECIPIENT:" + recipient);
			}

			public void data(InputStream data) throws IOException {
				logger.info("MAIL DATA");
				logger.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
				logger.info(this.convertStreamToString(data));
				logger.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
			}

			public void done() {
				logger.info("Finished");
				++messageCount;
			}

			public String convertStreamToString(InputStream is) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return sb.toString();
			}

		}
	}
}
