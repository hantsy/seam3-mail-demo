package com.hantsylabs.example.maildemo.mail;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hantsylabs.example.maildemo.model.EmailMessage;

@MessageDriven(name = "mailProcessorMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/test") }, mappedName = "java:/queue/test")
public class MailProcessorMDB implements MessageListener {
	private static final Logger log = LoggerFactory
			.getLogger(MailProcessorMDB.class);

	@EJB
	private MailProcessor processor;

	@Override
	public void onMessage(Message message) {
		if (log.isDebugEnabled()) {
			log.debug("receiving message from client...delegating to mail processor");
		}

		Connection connection = null;
		try {
			ObjectMessage om = (ObjectMessage) message;
			EmailMessage _message = (EmailMessage) om.getObject();
			processor.send(_message);
		} catch (JMSException e) {
			log.error(e.getMessage());
		}
	}
}
