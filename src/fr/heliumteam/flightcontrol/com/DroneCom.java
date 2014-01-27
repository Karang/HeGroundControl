package fr.heliumteam.flightcontrol.com;

import fr.heliumteam.flightcontrol.ControlHandler;
import fr.heliumteam.flightcontrol.GroundControl;
import fr.heliumteam.flightcontrol.tools.ByteTool;
import fr.heliumteam.flightcontrol.tools.MathHelper;

public abstract class DroneCom extends Thread {

	private final ControlHandler pilote;
	
	private float lastYaw = 0, lastPitch = 0, lastRoll = 0, lastThrust = 0;
	private float yaw = 0, pitch = 0, roll = 0, thrust = 0;
	
	//private long lastTime;
	
	public DroneCom(ControlHandler pilote) {
		this.pilote = pilote;
	}
	
	@Override
	public void run() {
		//lastTime = System.currentTimeMillis();
		
		while (!isInterrupted()) {
			
			//float dt = ((float)(System.currentTimeMillis() - lastTime)) / 1000f;
			//lastTime = System.currentTimeMillis();
			
			thrust += pilote.getActionValue("Monter");
			yaw += pilote.getActionValue("Rotation droite");
			yaw = MathHelper.correctAngle(yaw);
			pitch = MathHelper.correctAngle(pilote.getActionValue("Translation avant")*30f);
			roll = MathHelper.correctAngle(-pilote.getActionValue("Translation droite")*30f);
			
			if (!MathHelper.compareFloat(lastThrust, thrust)) {
				send(ByteTool.encodePayload('T', thrust));
				lastThrust = thrust;
				//GroundControl.getGCS().log("Send T "+thrust);
			}
			
			if (!MathHelper.compareFloat(lastYaw, yaw)) {
				send(ByteTool.encodePayload('Y', yaw));
				lastYaw = yaw;
				GroundControl.getGCS().log("Send Y "+yaw);
			}
			
			if (!MathHelper.compareFloat(lastPitch, pitch)) {
				send(ByteTool.encodePayload('P', pitch));
				lastPitch = pitch;
				//GroundControl.getGCS().log("Send P "+pitch);
			}
			
			if (!MathHelper.compareFloat(lastRoll, roll)) {
				send(ByteTool.encodePayload('R', roll));
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
