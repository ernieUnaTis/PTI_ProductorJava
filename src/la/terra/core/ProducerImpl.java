/*
 * 
 */
package la.terra.core;


import java.util.Hashtable;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

import la.terra.common.LogLocal;


/**
 * A simple tool for publishing messages
 * 
 * @version $Revision: 1.2 $
 */
public class ProducerImpl{



        private Properties propiedades;    
        //private Logger log = Logger.getLogger(this.getClass().getSimpleName());

        private Destination destination;         
        private long sleepTime=0;           
        private int messageSize=255;       
        private String user="system";
        private String password="manager";
        private String url="tcp://localhost:61616";
        private String subject="";
        private boolean topic=false;
        private boolean transacted=false;
        private boolean persistent=true;
        private String cola="";
        private Hashtable propiedadesMensaje;

        // Crea La conexion 
        public  ProducerImpl(){}

        protected  ActiveMQConnectionFactory ConectionMasterMQ(String user, String password, String url) throws JMSException{
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
                connectionFactory.setUseAsyncSend(true);
                return connectionFactory;       
        }

        protected  Session SesionMasterMQ(Connection connection, String subject) throws JMSException{

                Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
                destination = session.createQueue(subject);
                return session;
        }

        protected  MessageProducer ProductorMasterMQ(Connection connection) throws JMSException{

                MessageProducer producer = SesionMasterMQ( connection,this.subject).createProducer(destination);
                if (persistent) {
                        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
                } else {
                        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                }

                return producer;
        }

        public void enviar(TextMessage Msg) {
                Connection connection = null;
                try {

                        connection = ConectionMasterMQ( this.user,  
                                        this.password, 
                                        this.url
                                        ).createConnection();

                        connection.start();
                        this.subject= "json." + Msg.getStringProperty("carrier");
                        this.toQueue(SesionMasterMQ( connection , 
                                        this.subject),
                                        ProductorMasterMQ( connection),  Msg );            

                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        try {
                                connection.close();
                        } catch (Throwable e) {
                                e.printStackTrace();
                        }
                }
        }

        public void enviar(String carrier, String msisdn, String operador,String idInterno, LogLocal logger) throws Exception{
                Connection connection = null;
                try {

                        connection = this.ConectionMasterMQ( this.user,  
                                        this.password, 
                                        this.url
                                        ).createConnection();
                        this.subject= carrier+".reciclaje";
                        connection.start();
                        this.toQueue(SesionMasterMQ( connection , 
                                        this.subject),
                                        ProductorMasterMQ( connection), carrier, msisdn,operador, idInterno);            
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception();
                } finally {
                        try {
                                connection.close();
                        } catch (Throwable e) {
                                e.printStackTrace();
                        }
                }
        }
        
      
        
        public void enviar() {
                Connection connection = null;
                try {

                        connection = this.ConectionMasterMQ( this.user,  
                                        this.password, 
                                        this.url
                                        ).createConnection();

                        connection.start();

                        this.toQueue(SesionMasterMQ( connection , 
                                        this.subject),
                                        ProductorMasterMQ( connection),this.subject );            
                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        try {
                                connection.close();
                        } catch (Throwable e) {
                                e.printStackTrace();
                        }
                }
        }

        protected void toQueue(Session session, MessageProducer producer , String Mensaje) throws Exception {

                TextMessage message=null;
                message = session.createTextMessage(Mensaje);    
                try{
                        producer.send(message);
                }
                catch (Exception e) 
                {
                        e.printStackTrace();
                        producer.close();
                        throw new Exception();
                }

                if (this.transacted) {
                        session.commit();
                }
                producer.close();
                Thread.sleep(this.sleepTime);

        }

        protected void toQueue(Session session, MessageProducer producer , TextMessage Mensaje) throws Exception {

                try{
                        producer.send(Mensaje);                

                }catch (Exception e) 
                {
                        e.printStackTrace();
                        producer.close();
                }

                if (this.transacted) {
                        session.commit();
                }
                producer.close();
                Thread.sleep(this.sleepTime);

        }
        
