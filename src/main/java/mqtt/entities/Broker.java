package mqtt.entities;

public class Broker {
	
	private String address;
	
	public Broker(String address) {
		this.address = address;
	}
	
	public String getAddress() { return address; }
 
}
