package com.hantsylabs.example.maildemo.resource;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.jboss.seam.jms.BuilderFactory;
import org.jboss.seam.jms.QueueBuilder;
import org.jboss.seam.jms.annotations.JmsDefault;

public class JmsResourceProducer {
	@Inject
	BuilderFactory factory;

	@Resource(mappedName = "queue/test")
	Queue queue;

	@Resource(mappedName = "java:/ConnectionFactory")
	ConnectionFactory cf;

	@Produces
	@MailQueue
	public QueueBuilder queueBuilder() {

		return factory.newQueueBuilder().connectionFactory(cf)
				.destination(queue);

	}
}
