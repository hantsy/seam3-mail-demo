package com.hantsylabs.example.maildemo.mail;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.jms.QueueBuilder;

import com.hantsylabs.example.maildemo.model.WebMessage;
import com.hantsylabs.example.maildemo.resource.MailQueue;

@Named
public class MailSender {
	
	@Inject
	@MailQueue
	QueueBuilder queueBuilder;
	
	public void sendToQueue(WebMessage message) {
		queueBuilder.sendObject(message);

	}
}
