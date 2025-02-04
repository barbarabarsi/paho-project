package mqtt.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Callback implements MqttCallback {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Callback.class);
    @Override public void connectionLost(Throwable throwable) {
        LOGGER.error("Connection lost. " + throwable);
    }

    @Override 
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        LOGGER.debug("Topic: {}, message: {}", topic, mqttMessage);
        // **Do something with the message**
    }

    @Override 
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOGGER.debug("Delivery complete: {}",  iMqttDeliveryToken);
    }

}
