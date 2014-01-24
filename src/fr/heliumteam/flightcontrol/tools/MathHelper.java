package fr.heliumteam.flightcontrol.tools;

public class MathHelper {

	public static float correctAngle(float a) {
		if (a>360f) {
			return a-360f;
		}
		if (a<0) {
			return a+360f;
		}
		return a;
	}
	
	public float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(value, max));
	}
	
}
