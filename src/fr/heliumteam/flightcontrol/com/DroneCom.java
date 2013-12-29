package fr.heliumteam.flightcontrol.com;

import fr.heliumteam.flightcontrol.ControlHandler;

public abstract class DroneCom extends Thread {

	private final ControlHandler pilote;
	
	public DroneCom(ControlHandler pilote) {
		this.pilote = pilote;
	}
	
	@Override
	public void run() {
		while (!isInterrupted()) {
			if (pilote.isActionDown("Monter")) {
				send("M");
			}
			
			if (pilote.isActionDown("Descendre")) {
				send("D");
			}
			
			if (pilote.isActionDown("Rotation gauche")) {
				send("Rg");
			}
			
			if (pilote.isActionDown("Rotation droite")) {
				send("Rd");
			}
			
			if (pilote.isActionDown("Translation gauche")) {
				send("Tg");
			}
			
			if (pilote.isActionDown("Translation droite")) {
				send("Td");
			}
			
			if (pilote.isActionDown("Avancer")) {
				send("A");
			}
			
			if (pilote.isActionDown("Reculer")) {
				send("R");
			}
			
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public abstract void send(String msg);
}
