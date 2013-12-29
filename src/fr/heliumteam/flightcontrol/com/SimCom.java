package fr.heliumteam.flightcontrol.com;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import fr.heliumteam.flightcontrol.ControlHandler;

public class SimCom extends DroneCom {

	private Socket socket;
	private PrintWriter out;
	
	public SimCom(String host, int port, ControlHandler pilote) {
		super(pilote);
		try {
			socket = new Socket(host, port);
			socket.setSoLinger(true, 10);
			
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void send(String msg) {
		out.print(msg);
		out.flush();
		System.out.println("Sending :"+msg);
	}
	
}
