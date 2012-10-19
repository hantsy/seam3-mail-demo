package com.hantsylabs.example.maildemo.mail;

import javax.enterprise.event.Observes;
import javax.jms.Queue;

import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.annotations.Outbound;

import com.hantsylabs.example.maildemo.model.EmailMessage;
import com.hantsylabs.example.maildemo.view.NoneBlocking;

public interface CdiJmsMapping {
	@Outbound
	public void mapStatusToQueue(@Observes @NoneBlocking EmailMessage message,
			@JmsDestination(jndiName = "java:/queue/test") Queue q);
}
