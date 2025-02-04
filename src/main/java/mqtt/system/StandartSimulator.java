package mqtt.system;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import mqtt.entities.Client;

public class StandartSimulator {

	public static void main(String[] args) {
		
		String broker = "tcp://localhost:1883"; 
		String topic = "labs/new-topic-4.8";
	    String messageContent = "Message from my Lab's Paho Mqtt Client - retained flag";
	    int qos = 1;
		
		try {
				
			Client publisher = new Client(broker, "myClientID_Pub");
			Client subscriber = new Client(broker, "myClientID_Sub");
			
	    	subscriber.connectWithOptions(false);
	    	publisher.connectWithOptions(false, topic, "Saio da vida pra entrar na história.", qos);    	
	    	System.out.println("Mqtt Clients sucessfully connected.");
	    	
	    	 Thread subscribeThread = new Thread(() -> {
					try {
						subscriber.subscribeToTopic(topic, qos);
					} catch (MqttException e) {
						e.printStackTrace();
					}
				});
		        
		    subscribeThread.start();
	        	 
			for (int i = 1; i <= 5; i++) {
            	publisher.publishMessage(topic, messageContent + " -  Message #" + i, qos, true);
            	Thread.sleep(1000);
			}
			
			System.out.println("All messages were published.");
	        
	       
	        
	        try {
	            subscribeThread.join();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			
			
		} 
		catch(MqttException e) { Client.printError(e);}
		catch (InterruptedException e) { e.printStackTrace(); }

	}
}