package fr.heliumteam.flightcontrol;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;

/*import de.hardcode.jxinput.Axis;
import de.hardcode.jxinput.Button;
import de.hardcode.jxinput.JXInputDevice;
import de.hardcode.jxinput.event.JXInputAxisEvent;
import de.hardcode.jxinput.event.JXInputAxisEventListener;
import de.hardcode.jxinput.event.JXInputButtonEvent;
import de.hardcode.jxinput.event.JXInputButtonEventListener;
import de.hardcode.jxinput.event.JXInputEventManager;*/

public class ControlHandler implements /*JXInputAxisEventListener, JXInputButtonEventListener,*/ KeyEventDispatcher {

	private final GroundControl app;
	private final String name;
	private final Map<String, String> controls = new HashMap<String, String>();
	private final Map<String, Boolean> controlsDown = new HashMap<String, Boolean>();

	//private JXInputDevice device = null;

	// For config
	private JButton waitingBtn = null;
	private String waitingKey;

	public ControlHandler(GroundControl app, String name) {
		this.name = name;
		this.app = app;

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
		resetControls();
		//setDevice(null);
	}

	public void resetControls() {
		controls.put("Monter", "Z");
		controls.put("Descendre", "S");
		controls.put("Rotation droite", "D");
		controls.put("Rotation gauche", "Q");
		controls.put("Translation avant", "I");
		controls.put("Translation arrière", "K");
		controls.put("Translation droite", "L");
		controls.put("Translation gauche", "J");

		for (String key : controls.keySet()) {
			controlsDown.put(key, false);
		}
	}

	public boolean isActionDown(String action) {
		if (!controlsDown.containsKey(action)) return false;
		return controlsDown.get(action);
	}

	public Map<String, String> getControls() {
		return controls;
	}

	public String getName() {
		return name;
	}

	/*public void setDevice(JXInputDevice device) {
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
	}*/

	public void resetWaitForKey() {
		this.waitingBtn = null;
		this.waitingKey = null;
	}

	public void buttonWaitForKey(String key, JButton btn) {
		if (waitingBtn != null)
			return;
		btn.setText("<Appuyer sur une touche>");
		this.waitingBtn = btn;
		this.waitingKey = key;
	}

	@Override
    public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_PRESSED) {
			if (waitingBtn != null) {
				String name = KeyEvent.getKeyText(e.getKeyCode());
				waitingBtn.setText("<"+name+">");
				waitingBtn = null;
				controls.put(waitingKey, name);
				return false;
			}
			String keyText = KeyEvent.getKeyText(e.getKeyCode());
			String action = getActionForKey(keyText);
			if (action != null) {
				controlsDown.put(action, true);
			}
		} else if (e.getID() == KeyEvent.KEY_RELEASED) {
			if (waitingBtn != null) {
				return false;
			}
			String keyText = KeyEvent.getKeyText(e.getKeyCode());
			String action = getActionForKey(keyText);
			if (action != null) {
				controlsDown.put(action, false);
			}
		} else {
			return false;
		}
		return true;
	}
	
	public String getActionForKey(String key) {
		for (Entry<String, String> e : controls.entrySet()) {
			if (e.getValue().equalsIgnoreCase(key)) {
				return e.getKey();
			}
		}
		return null;
	}

	/*@Override
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
	}*/

}
