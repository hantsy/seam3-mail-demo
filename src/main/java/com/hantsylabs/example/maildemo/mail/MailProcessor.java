package com.hantsylabs.example.maildemo.mail;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.mail.Session;

import org.jboss.seam.jms.AbstractMessageListener;
import org.jboss.seam.mail.api.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hantsylabs.example.maildemo.model.WebMessage;

@SessionScoped
@Named
public class MailProcessor extends AbstractMessageListener implements
		Serializable {
	private static final Logger log = LoggerFactory
			.getLogger(MailProcessor.class);

	@Inject
	private transient Instance<MailMessage> mailMessage;

	@Inject
	private transient Instance<Session> session;

	private void send(WebMessage message) {
		if (log.isDebugEnabled()) {
			log.debug("send email@ message object->" + message);
		}

		MailMessage msg = mailMessage.get();
		msg.subject(message.getSubject());

		if (message.getFrom() != null) {
			msg.from(message.getFrom());
		}

		msg.to(message.getTo());

		if (message.getHtmlContent() != null
				&& message.getHtmlContent().trim().length() > 0) {
			msg.bodyHtmlTextAlt(message.getHtmlContent(), message.getContent());
		} else {
			msg.bodyText(message.getContent());
		}

		msg.send(session.get());

	}

	@Override
	protected void handleMessage(Message _msg) throws JMSException {
		if (log.isDebugEnabled()) {
			log.debug("call handleMessage...");
		}

		ObjectMessage objMessage = (ObjectMessage) _msg;
		WebMessage mailMessage = (WebMessage) objMessage.getObject();
		send(mailMessage);

	}
}
