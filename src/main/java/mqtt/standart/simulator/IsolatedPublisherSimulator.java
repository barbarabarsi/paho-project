package mqtt.standart.simulator;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import mqtt.standart.entities.Client;

public class IsolatedPublisherSimulator {

	public static void main(String[] args) {
		
		String broker = "tcp://localhost:1883"; 
		String topic = "labs/new-topic-4.8";
	    String messageContent = "Message from my Lab's Paho Mqtt Client - retained flag";
	    int qos = 1;
		
		try {
				
			Client publisher = new Client(broker, "myClientID_Pub");
			
	    	publisher.connectWithOptions(false, topic, "Saio da vida pra entrar na história.", qos);    	
	    	System.out.println("Mqtt Clients sucessfully connected.");
	    	
	        	 
			for (int i = 1; i <= 6; i++) {
            	publisher.publishMessage(topic, messageContent + " -  Message #" + i, qos, true);
            	Thread.sleep(100000);
			}
			
			System.out.println("All messages were published.");
	        
			
		} 
		catch(MqttException e) { Client.printError(e);}
		catch (InterruptedException e) { e.printStackTrace(); }

	}
}