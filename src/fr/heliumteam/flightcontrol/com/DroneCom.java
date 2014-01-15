package fr.heliumteam.flightcontrol.com;

import java.nio.ByteBuffer;

import fr.heliumteam.flightcontrol.ControlHandler;
import fr.heliumteam.flightcontrol.GroundControl;

public abstract class DroneCom extends Thread {

	private final ControlHandler pilote;
	
	private float lastYaw = 0, lastPitch = 0, lastRoll = 0, lastThrust = 0;
	private float yaw = 0, pitch = 0, roll = 0, thrust = 0;
	
	public DroneCom(ControlHandler pilote) {
		this.pilote = pilote;
	}
	
	public byte[] encodePayload(char t, float a) {
		ByteBuffer bb = ByteBuffer.allocate(6);
		bb.putChar(t);
		bb.putInt(Float.floatToIntBits(a));
		return bb.array();
	}
	
	@Override
	public void run() {
		while (!isInterrupted()) {
			
			thrust = pilote.getActionValue("Monter") - pilote.getActionValue("Descendre");
			yaw = (pilote.getActionValue("Rotation droite") - pilote.getActionValue("Rotation gauche"))*10f;
			pitch = pilote.getActionValue("Translation droite") - pilote.getActionValue("Translation gauche");
			roll = pilote.getActionValue("Translation avant") - pilote.getActionValue("Translation arrière");
			
			if (lastThrust != thrust) {
				send(encodePayload('T', thrust));
				lastThrust = thrust;
				GroundControl.getGCS().log("Send T "+thrust);
			}
			
			if (lastYaw != yaw) {
				send(encodePayload('Y', yaw));
				lastYaw = yaw;
				GroundControl.getGCS().log("Send Y "+yaw);
			}
			
			if (lastPitch != pitch) {
				send(encodePayload('P', pitch));
				lastPitch = pitch;
				GroundControl.getGCS().log("Send P "+pitch);
			}
			
			if (lastRoll != roll) {
				send(encodePayload('R', roll));
				lastRoll = roll;
				GroundControl.getGCS().log("Send R "+roll);
			}
			
			/*try {
				Thread.sleep(10);
			} catch(Exception e) {
				e.printStackTrace();
			}*/
		}
	}
	
	public abstract void send(byte[] msg);
}
