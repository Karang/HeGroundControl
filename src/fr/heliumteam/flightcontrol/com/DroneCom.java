package fr.heliumteam.flightcontrol.com;

import fr.heliumteam.flightcontrol.ControlHandler;

public abstract class DroneCom extends Thread {

	private final ControlHandler pilote;
	
	private float lastYaw = 0, lastPitch = 0, lastRoll = 0, lastThrust = 0;
	private float yaw = 0, pitch = 0, roll = 0, thrust = 0;
	
	public DroneCom(ControlHandler pilote) {
		this.pilote = pilote;
	}
	
	public char encodeValue(float v) {
		int i = (int)((v/100f)*127f);
		return (char)i;
	}
	
	public char encodeAngle(float a) {
		int i = (int)((a/360f)*127f);
		System.out.println(i);
		return (char)i;
	}
	
	@Override
	public void run() {
		while (!isInterrupted()) {
			if (pilote.isActionDown("Monter")) {
				thrust += 1;
			} else if (pilote.isActionDown("Descendre")) {
				thrust -= 1;
				if (thrust<0) thrust = 0;
			}
			
			if (pilote.isActionDown("Rotation gauche")) {
				yaw -= 1;
			} else if (pilote.isActionDown("Rotation droite")) {
				yaw += 1;
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
				send("T"+encodeValue(thrust));
				lastThrust = thrust;
			}
			
			if (lastYaw != yaw) {
				send("Y"+encodeAngle(yaw));
				lastYaw = yaw;
			}
			
			if (lastPitch != pitch) {
				send("P"+encodeAngle(pitch));
				lastPitch = pitch;
			}
			
			if (lastRoll != roll) {
				send("R"+encodeAngle(roll));
				lastRoll = roll;
			}
		}
	}
	
	public abstract void send(String msg);
}