        protected void toQueue(Session session, MessageProducer producer , String carrier, String msisdn,String operador,String idInterno) throws Exception {

                TextMessage message=null;
                message = session.createTextMessage();
        
                try{
                        message.setStringProperty("carrier", carrier);
                        message.setStringProperty("msisdn", msisdn);
                        message.setStringProperty("operador", operador);
                        message.setStringProperty("idInterno", idInterno);
                        producer.send(message);                
                }
                catch (Exception e) 
                {
                        e.printStackTrace();
                        producer.close();
                        throw new Exception();
                }

                if (this.transacted) {
                        session.commit();
                }
                producer.close();
                Thread.sleep(this.sleepTime);

        }

        
        /* (non-Javadoc)
         * @see cl.terra.bridgemq.componentes.agentesmq.impl.IProductor#setPropiedades(java.util.Properties)
         */
        public void setPropiedades(Properties propiedades) {

                this.propiedades= propiedades;

                try{
                        this.transacted =(this.propiedades.containsKey(this.cola+".transacted"))?
                                        (Boolean.valueOf(this.propiedades.get(this.cola+".transacted").toString()).booleanValue()):
                                                (false);

                                        this.topic      = false;

                                        this.persistent =(this.propiedades.containsKey(this.cola+".persistent"))?
                                                        (Boolean.valueOf(this.propiedades.get(this.cola+".persistent").toString()).booleanValue()):
                                                                (true);

                                                        this.subject    =(this.propiedades.containsKey(this.cola+".subject"))?
                                                                        (this.propiedades.get(this.cola+".subject").toString()):
                                                                                ("DEFECTO_NO_CONFIGURADA");

                                                                        this.url                =(this.propiedades.containsKey(this.cola+".url"))?
                                                                                        (this.propiedades.get(this.cola+".url").toString()):
                                                                                                ( ActiveMQConnection.DEFAULT_BROKER_URL);

                                                                                        this.user               =(this.propiedades.containsKey(this.cola+".user"))?
                                                                                                        (this.propiedades.get(this.cola+".user").toString()):
                                                                                                                (ActiveMQConnection.DEFAULT_USER);

                                                                                                        this.password   =(this.propiedades.containsKey(this.cola+".password"))?
                                                                                                                        (this.propiedades.get(this.cola+".password").toString()):
                                                                                                                                (ActiveMQConnection.DEFAULT_PASSWORD);

                                                                                                                        this.sleepTime  =(this.propiedades.containsKey(this.cola+".sleepTime"))?
                                                                                                                                        (Long.parseLong( this.propiedades.get(this.cola+".sleepTime").toString())):
                                                                                                                                                (0);

                                                                                                                                        this.messageSize=(this.propiedades.containsKey(this.cola+".messageSize"))?
                                                                                                                                                        (Integer.valueOf(this.propiedades.get(this.cola+".messageSize").toString()).intValue()):
                                                                                                                                                                (255);

                }catch(Exception e){
                        e.printStackTrace();
                }
        }

        public Properties getPropiedades() {
                return propiedades;
        }
        public void setDestination(Destination destination) {
                this.destination = destination;
        }
        public void setSleepTime(long sleepTime) {
                this.sleepTime = sleepTime;
        }
        public void setMessageSize(int messageSize) {
                this.messageSize = messageSize;
        }
        public void setUser(String user) {
                this.user = user;
        }
        public void setPassword(String password) {
                this.password = password;
        }
        public void setUrl(String url) {
                this.url = url;
        }
        public void setSubject(String subject) {
                this.subject = subject;
        }
        public void setTopic(boolean topic) {
                this.topic = topic;
        }
        public void setTransacted(boolean transacted) {
                this.transacted = transacted;
        }
        public void setPersistent(boolean persistent) {
                this.persistent = persistent;
        }
        public void setCola(String cola) {
                this.cola = cola;
        }
        public Destination getDestination() {
                return destination;
        }
        public long getSleepTime() {
                return sleepTime;
        }
        public int getMessageSize() {
                return messageSize;
        }
        public String getUser() {
                return user;
        }
        public String getPassword() {
                return password;
        }
        public String getUrl() {
                return url;
        }
        public String getSubject() {
                return subject;
        }
        public boolean isTopic() {
                return topic;
        }
        public boolean isTransacted() {
                return transacted;
        }
        public boolean isPersistent() {
                return persistent;
        }
        public String getCola() {
                return cola;
        }
        public void addStringPropertieToMessage(String key, String value) {

                this.propiedadesMensaje=(this.propiedadesMensaje!=null)?(this.propiedadesMensaje):(new Hashtable());

                this.propiedadesMensaje.put(key, value);                
        }
        public Hashtable getPropiedadesMensaje() {
                return propiedadesMensaje;
        }
        public void setPropiedadesMensaje(Hashtable propiedadesMensaje) {
                this.propiedadesMensaje = propiedadesMensaje;
        }
}
