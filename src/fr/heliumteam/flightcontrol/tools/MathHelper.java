package fr.heliumteam.flightcontrol.tools;

public class MathHelper {

	public static final float EPS = 0.0001f;
	
	public static float correctAngle(float a) {
		if (a>360f) {
			return a-360f;
		}
		if (a<0) {
			return a+360f;
		}
		return a;
	}
	
	public static boolean compareFloat(float a, float b) {
		return Math.abs(a-b)<EPS;
	}
	
	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(value, max));
	}
	
	public static float slope(float value, float min, float max) {
		return (value-min)/(max-min);
	}
	
	public static float lerp(float a, float b, float r) {
		return a*r + b*(1-r);
	}

	public static float checkZero(float value) {
		if (value<EPS && value>-EPS)
			return 0;
		return value;
	}
	
}
