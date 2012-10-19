#Send email with Seam 3 Mail and JMS


 Seam 3 Mail module provides simple API to use Java Mail API to send email message.
  
##Create a Java EE 6 application.
  
 Add seam mail dependency to your *pom.xml* file.
  
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
  
 Add mail configuration in *META-INF/seam-beans.xml*. 
  
  	<mail:MailConfig serverHost="smtp.gmail.com"
		serverPort="587" 
		auth="true" 
		enableTls="true" 
		username="<your gmail account>"
		password="<your password>">
		<ee:modifies />
	</mail:MailConfig>
 
 Inject `MailMessage` and java mail `Session`.
  
  	@Inject
	private transient Instance<MailMessage> mailMessage;

	@Inject
	private transient Instance<Session> session;
  
 `MailMessage` is fluid API to build message object and  send message.
  
   	MailMessage msg = mailMessage.get();
	msg.subject("test subject")
		.from("test@test.com")
		.to("user@test.com")
		.send();

 But when you try to send an email from jsf pages, the page will be blocked when the email is being sent. 
  
 You maybe know in EJB, you can use a `@Stateless` EJB and put the logic in a `@Asynchronous` method to do some work asynchronously. But but it still does not work, there is an issue when use @Asynchronous with CDI context.
  
##Send email asynchronously with JMS
  
 Seam 3 also provides a JMS module which simplify JMS and CDI integration.
  
 Configure the JMS connection factory.
  
  	<jmsi:JmsConnectionFactoryProducer>
		<ee:modifies />
		<jmsi:connectionFactoryJNDILocation>
			java:/ConnectionFactory
		</jmsi:connectionFactoryJNDILocation>
	</jmsi:JmsConnectionFactoryProducer>
  	
 Create a jms listener to process message when the message object is arrived.
  
  	@MessageDriven(....)
	public class MailProcessorMDB extends MessageListener {
	
	    @EJB MailProcessor processor;
		
		public void send()...//
		
		@Override
		protected void onMessage(Message _msg) throws JMSException {
			if (log.isDebugEnabled()) {
				log.debug("call handleMessage...");
			}
	
			ObjectMessage objMessage = (ObjectMessage) _msg;
			EmailMessage mailMessage = (EmailMessage) objMessage.getObject();
			processor.send(mailMessage);
		}
	}
  
 `MailProcessor` is a `@Stateless` EJB which is use for sending mail.
 
 When a message is arrived, the listener will call `MailProcessor` to process email.
  
 Use a interface to observe the CDI event and route to the JMS queue.
  
  	@Outbound
	public void mapStatusToQueue(@Observes @NoneBlocking EmailMessage message,
			@JmsDestination(jndiName = "java:/queue/test") Queue q);
  
 In the presentation layer, fire an event directly. The event object will be routed as payload of JMS message automatically.
   
   messageEventSrc.fire(message);

  
##Run the project

 I assume you have installed the latest Oracle JDK 7, JBoss AS 7.1.1.Final and Apache Maven 3.0.4.
  
  1.Check out the complete codes from github.com. 
  
  		git clone git://github.com/hantsy/seam3-mail-demo.git
  	
  2.Start JBoss AS with standalone full profile.
  	
  		<JBOSS_HOME>\bin\standalone.bat --server-config=standalone-full.xml
  	
  3.Deploy the application into the running JBoss AS.
  
  		mvn clean package jboss-as:deploy
  	
  4.Open your browser and go to http://localhost:8080/seam3-mail-demo.
  	  