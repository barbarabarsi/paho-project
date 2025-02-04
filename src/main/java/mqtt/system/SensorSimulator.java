package mqtt.system;

import java.util.Random;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import mqtt.entities.Client;

public class SensorSimulator {

	public static void main(String[] args) {
		
		String broker = "tcp://localhost:1883"; 
		String prefix = "/home/Lyon/sido/";
		
		String dhtTempTopic = "dht22/value";
		String dhtHumTopic = "dht22/value2";
		
		String shtTempTopic = "sht30/value";
		String shtHumTopic = "sht30/value2";
		
		try {
				
			Client sensorDht = new Client(broker, "dht20");
			Client sensorSht = new Client(broker, "sht30");
			
			sensorDht.connectWithOptions(false);
			sensorSht.connectWithOptions(false);    	
	    	System.out.println("Mqtt Clients sucessfully connected.");
	    	
	    	Thread shtTempThread = SensorSimulator.createSensorThread(sensorSht, prefix + shtTempTopic, 27, 32);
	    	
	    	Thread shtHumThread = SensorSimulator.createSensorThread(sensorSht, prefix + shtHumTopic, 50, 70);
	    	
	    	Thread dhtTempThread = SensorSimulator.createSensorThread(sensorDht, prefix + dhtTempTopic, 27, 32);
	    	
	    	Thread dhtHumThread = SensorSimulator.createSensorThread(sensorDht, prefix + dhtHumTopic, 0, 70);
	    	
	    	shtTempThread.start();
	    	shtHumThread.start();
	    	dhtTempThread.start();
	    	dhtHumThread.start();
			
		} 
		catch(MqttException e) { Client.printError(e);}

	}
	
	public static Thread createSensorThread(Client sensor, String topic, int origin, int bound) {
		return new Thread(() -> {
			while (true) {
				Random rand = new Random();
				try {
					double value = rand.nextDouble(origin, bound);
					sensor.publishMessage(topic, String.valueOf(value), 2, true);
	            	Thread.sleep(1000);
				} catch (InterruptedException e) { e.printStackTrace();	}
    	 }
	 });
	}
}