package fr.heliumteam.flightcontrol.com;

import fr.heliumteam.flightcontrol.ControlHandler;
import fr.heliumteam.flightcontrol.GroundControl;
import fr.heliumteam.flightcontrol.comp.Boussole;
import fr.heliumteam.flightcontrol.tools.ByteTool;
import fr.heliumteam.flightcontrol.tools.MathHelper;

public abstract class DroneCom extends Thread {

	private final ControlHandler pilote;
	
	private float lastYaw = 0, lastPitch = 0, lastRoll = 0, lastThrust = 0;
	private float yaw = 0, pitch = 0, roll = 0, thrust = 0;
	
	private long lastTime;
	private long lastPing;
	
	public DroneCom(ControlHandler pilote) {
		this.pilote = pilote;
	}
	
	@Override
	public void run() {
		lastTime = System.currentTimeMillis();
		lastPing = System.currentTimeMillis();
		
		while (!isInterrupted()) {
			
			float dt = ((float)(System.currentTimeMillis() - lastTime)) / 1000f;
			lastTime = System.currentTimeMillis();
			
			thrust = (1-pilote.getActionValue("Puissance"))*100;
			thrust = MathHelper.clamp(thrust, 0, 100);
			yaw += pilote.getActionValue("Lacet")*100*dt;
			yaw = MathHelper.correctAngle(yaw);
			pitch = MathHelper.checkZero(pilote.getActionValue("Tangage")*50f, 6f);
			roll = MathHelper.checkZero(-pilote.getActionValue("Roulis")*50f, 6f);
			
			if (!MathHelper.compareFloat(lastThrust, thrust)) {
				send(ByteTool.encodePayload('T', thrust));
				lastThrust = thrust;
				GroundControl.getGCS().log("Send T "+thrust);
			}
			
			final Boussole b = GroundControl.getGCS().getYaw();
			if (thrust == 0) {
				b.setYawRef(b.getYaw());
			}
			
			float y = MathHelper.correctAngle((float)(yaw+b.getYawRef()));
			if (!MathHelper.compareFloat(lastYaw, y)) {
				send(ByteTool.encodePayload('Y', y));
				lastYaw = y;
				//GroundControl.getGCS().log("Send Y "+y+" "+yaw);
			}
			
			if (!MathHelper.compareFloat(lastPitch, pitch)) {
				send(ByteTool.encodePayload('P', pitch));
				lastPitch = pitch;
				GroundControl.getGCS().log("Send P "+pitch);
			}
			
			if (!MathHelper.compareFloat(lastRoll, roll)) {
				send(ByteTool.encodePayload('R', roll));
				lastRoll = roll;
				GroundControl.getGCS().log("Send R "+roll);
			}
			
			if ((System.currentTimeMillis() - lastPing) >= 1000f) {
				send(ByteTool.encodePayload('Z', 0.f));
				lastPing = System.currentTimeMillis();
				//GroundControl.getGCS().log("Send ping");
			}
			
			try {
				Thread.sleep(100);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public abstract void send(byte[] msg);
}
