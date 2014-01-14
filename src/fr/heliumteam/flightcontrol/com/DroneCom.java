package fr.heliumteam.flightcontrol.com;

import java.nio.ByteBuffer;

import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.XBeeResponse;

import fr.heliumteam.flightcontrol.ControlHandler;
import fr.heliumteam.flightcontrol.GroundControl;

public abstract class DroneCom extends Thread implements PacketListener {

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
			if (pilote.isActionDown("Monter")) {
				thrust += 0.5f;
				if (thrust>100) thrust = 100;
			} else if (pilote.isActionDown("Descendre")) {
				thrust -= 0.5f;
				if (thrust<0) thrust = 0;
			}
			
			if (pilote.isActionDown("Rotation gauche")) {
				yaw -= 0.5f;
				if (yaw<0) yaw += 360;
			} else if (pilote.isActionDown("Rotation droite")) {
				yaw += 0.5f;
				if (yaw>=360) yaw -= 360;
			}
			
			if (pilote.isActionDown("Translation droite")) {
				roll = -30;
			} else if (pilote.isActionDown("Translation gauche")) {
				roll = 30;
			} else {
				roll = 0;
			}
			
			if (pilote.isActionDown("Translation avant")) {
				pitch = -30;
			} else if (pilote.isActionDown("Translation arrière")) {
				pitch = 30;
			} else {
				pitch = 0;
			}
			
			//System.out.println("Thrust: "+thrust+" Yaw: "+yaw+" Pitch: "+pitch+" Roll:"+roll);
			
			if (lastThrust != thrust) {
				send(encodePayload('T', thrust));
				lastThrust = thrust;
			}
			
			if (lastYaw != yaw) {
				send(encodePayload('Y', yaw));
				lastYaw = yaw;
			}
			
			if (lastPitch != pitch) {
				send(encodePayload('P', pitch));
				lastPitch = pitch;
			}
			
			if (lastRoll != roll) {
				send(encodePayload('R', roll));
				lastRoll = roll;
			}
			
			try {
				Thread.sleep(10);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private byte[] convert(int[] i) {
		byte [] b = new byte[i.length];
		for (int j=0 ; j< i.length ; j++) {
			b[j] = (byte)i[j];
		}
		return b;
	}
	
	@Override
	public void processResponse(XBeeResponse res) {
		byte[] b = convert(res.getRawPacketBytes());
		GroundControl.getGCS().getConsole().append(new String(b)+"\n");
	}
	
	public abstract void send(byte[] msg);
}
