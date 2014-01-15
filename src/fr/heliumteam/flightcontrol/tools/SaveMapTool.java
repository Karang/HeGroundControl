package fr.heliumteam.flightcontrol.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SaveMapTool {

	public static void saveMap(Map<String, String> map, String filename) {
		try {
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
		
			for (Entry<String, String> e : map.entrySet()) {
				writer.println(e.getKey()+"="+e.getValue());
			}
			
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, String> loadMap(String filename) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			InputStream f = new FileInputStream(filename);
			InputStreamReader fr = new InputStreamReader(f);
			
			BufferedReader in = new BufferedReader(fr);
			
			String line = in.readLine();
			while(line!=null) {
				String[] entry = line.split("=");
				map.put(entry[0], entry[1]);
				line = in.readLine();
			}
			
			fr.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
