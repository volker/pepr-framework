/**
 * Copyright 2009 pepr Framework
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.peprframework.activities.activemq;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.peprframework.core.Activity;
import org.peprframework.core.Context;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name="JmsProducer")
public class Producer extends Activity<JmsPayload,Void,JmsConfiguration> {

	public static final String ID = "org.peprframework.activities.activemq.producer";
	
	private transient Topic topic = null;
	
	private transient Connection connection = null;
	
	private transient Session session = null;
	
	private transient MessageProducer producer = null;
	
	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}
	
	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#onMessage(org.peprframework.core.Context)
	 */
	@Override
	public void onMessage(Context ctx) {
		JmsPayload input = createInput();
		input = inputFilter.filter(input, ctx);
		
		JmsPayloadContainer container = new JmsPayloadContainer();
		container.payload = input.payload;
		container.timestamp = ctx.getTimestamp();
		container.number = ctx.getContextNumber();
		
		try {
			ObjectMessage objectMessage = session.createObjectMessage(container);
			this.producer.send(objectMessage);
		} catch (JMSException ex) {
			ex.printStackTrace();
		}
		
		ctx.setLatestActivity(name);
		callback.publish(ctx);
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#handleMessage(java.lang.Object)
	 */
	@Override
	public Void handleMessage(JmsPayload msg) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();
		
		String brokerURL = getConfiguration().getProviderUrl();
		String userName = getConfiguration().getUsername();
		String password = getConfiguration().getPassword();
		String topicName = getConfiguration().getTopic();
		boolean transacted = false;
		
		try {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(userName, password, brokerURL);
			this.connection = factory.createConnection(userName, password);
			this.connection.start();
			
			this.session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
			this.topic = session.createTopic(topicName);
			
			this.producer = session.createProducer(this.topic);
			this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		} catch (JMSException ex) {
			ex.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#terminate()
	 */
	@Override
	public void terminate() {
		try {
			connection.close();
		} catch (JMSException ex) {
			ex.printStackTrace();
		}
		
		super.terminate();
	}

}
