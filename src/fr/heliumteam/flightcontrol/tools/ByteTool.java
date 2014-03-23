package fr.heliumteam.flightcontrol.tools;

import java.nio.ByteBuffer;

import fr.heliumteam.flightcontrol.GroundControl;

public class ByteTool {

	public static float byteToFloat(byte b3, byte b2, byte b1, byte b0) {
		int intbit = 0;
		intbit = (b3 << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | (b0 & 0xff);

		return Float.intBitsToFloat(intbit);
	}
	
	public static byte[] encodePIDPayload(char type, float kP, float kI, float kD) {
		ByteBuffer bb = ByteBuffer.allocate(13);
		bb.put((byte)type);
		bb.putFloat(kP);
		bb.putFloat(kI);
		bb.putFloat(kD);
		return bb.array();
	}
	
	public static byte[] encodePayload(char t, float a) {
		ByteBuffer bb = ByteBuffer.allocate(5);
		bb.put((byte)t);
		bb.putInt(Float.floatToIntBits(a));
		return bb.array();
	}
	
	public static void printBytes(byte[] bytes) {
		GroundControl.getGCS().log(bytArrayToHex(bytes));
	}
	
	public static String bytArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		int i=0;
		for (byte b : a) {
			if ((i%4)==0)
				sb.append(" | ");
			sb.append(String.format("%02x", b&0xff)).append(" ");
			i++;
		}
		return sb.toString();
	}
	
}
