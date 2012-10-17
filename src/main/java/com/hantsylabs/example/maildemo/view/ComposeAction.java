package com.hantsylabs.example.maildemo.view;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.mail.templating.velocity.CDIVelocityContext;
import org.jboss.solder.resourceLoader.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hantsylabs.example.maildemo.mail.MailSender;
import com.hantsylabs.example.maildemo.model.WebMessage;

/**
 * Session Bean implementation class ReportAction
 */
@Stateful
@ConversationScoped
@Named(value = "composeAction")
public class ComposeAction {
	private static final Logger log = LoggerFactory
			.getLogger(ComposeAction.class);

	@Inject
	private ResourceProvider resourceProvider;

	@Inject
	FacesContext facesContext;

	@Inject
	private transient CDIVelocityContext velocityContext;

	@Inject
	private Conversation conversation;

	@Inject
	private MailSender sender;

	private WebMessage message;

	public WebMessage getMessage() {
		return message;
	}

	public void setMessage(WebMessage message) {
		this.message = message;
	}

	/**
	 * Default constructor.
	 */
	public ComposeAction() {
	}

	public void select() {
		if (log.isDebugEnabled()) {
			log.debug("call select...");
		}

		if (conversation.isTransient()) {
			conversation.begin();
		}
		this.message = new WebMessage();
	}

	public void send() {
		if (log.isDebugEnabled()) {
			log.debug("send...");
		}
       
	    sender.sendToQueue(message);
	}
}
