package com.hantsylabs.example.maildemo.mail;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mail.Session;

import org.jboss.seam.mail.api.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hantsylabs.example.maildemo.model.EmailMessage;

@Stateless
public class MailProcessor {
	private static final Logger log = LoggerFactory
			.getLogger(MailProcessor.class);

	@Inject
	private transient Instance<MailMessage> mailMessage;

	@Inject
	private transient Instance<Session> session;

	public void send(EmailMessage message) {
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
}
