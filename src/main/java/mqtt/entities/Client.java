package mqtt.entities;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import mqtt.messages.Callback;

public class Client extends MqttClient {
	
	//private MemoryPersistence persistence;
    
    public Client(String brokerURI, String clientId) throws MqttException {
    	super(brokerURI, clientId, new MemoryPersistence());
    	setCallback(new Callback());
    }
    
    public void publishMessage(String topic, String messageContent, int qos) {
    	  
    	// publish a message
        System.out.println("Mqtt Client: Publishing message: " + messageContent);
        MqttMessage message = new MqttMessage(messageContent.getBytes());
        //message.setRetained(true);
        
        message.setQos(qos);
        
        try {
			publish(topic, message);
		} catch (MqttException e) {
			printError(e);
		}
        
        System.out.println("Mqtt Client: successfully published the message.");
    }
    
    public void subscribeToTopic(String topic, int qos) throws MqttException {
        
    	try {
			subscribe(topic, qos);
			System.out.println("Mqtt Client Subscriber: successfully subscribed to topic " + topic);
		} catch (MqttException e) {
			e.printStackTrace();
		}
 
    }
    
    public void subscribeToTopic(String topic) throws MqttException {
      
    	try {
			subscribe(topic);
			System.out.println("Mqtt Client Subscriber: successfully subscribed to topic " + topic);
		} catch (MqttException e) {
			e.printStackTrace();
		}
 
    }
    
    public static void printError(MqttException e) {
    	System.out.println("Mqtt Exception reason: " + e.getReasonCode());
		System.out.println("Mqtt Exception message: " + e.getMessage());
		System.out.println("Mqtt Exception location: " + e.getLocalizedMessage());
		System.out.println("Mqtt Exception cause: " + e.getCause());
		System.out.println("Mqtt Exception reason: " + e);
		e.printStackTrace();
    }
    
    

}
