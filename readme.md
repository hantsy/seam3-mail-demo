#Send email with Seam 3 Mail and JMS

JavaLobby post link [http://java.dzone.com/articles/send-email-seam-3-mail-and-jms](http://java.dzone.com/articles/send-email-seam-3-mail-and-jms)

Seam 3 Mail module provides simple API to use Java Mail API to send email message.
  
##Basic Configuration
  
Assume you have already created a Maven based Java EE 6 application. 

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
  
Add basic mail configuration in your *META-INF/seam-beans.xml*. 
  
  	<mail:MailConfig serverHost="smtp.gmail.com"
		serverPort="587" 
		auth="true" 
		enableTls="true" 
		username="<your gmail account>"
		password="<your password>">
		<ee:modifies />
	</mail:MailConfig>
 
In your Java codes, inject `MailMessage` and `Session`.
  
  	@Inject
	private transient Instance<MailMessage> mailMessage;

	@Inject
	private transient Instance<Session> session;
  
 `MailMessage` is provided by Seam3 module, it is a fluid API to build message object and send message. `Session` is from the standard Java Mail API.
  
   	MailMessage msg = mailMessage.get();
	msg.subject("test subject")
		.from("test@test.com")
		.to("user@test.com")
		.send();

Everything works well.

But if you try to send an email from jsf pages, the page will be blocked when the email is being sent. 
  
EJB 3.1 provides a simple way to execute asynchronous action. You can simply create a `@Stateless` EJB and put the logic in a `@Asynchronous` method. But unfortunately  when you try to use @Asynchronous and CDI together, it does not work.
  
JMS provides standard asynchronous processing capability for Java EE, Seam3 also includes a JMS module.

##Send email asynchronously with JMS
  
Seam 3 also provides a JMS module which simplify JMS and CDI integration, we can utilize JMS to process the asynchronous work.
  
Configure the JMS connection factory in your *META-INF/seam-beans.xml*.
  
  	<jmsi:JmsConnectionFactoryProducer>
		<ee:modifies />
		<jmsi:connectionFactoryJNDILocation>
			java:/ConnectionFactory
		</jmsi:connectionFactoryJNDILocation>
	</jmsi:JmsConnectionFactoryProducer>
  	
Create a standard JMS listener to handle JMS message.
  
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
  
Use an interface to observe the CDI event and route it to the JMS queue.
  
  	@Outbound
	public void mapStatusToQueue(@Observes @NoneBlocking EmailMessage message,
			@JmsDestination(jndiName = "java:/queue/test") Queue q);
  
In the presentation layer, fire an event directly. The event object will be routed as the payload of JMS message automatically.
   
 	messageEventSrc.fire(message);

  
##Run the project

I assume you have installed the latest Oracle JDK 7, JBoss AS 7.1.1.Final and Apache Maven 3.0.4.
  
  1. Check out the complete codes from github.com. 
  
  		git clone git://github.com/hantsy/seam3-mail-demo.git
  	
  2. Start JBoss AS with standalone full profile which includes JMS support.
  	
  		<JBOSS_HOME>\bin\standalone.bat --server-config=standalone-full.xml
  	
  3. Deploy the application into the running JBoss AS.
  
  		mvn clean package jboss-as:deploy
  	
  4. Open your browser and go to http://localhost:8080/seam3-mail-demo.
  	  
