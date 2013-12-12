package fr.heliumteam.flightcontrol;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;

import de.hardcode.jxinput.Axis;
import de.hardcode.jxinput.Button;
import de.hardcode.jxinput.JXInputDevice;
import de.hardcode.jxinput.event.JXInputAxisEvent;
import de.hardcode.jxinput.event.JXInputAxisEventListener;
import de.hardcode.jxinput.event.JXInputButtonEvent;
import de.hardcode.jxinput.event.JXInputButtonEventListener;
import de.hardcode.jxinput.event.JXInputEventManager;

public class ControlHandler implements JXInputAxisEventListener, JXInputButtonEventListener, KeyListener {

	private final GroundControl app;
	private final String name;
	private final Map<String, String> controls = new HashMap<String, String>();
	
	private JXInputDevice device = null;
	
	// For config
	private JButton waitingBtn = null;
	private String waitingKey;
	
	public ControlHandler(GroundControl app, String name) {
		this.name = name;
		this.app = app;
		
		setDevice(null);
	}
	
	public void resetControls() {
		controls.put("Monter", "");
		controls.put("Descendre", "");
		controls.put("Rotation droite", "");
		controls.put("Rotation gauche", "");
		controls.put("Translation avant", "");
		controls.put("Translation arrière", "");
		controls.put("Translation droite", "");
		controls.put("Translation gauche", "");
	}
	
	public Map<String, String> getControls() {
		return controls;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDevice(JXInputDevice device) {
		if (this.device == device)
			return;
		this.device = device;
		
		JXInputEventManager.reset();
		app.removeKeyListener(this);
		
		if (device == null) {
			app.addKeyListener(this);
		} else {
			for (int i=0 ; i<device.getMaxNumberOfAxes() ; i++) {
				final Axis ax = device.getAxis(i);
				if (ax != null) {
					JXInputEventManager.addListener(this, ax, 0.1);
				}
			}
			for (int i=0 ; i<device.getMaxNumberOfButtons() ; i++) {
				final Button btn = device.getButton(i);
				if (btn != null) {
					JXInputEventManager.addListener(this, btn);
				}
			}
			JXInputEventManager.setTriggerIntervall(20);
		}
		resetControls();
	}
	
	public JXInputDevice getDevice() {
		return device;
	}
	
	public void buttonWaitForKey(String key, JButton btn) {
		if (waitingBtn != null)
			return;
		btn.setText("<Appuyer sur une touche>");
		this.waitingBtn = btn;
		this.waitingKey = key;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (waitingBtn != null) {
			String name = KeyEvent.getKeyText(e.getKeyCode());
			waitingBtn.setText("<"+name+">");
			waitingBtn = null;
			controls.put(waitingKey, name);
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void changed(JXInputButtonEvent e) {
		if (waitingBtn != null) {
			String name = e.getButton().getName();
			waitingBtn.setText("<"+name+">");
			waitingBtn = null;
			controls.put(waitingKey, name);
			return;
		}
	}

	@Override
	public void changed(JXInputAxisEvent e) {
		if (waitingBtn != null) {
			String name = ((e.getDelta()>0)?"-":"+")+e.getAxis().getName();
			waitingBtn.setText("<"+name+">");
			waitingBtn = null;
			controls.put(waitingKey, name);
			return;
		}
	}
	
}
