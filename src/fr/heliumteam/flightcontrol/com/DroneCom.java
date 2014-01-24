package fr.heliumteam.flightcontrol.com;

import java.nio.ByteBuffer;

import fr.heliumteam.flightcontrol.ControlHandler;
import fr.heliumteam.flightcontrol.GroundControl;
import fr.heliumteam.flightcontrol.tools.MathHelper;

public abstract class DroneCom extends Thread {

	private final ControlHandler pilote;
	
	private float lastYaw = 0, lastPitch = 0, lastRoll = 0, lastThrust = 0;
	private float yaw = 0, pitch = 0, roll = 0, thrust = 0;
	
	public DroneCom(ControlHandler pilote) {
		this.pilote = pilote;
	}
	
	public byte[] encodePayload(char t, float a) {
		ByteBuffer bb = ByteBuffer.allocate(5);
		bb.put((byte)t);
		bb.putInt(Float.floatToIntBits(a));
		return bb.array();
	}
	
	public void printBytes(byte[] bytes) {
		GroundControl.getGCS().log(bytArrayToHex(bytes));
	}
	
	public String bytArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		for (byte b : a)
			sb.append(String.format("%02x", b&0xff)).append(" ");
		return sb.toString();
	}
	
	@Override
	public void run() {
		while (!isInterrupted()) {
			
			thrust += pilote.getActionValue("Monter");
			yaw += pilote.getActionValue("Rotation droite");
			yaw = MathHelper.correctAngle(yaw);
			pitch = MathHelper.correctAngle(pilote.getActionValue("Translation avant")*30f);
			roll = MathHelper.correctAngle(-pilote.getActionValue("Translation droite")*30f);
			
			if (lastThrust != thrust) {
				send(encodePayload('T', thrust));
				lastThrust = thrust;
				//GroundControl.getGCS().log("Send T "+thrust);
			}
			
			if (lastYaw != yaw) {
				send(encodePayload('Y', yaw));
				lastYaw = yaw;
				//GroundControl.getGCS().log("Send Y "+yaw);
			}
			
			if (lastPitch != pitch) {
				send(encodePayload('P', pitch));
				lastPitch = pitch;
				//GroundControl.getGCS().log("Send P "+pitch);
			}
			
			if (lastRoll != roll) {
				send(encodePayload('R', roll));
				lastRoll = roll;
				//GroundControl.getGCS().log("Send R "+roll);
			}
			
			try {
				Thread.sleep(10);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public abstract void send(byte[] msg);
}
