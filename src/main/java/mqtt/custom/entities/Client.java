package mqtt.custom.entities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
	
	private Broker broker;
    private String ID;
    private String protocolName;
    private boolean cleanSession;
    private boolean willFlag;
    private int willQoS;
    private boolean willRetain;
    private boolean hasPassword;
    private boolean hasUsername;
	private String password;
	private String username;
	private int keepAliveTime;
    private static final AtomicInteger packetIdentifierGenerator = new AtomicInteger(1); // Packet Identifier generator

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    
    public Client(Broker broker, String ID, boolean cleanSession, int keepAliveTime, boolean willFlag, int willQoS, boolean willRetain) {
        this(broker, ID, cleanSession, keepAliveTime, willFlag, willQoS, willRetain, null, null);
    }
    
    public Client(Broker broker, String ID, boolean cleanSession, int keepAliveTime, boolean willFlag, int willQoS, boolean willRetain, String username, String password) {
    	 this.broker = broker;
         this.ID = ID;
         this.protocolName = "MQTT";
         this.cleanSession = cleanSession;
         this.keepAliveTime = keepAliveTime;
         
         this.willFlag = willFlag;
         this.willQoS = willQoS;
         this.willRetain = willRetain;
         
         this.username = username;
         this.password = password;
         
         this.hasPassword = (this.password != null) ;
         this.hasUsername = (this.username != null);
    }
    
    
    public void connect() {
    	  
    	try {
               
			   this.socket = new Socket(InetAddress.getByName(broker.getAddress()), broker.getPort());
			   this.outputStream = socket.getOutputStream();
			   this.inputStream = socket.getInputStream();
				
               byte[] connectpacket = createConnectPacket();
               
               System.out.println("Connecting with options:");
               System.out.println("========================");
               System.out.println("Protocol Name: " + protocolName);
               System.out.println("Client ID: " + ID);
               System.out.println("Broker: " + broker);
               System.out.println("Clean Session: " + cleanSession);
               System.out.println("Keep Alive Time: " + keepAliveTime + " seconds");
               System.out.println("Will Flag: " + willFlag);
               System.out.println("Will QoS: " + willQoS);
               System.out.println("Will Retain: " + willRetain);
               System.out.println("Username: " + (hasUsername ? username : "None"));
               System.out.println("Password: " + (hasPassword ? "******" : "None"));
               System.out.println("========================\n");

               outputStream.write(connectpacket); 
               outputStream.flush();
               
               byte[] response = new byte[4]; 
               int bytesRead = inputStream.read(response);
               
               if (bytesRead > 0) {
                   readByteInfo("Received CONNACK:", response);
               }

               
               startKeepAlive(outputStream);
           } catch (Exception e) { printError(e); }
    	
    }
    
    private byte[] createConnectPacket() {
        byte[] clientIdBytes = ID.getBytes();
        byte[] usernameBytes = hasUsername ? username.getBytes() : new byte[0];
        byte[] passwordBytes = hasPassword ? password.getBytes() : new byte[0];

        int length = 14 + clientIdBytes.length; // Fixed header + variable header + client ID length

        if (hasUsername) {
            length += 2 + usernameBytes.length; // Add username length and username bytes
        }
        if (hasPassword) {
            length += 2 + passwordBytes.length; // Add password length and password bytes
        }

        byte[] packet = new byte[length];

        // Fixed header
        packet[0] = 0x10; // CONNECT packet type (0001 0000)
        packet[1] = (byte) (length - 2); // Remaining length

        // Variable header
        packet[2] = 0x00; // Protocol name length MSB
        packet[3] = 0x04; // Protocol name length LSB
        System.arraycopy("MQTT".getBytes(), 0, packet, 4, 4); // Protocol name
        packet[8] = 0x03; // Protocol level (MQTT 3.1)

        // Connect flags
        byte connectFlags = 0;
        if (cleanSession) connectFlags |= 0x02; // Clean Session flag
        if (willFlag) {
        	connectFlags |= 0x04; // Will flag
        	connectFlags |= (willQoS << 3); // Will QoS level
            if (willRetain) connectFlags |= 0x20; // Will Retain flag
        }
        if (hasUsername) connectFlags |= 0x80; // Username flag
        if (hasPassword) connectFlags |= 0x40; // Password flag
        
        packet[9] = connectFlags;

        // Keep Alive (2 bytes)
        packet[10] = (byte) ((keepAliveTime >> 8) & 0xFF); // MSB
        packet[11] = (byte) (keepAliveTime & 0xFF); // LSB

        // Payload
        int index = 12;

        // Client ID
        packet[index++] = (byte) ((clientIdBytes.length >> 8) & 0xFF); // MSB
        packet[index++] = (byte) (clientIdBytes.length & 0xFF); // LSB
        System.arraycopy(clientIdBytes, 0, packet, index, clientIdBytes.length);
        index += clientIdBytes.length;

        // Username
        if (hasUsername) {
            packet[index++] = (byte) ((usernameBytes.length >> 8) & 0xFF); // MSB
            packet[index++] = (byte) (usernameBytes.length & 0xFF); // LSB
            System.arraycopy(usernameBytes, 0, packet, index, usernameBytes.length);
            index += usernameBytes.length;
        }

        // Password
        if (hasPassword) {
            packet[index++] = (byte) ((passwordBytes.length >> 8) & 0xFF); // MSB
            packet[index++] = (byte) (passwordBytes.length & 0xFF); // LSB
            System.arraycopy(passwordBytes, 0, packet, index, passwordBytes.length);
        }

        return packet;
    }
    
    
    public void publish(String topic, String message, int qos) {
        try {
            byte[] publishPacket = createPublishPacket(topic, message, qos);
            outputStream.write(publishPacket);
            outputStream.flush();
            System.out.println("Topic: " + topic +"  | Published message: " + message +"\n");
    
            if (qos > 0) {
            	byte[] response = new byte[4]; 
                int bytesRead = inputStream.read(response);
                
            	if (bytesRead > 0) {
            		readByteInfo("Received PUBBACK:", response);
                }
            	
            }
            
        } catch (Exception e) { printError(e); }
    }
    
    private byte[] createPublishPacket(String topic, String message, int qos) {
    	
    	byte[] topicBytes = topic.getBytes();
        byte[] messageBytes = message.getBytes();
        
        int length = 2 + topicBytes.length + messageBytes.length;
        if (qos > 0) length += 2; 
        
        byte[] packet = new byte[length + 2]; 
        
        packet[0] = (byte) (0x30 | (qos << 1)); // PUBLISH packet type (0011 0000) + QoS
        packet[1] = (byte) length;
        
        // Topic bytes size
        packet[2] = (byte) (topicBytes.length >> 8); // MSB
        packet[3] = (byte) (topicBytes.length & 0xFF); //LSB
        
        // Topic
        System.arraycopy(topicBytes, 0, packet, 4, topicBytes.length);
        
        
        int index = 4 + topicBytes.length;
        
        // Packet Identifier (if QoS == 1 or 2)
        if (qos > 0) {
            int packetIdentifier = packetIdentifierGenerator.getAndIncrement();
            packet[index++] = (byte) (packetIdentifier >> 8);
            packet[index++] = (byte) (packetIdentifier & 0xFF);
        }
        
        // Message
        System.arraycopy(messageBytes, 0, packet, index, messageBytes.length);
       
        
        return packet;
    }
    
    public void subscribe(String topic, int qos) {
        try {
            byte[] subscribePacket = createSubscribePacket(topic, qos);

            outputStream.write(subscribePacket);
            outputStream.flush();
            
            
            if (qos > 0 ) {
            	byte[] response = new byte[5];
                int bytesRead = inputStream.read(response);
                
                if (bytesRead > 0) {
                    readByteInfo("Received SUBACK:", response);
                }
                
            }
            
            listenForMessages(topic, inputStream);
        } catch (Exception e) { printError(e); }
    }
    
	private byte[] createSubscribePacket(String topic, int qos) {
	    byte[] topicBytes = topic.getBytes();
	
	    // Correct length calculation
	    int length = 2 + topicBytes.length + 2 + 1; // Header + Topic Length (2 bytes) + Topic + QoS
	    if (qos > 0) { length += 2; } // Packet Identifier (2 bytes) 
	
	    byte[] packet = new byte[length];
	
	    packet[0] = (byte) 0x82; // SUBSCRIBE packet type (1000 0010)
	    packet[1] = (byte) (length - 2); // Remaining length
	
	    int index = 2;
	
	    // Packet Identifier (if QoS > 0)
	    if (qos > 0) {
	        int packetIdentifier = packetIdentifierGenerator.getAndIncrement();
	        packet[index++] = (byte) (packetIdentifier >> 8);
	        packet[index++] = (byte) (packetIdentifier & 0xFF);
	    }
	
	    // Topic Length
	    packet[index++] = (byte) (topicBytes.length >> 8); // MSB
	    packet[index++] = (byte) (topicBytes.length & 0xFF); // LSB
	
	    // Topic
	    System.arraycopy(topicBytes, 0, packet, index, topicBytes.length);
	    index += topicBytes.length;
	
	    // QoS level
	    packet[index] = (byte) qos; // Ensure the correct QoS level is set
	
	    return packet;
	}

    
    private void listenForMessages(String topic, InputStream inputStream) {
        new Thread(() -> {
            try {
                while (true) {
                    byte[] header = new byte[2];
                    int bytesRead = inputStream.read(header);
                    if (bytesRead < 2) continue;
                    
                    int remainingLength = Integer.valueOf(header[1]);
                    byte[] payload = new byte[remainingLength];
                    inputStream.read(payload);
                    
                    if (payload.length > 0) {
                    						// MSB 						      // LSB
                    	 int topicLength = Integer.valueOf(payload[0] << 8) + Integer.valueOf(payload[1]);
 
                         String receivedTopic = new String(payload, 2, topicLength); // Skip two first bytes for topic size definition

                         String message = new String(payload, 4 + topicLength, payload.length - (4 + topicLength));

                         if (receivedTopic.equals(topic)) {
                             System.out.println("Received message on topic " + topic + ": " + message + "\n");
                         }
                    }
                   
                }
            } catch (Exception e) { printError(e); }
        }).start();
    }
    
    public void startKeepAlive(OutputStream outputStream) {

        Thread keepAliveThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(keepAliveTime * 1000); 
                    sendPingReq(outputStream);
                }
            } catch (InterruptedException e) {
                System.out.println("Keep-alive thread interrupted.");
            } catch (IOException e) {
                printError(e);
            }
        });

        keepAliveThread.start();
    }

    private void sendPingReq(OutputStream outputStream) throws IOException {
    	try {
	        byte[] pingReq = new byte[] { (byte) 0xC0, 0x00 }; // PINGREQ packet
	        outputStream.write(pingReq);
	        outputStream.flush();
	        //System.out.println("Sent PINGREQ");
	    }
		        catch (IOException e) {
		            printError(e);
	        }
    }
    
    
    public static void readByteInfo(String origin, byte[] message) {
    	System.out.println(origin);
    	for (byte b : message) {
            System.out.printf("0x%02X ", b);
        }
        System.out.println("\n");
    }

    
    public static void printError(Exception e) {
    	System.out.println("Error!");
		System.out.println("Packet: " + e.getMessage());
		System.out.println("Location: " + e.getLocalizedMessage());
		System.out.println("Cause: " + e.getCause());
		System.out.println("Reason: " + e);
		e.printStackTrace();
    }
    
    

}
