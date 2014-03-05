package fr.heliumteam.flightcontrol.com;

import java.io.IOException;
import java.io.InputStream;

import fr.heliumteam.flightcontrol.GroundControl;
import fr.heliumteam.flightcontrol.tools.BatteryTool;
import fr.heliumteam.flightcontrol.tools.ByteTool;
import fr.heliumteam.flightcontrol.tools.MathHelper;

public class ReceiverThread extends Thread {
	
	private InputStream input;
	private float previousVCC = 0;
	
	public ReceiverThread(InputStream input) {
		this.input = input;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				while (input.read() != 0xBE);
				while (input.available() < 16);
				
				byte[] data = new byte[16];
				input.read(data);

				float yaw, pitch, roll, batterie;

				yaw = ByteTool.byteToFloat(data[0], data[1], data[2], data[3]);
				pitch = ByteTool.byteToFloat(data[4], data[5], data[6], data[7]);
				roll = ByteTool.byteToFloat(data[8], data[9], data[10], data[11]);
				batterie = ByteTool.byteToFloat(data[12], data[13], data[14], data[15]);

				batterie *= 3.2f; // Diviseur de tension
				batterie = MathHelper.lerp(batterie, previousVCC, 0.5f); // Low pass filter
				previousVCC = batterie;
				
				GroundControl.getGCS().getYaw().setYaw(yaw);
				GroundControl.getGCS().getPitchRoll().setPitch(pitch);
				GroundControl.getGCS().getPitchRoll().setRoll(roll);
				GroundControl.getGCS().getBatterieVolt().setValue(batterie);
				GroundControl.getGCS().getBatterie().setValue(BatteryTool.getPercent(batterie));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
