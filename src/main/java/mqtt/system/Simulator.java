package mqtt.system;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import mqtt.entities.Client;

public class Simulator {

	public static void main(String[] args) {
		
		String broker = "tcp://localhost:1883"; 
		String topic = "labs/test-topic-4";
	    String messageContent = "Message from my Lab's Paho Mqtt Client";
	    int qos = 0;
		
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
			
			Thread subscribeThread = new Thread(() -> {
				try {
					subscriber.subscribeToTopic(topic, qos);
				} catch (MqttException e) {
					e.printStackTrace();
				}
			});
			
			subscriber.subscribeToTopic(topic, qos);
			subscriber.disconnect();
			System.out.println("Subscriber disconnected.");
			
			for (int i = 1; i <= 5; i++) {
            	publisher.publishMessage(topic, messageContent + " - HELLO TEST MESSAGE -  Message #" + i, qos, true);
            }
			
			System.out.println("All messages were published.");
	        
	        MqttConnectOptions subReconnectOptions = new MqttConnectOptions();
	        subReconnectOptions.setCleanSession(true);
	    	
	        subscriber.connect(subReconnectOptions);
	        System.out.println("Subscriber reconnected.");
	        subscribeThread.start();
	        
	        try {
	            subscribeThread.join();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			
			
		} catch(MqttException e) { Client.printError(e);}

	}
}