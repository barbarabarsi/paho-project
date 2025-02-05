package mqtt.custom.entities;

public class Broker {
	
	private String address;
	private int port;
	
	public Broker(String address, int port) {
		this.address = address;
		this.port = port;
	}
	
	public String getAddress() { return this.address; }
	
	public int getPort() { return this.port; }

}
