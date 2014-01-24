package fr.heliumteam.flightcontrol.tools;

import java.nio.ByteBuffer;

import fr.heliumteam.flightcontrol.GroundControl;

public class ByteTool {

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
		for (byte b : a)
			sb.append(String.format("%02x", b&0xff)).append(" ");
		return sb.toString();
	}
	
}
