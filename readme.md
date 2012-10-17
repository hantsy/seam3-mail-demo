Send email with Seam 3 Mail and JMS
==================================

 
  Seam 3 Mail module provides simple API to use Java Mail API to send email message.
  
  Configure mail is simple.
  
  Add seam mail dependency to your pom.xml file.
  
  	<dependency>
		<groupId>org.jboss.seam.mail</groupId>
		<artifactId>seam-mail-api</artifactId>
		<scope>compile</scope>
	</dependency>
	<dependency>
		<groupId>org.jboss.seam.mail</groupId>
		<artifactId>seam-mail</artifactId>
		<scope>compile</scope>
	</dependency>
  
  Add mail configuration in META-INF/seam-beans.xml. 
  
  	<mail:MailConfig serverHost="smtp.gmail.com"
		serverPort="587" auth="true" enableTls="true" username="frankouz.ramirez@gmail.com"
		password="abcd2010">
		<ee:modifies />
	</mail:MailConfig>
  
  Inject MailMessage and java mail Session.
  
  	@Inject
	private transient Instance<MailMessage> mailMessage;

	@Inject
	private transient Instance<Session> session;
  
  MailMessage is fluid API to build message object and  send message.
  
   	MailMessage msg = mailMessage.get();
	msg.subject("test subject")
		.from("test@test.com")
		.to("user@test.com")
		.send();

  But when send email from jsf pages, the page will be blocked when the email is being sent. 
  
  Create a @Stateless EJB and put the logic in a @Asynchronous, but it still does not work, there is an issue when use @Asynchronous with CDI context.
  
  ## Send email asynchronously with JMS
  
  Seam 3 also provides a JMS module which simplify JMS and CDI integration.
  
  Configure the JMS connection factory.
  
  	<jmsi:JmsConnectionFactoryProducer>
		<ee:modifies />
		<jmsi:connectionFactoryJNDILocation>java:/ConnectionFactory</jmsi:connectionFactoryJNDILocation>
	</jmsi:JmsConnectionFactoryProducer>
  	
  Create a jms listener to process message when the message object is arrived.
  
  	@SessionScoped
	@Named
	public class MailProcessor extends AbstractMessageListener implements Serializable {
		
		public void send()...//
		
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
  
  AbstractMessageListener is a specified JMS listener provided by Seam3 JMS module.
  
  Send the message object to JMS queue.
  
  	@Inject
	@MailQueue
	QueueBuilder queueBuilder;
	
	public void sendToQueue(WebMessage message) {
		queueBuilder.sendObject(message);

	}
  
   QueueBuilder is an utility class provided by Seam 3 JMS module, we can use a @Produces to configure a application specified QueueBuilder.
   
   	@Produces
	@MailQueue
	public QueueBuilder queueBuilder() {

		return factory.newQueueBuilder().connectionFactory(cf)
				.destination(queue);

	} 
  
  Check out the complete code from github.com, have a try yourself. 
  
  