package org.oddjob.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaText;
import org.oddjob.arooa.types.ValueFactory;

public class TextPart implements ValueFactory<MimeBodyPart>{

	private String text;
	
	@Override
	public MimeBodyPart toValue() throws ArooaConversionException {
		MimeBodyPart bodyPart = new MimeBodyPart();
		
		try {
			bodyPart.setText(text);
		} catch (MessagingException e) {
			throw new ArooaConversionException(e);
		}
		
		return bodyPart;
	}

	public String getText() {
		return text;
	}

	@ArooaText
	public void setText(String text) {
		this.text = text;
	}
}
