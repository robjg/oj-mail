package org.oddjob.mail;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.types.ValueFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.io.File;

public class AttachmentPart implements ValueFactory<MimeBodyPart>{

	private File file;

	@Override
	public MimeBodyPart toValue() throws ArooaConversionException {
		
		MimeBodyPart bodyPart = new MimeBodyPart();
		DataSource ds = new FileDataSource(file);
		
		
		try {
			bodyPart.setDataHandler(new DataHandler(ds));
		    bodyPart.setFileName(file.getName());
		} catch (MessagingException e) {
			throw new ArooaConversionException(e);
		}

		return bodyPart;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
