package fr.heliumteam.flightcontrol;

import fr.heliumteam.flightcontrol.com.DroneCom;
import fr.heliumteam.flightcontrol.com.SimCom;
import fr.heliumteam.flightcontrol.com.XBeeCom;
import fr.heliumteam.flightcontrol.comp.Boussole;
import fr.heliumteam.flightcontrol.comp.Gauge;
import fr.heliumteam.flightcontrol.comp.Horizon;
import fr.heliumteam.flightcontrol.comp.ValueDisplay;
import gnu.io.CommPortIdentifier;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.hardcode.jxinput.JXInputDevice;
import de.hardcode.jxinput.JXInputManager;

public class GroundControl extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private static GroundControl gcs;
	
	// Pilotes
	
	private final ControlHandler pilote;
	
	// Instruments
	
	private Gauge altimetre;
	private Boussole yaw;
	private Horizon pitch_roll;
	
	private Gauge up_speed;
	private Gauge forward_speed;
	
	private Gauge batterie;
	private ValueDisplay batterieTime;
	
	private JTextArea console;
	
	// Drone
	
	public DroneCom droneCom;
	
	public GroundControl() {
		gcs = this;
		this.setFocusable(true);
		
		this.pilote = new ControlHandler("Pilote");
		
		this.setTitle("HeliumTeam : Ground control station");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		
		this.add(buildTopPanel(), BorderLayout.NORTH);
		this.add(buildInstrumentsPanel(), BorderLayout.CENTER);
		
		this.pack();
	}
	
	private final JPanel buildInstrumentsPanel() {
		final JPanel pan = new JPanel();
		
		final JPanel pos_pan = new JPanel();
		pos_pan.setBorder(BorderFactory.createTitledBorder("Position"));
		
		altimetre = new Gauge();
		altimetre.setUnitString("m");
		altimetre.setMin(0);
		altimetre.setMax(20);
		pos_pan.add(altimetre);
		
		yaw = new Boussole();
		pos_pan.add(yaw);
		
		pitch_roll = new Horizon();
		pos_pan.add(pitch_roll);
		
		pan.add(pos_pan);
		
		final JPanel vitesse_pan = new JPanel();
		vitesse_pan.setBorder(BorderFactory.createTitledBorder("Vitesses"));
		
		up_speed = new Gauge();
		up_speed.setUnitString("km/h");
		up_speed.setMin(0);
		up_speed.setMax(30);
		vitesse_pan.add(up_speed);
		
		forward_speed = new Gauge();
		forward_speed.setUnitString("km/h");
		forward_speed.setMin(0);
		forward_speed.setMax(30);
		vitesse_pan.add(forward_speed);
		
		pan.add(vitesse_pan);
		
		final JPanel batterie_pan = new JPanel();
		batterie_pan.setBorder(BorderFactory.createTitledBorder("Batterie"));
		
		batterie = new Gauge();
		batterie.setUnitString("%");
		batterie.setMin(0);
		batterie.setMax(100);
		batterie_pan.add(batterie);
		
		batterieTime = new ValueDisplay();
		batterieTime.setUnit("min");
		batterie_pan.add(batterieTime);
		
		pan.add(batterie_pan);
		
		final JPanel console_pan = new JPanel();
		console_pan.setBorder(BorderFactory.createTitledBorder("Console"));
		
		console = new JTextArea();
		console.setEditable(false);
		final JScrollPane scrollConsole = new JScrollPane(console);
		scrollConsole.setAutoscrolls(true);
		scrollConsole.setPreferredSize(new Dimension(600, 200));
		console_pan.add(scrollConsole);
		
		pan.add(console_pan);
		
		return pan;
	}
	
	private final JPanel buildTopPanel() {
		final JPanel top = new JPanel();
		
		top.add(new JLabel("Pilote :"));
		
		final JComboBox<String> pilote_joystick = new JComboBox<String>();
		for (int i=0 ; i<JXInputManager.getNumberOfDevices() ; i++) {
			pilote_joystick.addItem(JXInputManager.getJXInputDevice(i).getName());
		}
		pilote_joystick.addItem("Clavier");
		top.add(pilote_joystick);
		
		final JButton pilote_config = new JButton("Config...");
		pilote_config.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				openConfigFor(pilote, (String)pilote_joystick.getItemAt(pilote_joystick.getSelectedIndex()));
			}
		});
		top.add(pilote_config);
		
		/*top.add(new JLabel("Co-pilote :"));
		
		final JComboBox<String> copilote_joystick = new JComboBox<String>();
		copilote_joystick.addItem("Aucun");
		for (int i=0 ; i<JXInputManager.getNumberOfDevices() ; i++) {
			copilote_joystick.addItem(JXInputManager.getJXInputDevice(i).getName());
		}
		copilote_joystick.addItem("Clavier");
		top.add(copilote_joystick);
		
		final JButton copilote_config = new JButton("Config...");
		copilote_config.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				openConfigFor(copilote, (String)copilote_joystick.getItemAt(copilote_joystick.getSelectedIndex()));
			}
		});
		top.add(copilote_config);*/
		
		top.add(new JLabel("Drone :"));
		
		final JComboBox<String> drone_serial = new JComboBox<String>();
		
		Enumeration<?> enu = CommPortIdentifier.getPortIdentifiers();
		while (enu.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) enu.nextElement();
	        if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	        	drone_serial.addItem(portId.getName());
	        }
		}
		drone_serial.addItem("Simulateur");
		
		top.add(drone_serial);
		
		JButton drone_connect = new JButton("Connecter");
		top.add(drone_connect);
		
		drone_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (gcs.droneCom != null) {
					gcs.console.append("Déjà connecté\n");
					return;
				}
				String com = (String)drone_serial.getItemAt(drone_serial.getSelectedIndex());
				if (com.equalsIgnoreCase("Simulateur")) {
					gcs.droneCom = new SimCom("localhost", 11337, pilote);
					gcs.droneCom.start();
				} else {
					gcs.droneCom = new XBeeCom(com, pilote);
					gcs.droneCom.start();
				}
			}
		});
		
		return top;
	}
	
	private final JPanel buildConfigPanel(final JDialog dialog, final ControlHandler ctrlHandler) {
		JPanel config = new JPanel();
		config.setLayout(new GridLayout(0, 2));
		
		for (final Entry<String, String> entry : ctrlHandler.getControls().entrySet()) {
			config.add(new JLabel(entry.getKey()));
			final JButton btn = new JButton("<"+entry.getValue()+">");
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ctrlHandler.buttonWaitForKey(entry.getKey(), btn);
				}
			});
			config.add(btn);
		}
		
		final JButton ok_btn = new JButton("OK");
		config.add(ok_btn);
		
		ok_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
				ctrlHandler.resetWaitForKey();
			}
		});
		
		return config;
	}
	
	public JXInputDevice getDevice(String name) {
		for (int i=0 ; i<JXInputManager.getNumberOfDevices() ; i++) {
			JXInputDevice d = JXInputManager.getJXInputDevice(i);
			if (d.getName().equals(name)) {
				return d;
			}
		}
		return null;
	}
	
	public void openConfigFor(ControlHandler ctrlHandler, String name) {
		if (name.equalsIgnoreCase("Aucun"))
			return;
		if (name.equalsIgnoreCase("Clavier")) {
			ctrlHandler.setDevice(null);
		} else {
			final JXInputDevice device = getDevice(name);
			if (device==null)
				return;
			ctrlHandler.setDevice(device);
		}
		
		final JDialog dialog = new JDialog(this);
		dialog.setTitle("Options du "+ctrlHandler.getName().toLowerCase());
		dialog.setModal(true);
		dialog.setSize(300, 200);
		dialog.setContentPane(this.buildConfigPanel(dialog, ctrlHandler));
		dialog.setVisible(true);
	}
	
	public ControlHandler getPilote() {
		return pilote;
	}

	public Gauge getAltimetre() {
		return altimetre;
	}

	public Boussole getYaw() {
		return yaw;
	}

	public Horizon getPitchRoll() {
		return pitch_roll;
	}

	public Gauge getUpSpeed() {
		return up_speed;
	}

	public Gauge getForwardSpeed() {
		return forward_speed;
	}

	public Gauge getBatterie() {
		return batterie;
	}

	public ValueDisplay getBatterieTime() {
		return batterieTime;
	}

	public JTextArea getConsole() {
		return console;
	}

	public static GroundControl getGCS() {
		return gcs;
	}
	
	public static void main(String[] args) {
		final GroundControl gc = new GroundControl();
		gc.setVisible(true);
	}
}
