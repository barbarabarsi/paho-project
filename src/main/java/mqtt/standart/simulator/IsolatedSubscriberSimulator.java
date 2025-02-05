package mqtt.standart.simulator;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import mqtt.standart.entities.Client;

public class IsolatedSubscriberSimulator {

	public static void main(String[] args) {
		
		String broker = "tcp://localhost:1883"; 
		String topic = "labs/new-topic-4.8";
	    int qos = 1;
		
		try {
				
			Client subscriber = new Client(broker, "myClientID_Sub");
			
	    	subscriber.connectWithOptions(false);
	    	System.out.println("Mqtt Subscriber sucessfully connected.");
	    	
	    	 Thread subscribeThread = new Thread(() -> {
					try {
						subscriber.subscribeToTopic(topic, qos);
					} catch (MqttException e) {
						e.printStackTrace();
					}
				});
		        
		    subscribeThread.start();
	        	  
	        
	        try {
	            subscribeThread.join();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			
			
		} 
		catch(MqttException e) { Client.printError(e);}

	}
}