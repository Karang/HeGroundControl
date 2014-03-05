package fr.heliumteam.flightcontrol.tools;

public class BatteryTool {

	public static final float[] voltages = new float[] {0, 9, 9.9f, 10.8f, 11.1f, 11.25f, 11.37f, 11.49f, 11.61f, 11.76f, 11.91f, 12.3f, 12.6f, 12.6f};
	public static final float[] pcent = new float[] {0, 0, 5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 100};
	
	
	private static int getIndex(float voltage) {
		for (int i=1 ; i<voltages.length-1 ; i++) {
			if (voltage<=voltages[i]) {
				return i;
			}
		}
		return voltages.length-2;
	}
	
	public static float getPercent(float voltage) {
		int i = getIndex(voltage);
		float slope = MathHelper.slope(voltage, voltages[i-1], voltages[i]);
		return MathHelper.clamp(MathHelper.lerp(pcent[i-1], pcent[i], slope), 0, 100);
	}
	
	public static void main(String[] args) {
		System.out.println(getPercent(11.2f));
	}
}
