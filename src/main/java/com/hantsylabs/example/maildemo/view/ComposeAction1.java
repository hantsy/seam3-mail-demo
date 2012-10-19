package com.hantsylabs.example.maildemo.view;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hantsylabs.example.maildemo.mail.MailProcessor;
import com.hantsylabs.example.maildemo.model.EmailMessage;

@RequestScoped
@Named(value = "composeAction1")
public class ComposeAction1 {
	private static final Logger log = LoggerFactory
			.getLogger(ComposeAction1.class);

	@Inject
	FacesContext facesContext;

	@Inject
	Messages messages;

	@Inject
	@Blocking
	Event<EmailMessage> messageEventSrc;

	@Inject
	MailProcessor processor;

	private EmailMessage message;

	public EmailMessage getMessage() {
		return message;
	}

	public void setMessage(EmailMessage message) {
		this.message = message;
	}

	/**
	 * Default constructor.
	 */
	public ComposeAction1() {
	}

	@PostConstruct
	public void init() {
		this.message = new EmailMessage();
	}


	public void send() {
		if (log.isDebugEnabled()) {
			log.debug("send...@" + message);
		}

		processor.send(message);
		messageEventSrc.fire(message);

	}

	public void onSaved(@Observes @Blocking EmailMessage _message) {
		messages.info(new BundleKey("messages", "email.sent"))
				.defaults("Email message was sent successfully...").build();
	}
}
