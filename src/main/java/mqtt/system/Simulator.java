package mqtt.system;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import mqtt.entities.Client;

public class Simulator {

	public static void main(String[] args) {
		
		String broker = "tcp://localhost:1883"; 
		String topic = "labs/paho-example-topic";
	    String messageContent = "Message from my Lab's Paho Mqtt Client";
	    int qos = 1;
		
		try {
				
			Client publisher = new Client(broker, "myClientID_Pub");
			Client subscriber = new Client(broker, "myClientID_Sub");
			
			try{
		    	MqttConnectOptions subConnectOptions = new MqttConnectOptions();
		    	subConnectOptions.setCleanSession(false);
		    	
		    	MqttConnectOptions pubConnectOptions = new MqttConnectOptions();
		    	pubConnectOptions.setCleanSession(true);
		    	
		    	System.out.println("Connecting " + publisher.getClientId() + " to Mqtt Broker running at: " + broker);
		    	publisher.connect(pubConnectOptions);
		    	
		    	
		    	System.out.println("Connecting " + subscriber.getClientId()  + " to Mqtt Broker running at: " + broker);
		    	subscriber.connect(subConnectOptions);
		    	
		    	System.out.println("Mqtt Clients sucessfully connected.");
	        	 
	         } catch(MqttException e) { Client.printError(e); }	
		
			subscriber.subscribeToTopic(topic, qos);
			Thread.sleep(1000);
			subscriber.disconnect();
			System.out.println("Subscriber disconnected.");
			
	        publisher.publishMessage(topic, messageContent + " - q.4.3", qos);
	        
	        subscriber.connect();
	        subscriber.subscribeToTopic(topic, qos);
			
		} catch(MqttException e) { Client.printError(e); 
		} catch (InterruptedException e) { e.printStackTrace(); }	
		
		
		
		
		
	
	}

}
