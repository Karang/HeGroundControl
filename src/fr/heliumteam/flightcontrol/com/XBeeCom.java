package fr.heliumteam.flightcontrol.com;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.heliumteam.flightcontrol.ControlHandler;
import fr.heliumteam.flightcontrol.GroundControl;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class XBeeCom extends DroneCom {

	public static final int BAUD_RATE = 9600;
	
	private SerialPort serial;
	
	private InputStream input;
	private OutputStream output;
	
	public XBeeCom(String com, ControlHandler pilote) {
		super(pilote);
		
		try {
			final CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(com);
			serial = (SerialPort) id.open("DroneGCS", 2000);
			serial.setSerialPortParams(BAUD_RATE,SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			
			input = serial.getInputStream();
			output = serial.getOutputStream();
			
			final ReceiverThread th = new ReceiverThread(input);
			th.start();
			
			GroundControl.getGCS().log("GCS connectée.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void send(byte[] msg) {
		try {
			output.write(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
