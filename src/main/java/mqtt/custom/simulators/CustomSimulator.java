package mqtt.custom.simulators;

import mqtt.custom.entities.Broker;
import mqtt.custom.entities.Client;

public class CustomSimulator {

    public static void main(String[] args) {
        Broker broker = new Broker("localhost", 1883);
        String topic = "labs/custom-topic";
        String messageContent = "Message from my Custom MQTT Client";
        int qos = 2;

        Client subscriber = new Client(broker, "SubscriberClient", true, 60, false, 0, false);
        subscriber.connect();
        subscriber.subscribe(topic, qos); 
        
        Client publisher = new Client(broker, "PublisherClient", true, 60, false, 0, false);
        publisher.connect();
        
        for (int i = 0; i <5; i++) {
        	String currentMessage = messageContent + "| Message " + i;
        	publisher.publish(topic, currentMessage , qos);
        }
        
    }
}
