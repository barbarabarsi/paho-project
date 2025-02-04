package mqtt.system;

import org.eclipse.paho.client.mqttv3.MqttException;

import mqtt.entities.Client;

public class Simulator {

	public static void main(String[] args) {
		
		String broker = "tcp://localhost:1883"; 
		String topic = "labs/paho-example-topic";
	    String messageContent = "Message from my Lab's Paho Mqtt Client";
	    int qos = 0;     
	    String clientId  = "myClientID_Pub";
	    //MemoryPersistence persistence = new MemoryPersistence
		
		try (
				Client publisher = new Client(broker, "myClientID_Pub");
				Client subscriber = new Client(broker, "myClientID_Sub")
						) {
			
			Thread subscribeThread = new Thread(() -> {
				try {
					subscriber.subscribeToTopic(qos, topic);
				} catch (MqttException e) {
					e.printStackTrace();
				}
			});
			
			subscribeThread.start();
			
			Thread publishThread = new Thread(() -> {
	            try {
	                for (int i = 1; i <= 5; i++) {
	                	publisher.publishMessage(topic, messageContent + " Message #" + i, qos);
	                    Thread.sleep(5000); // Wait 5 seconds between messages
	                }
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        });
	        publishThread.start();

	        try {
	            subscribeThread.join();
	            publishThread.join();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        
			
		} catch (MqttException e) {
			e.printStackTrace();
		}
		
		
		
		
		
	
	}

}
