package fr.heliumteam.flightcontrol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.logging.Logger;

public class UDPServer {

	private final static int XBEE_PORT = 0xBEE;
	private final Logger logger = Logger.getLogger("HeFlightController");
	
	private DatagramSocket socket;
	
	public UDPServer() {
		logger.info("Connecting on "+XBEE_PORT);
		try {
			socket = new DatagramSocket(null);
			socket.setReuseAddress(true);
			socket.setBroadcast(true);
			socket.bind(new InetSocketAddress(XBEE_PORT));
			logger.info("Connected");
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			try {
				byte[] buff = new byte[1024];
				final DatagramPacket packet = new DatagramPacket(buff, buff.length);
				logger.info("Listening...");
				socket.receive(packet);
				logger.info(packet.toString());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		UDPServer test = new UDPServer();
		test.run();
	}
	
}
