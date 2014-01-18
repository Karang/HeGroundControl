package fr.heliumteam.flightcontrol.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import fr.heliumteam.flightcontrol.ControlHandler;
import fr.heliumteam.flightcontrol.GroundControl;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class XBeeCom extends DroneCom implements SerialPortEventListener {

	public static final int BAUD_RATE = 9600;
	
	private SerialPort serial;
	
	private BufferedReader input;
	private OutputStream output;
	
	public XBeeCom(String com, ControlHandler pilote) {
		super(pilote);
		
		try {
			final CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(com);
			serial = (SerialPort) id.open("DroneGCS", 2000);
			serial.setSerialPortParams(BAUD_RATE,SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			
			input = new BufferedReader(new InputStreamReader(serial.getInputStream()));
			output = serial.getOutputStream();
			
			serial.addEventListener(this);
			serial.notifyOnDataAvailable(true);
			
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

	@Override
	public void serialEvent(SerialPortEvent evt) {
		if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = input.readLine();
				String [] data = inputLine.split(";");
				
				//GroundControl.getGCS().log(inputLine);
				
				float yaw = Float.parseFloat(data[0]);
				float pitch = Float.parseFloat(data[1]);
				float roll = Float.parseFloat(data[2]);
				float batterie = Float.parseFloat(data[3]);
				
				GroundControl.getGCS().getYaw().setYaw(yaw);
				GroundControl.getGCS().getPitchRoll().setPitch(pitch);
				GroundControl.getGCS().getPitchRoll().setRoll(roll);
				GroundControl.getGCS().getBatterie().setValue(batterie);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
