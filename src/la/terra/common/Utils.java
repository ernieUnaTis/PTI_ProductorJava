/*
 * Util.java
 *
 * Created on 2 de abril de 2007, 03:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package la.terra.common;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static org.apache.commons.lang.StringUtils.leftPad;


import java.io.PrintWriter;
import java.io.StringWriter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;



public class Utils {

	/**
	 * Convierte una excepcion en un mensaje del stack tipo String
	 * @param e La excepcion de la cual se extraera el stackTrace
	 * @return un String conteniendo el stackTrace
	 */
	public static String getStackTraceAsString(Exception e) {
		StringWriter stackTrace = new StringWriter();
		e.printStackTrace(new PrintWriter(stackTrace));
		return stackTrace.toString();

	}
	
	    public static String politicaMovil(String movil,String carrier){
	        carrier="722200";
	        if( carrier.equalsIgnoreCase("722200")){ 
	            if(!movil.startsWith("54")){
	                movil = "54" + movil;
	            }
	        }

	        return movil;
	    }
	
	/**
	 * Genera id interno
	 * emartine 09-09-2014
	 * @return
	 */
	public static String generarIdInterno(){
		int longitud = 20;
		String correlator = "";

		StringBuffer sb = new StringBuffer();     
		for (int i = longitud; i > 0; i -= 12) {       
			int n = min(12, abs(i));       
			sb.append(leftPad(Long.toString(round(random() * pow(36, n)), 36), n, '0'));     
		}     

		correlator = sb.toString();

		//System.out.println("Aleatorio: "+ cadenaAleatoria);

		return correlator;

	}
	
	

	/**
	 * Genera id aleatorio
	 * emartine
	 * @return
	 */
	public static String generaCorrelator(){
		int longitud = 20;
		String cid = "";

		StringBuffer sb = new StringBuffer();     
		for (int i = longitud; i > 0; i -= 12) {       
			int n = min(12, abs(i));       
			sb.append(leftPad(Long.toString(round(random() * pow(36, n)), 36), n, "0"));     
		}     

		cid = sb.toString();
		return cid;
	}
	
	public String enviarArtemis() throws JMSException, NamingException {
	      Connection connection = null;
	      InitialContext initialContext = null;
	      try {
	         // /Step 1. Create an initial context to perform the JNDI lookup.
	         initialContext = new InitialContext();

	         // Step 2. perform a lookup on the topic
	         Topic topic = (Topic) initialContext.lookup("topic/exampleTopic");

	         // Step 3. perform a lookup on the Connection Factory
	         ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");

	         // Step 4. Create a JMS Connection
	         connection = cf.createConnection();

	         // Step 5. Create a JMS Session
	         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

	         // Step 6. Create a Message Producer
	         MessageProducer producer = session.createProducer(topic);


	         // Step 9. Create a Text Message
	         TextMessage message = session.createTextMessage("This is a text message");

	         System.out.println("Sent message: " + message.getText());

	         // Step 10. Send the Message
	         producer.send(message);

	         // Step 11. Start the Connection
	         connection.start();

	      } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	         // Step 14. Be sure to close our JMS resources!
	         if (connection != null) {
	            connection.close();
	         }

	         // Also the initialContext
	         if (initialContext != null) {
	            initialContext.close();
	         }
	      }
	      return "OK";
	}
	
	
	
}