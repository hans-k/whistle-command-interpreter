package main;

import java.awt.Dimension;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.fazecast.jSerialComm.SerialPort;

/**
 * @author Hans Kruijsse
 */
public class Main {
	// Set one of these booleans to false to increase performance.
	public static final boolean DO_DRAW_GRAPH = true;
	public static final boolean DO_CHECK_FOR_TRIGGERS = true;
	public static final boolean DO_TRIGGER_VISUALIZATION = true;

	private static GraphPanel graphUI = new GraphPanel();
	private static TriggerCombinationManager triggerCombinationManager;
	
	public static void main(String[] args) {
		// Prepare TCManager by specifying whistle combo's (could be its own method later)
        TriggerCombinationFactory.addWhistleCombination(20, 6, 30, 6); // needs 6 times freq 20, and within 1.5 seconds 6 times freq 30.
        TriggerCombinationFactory.addWhistleCombination(130, 1); // needs 1x freq 130 to succeed.
        triggerCombinationManager = new TriggerCombinationManager(TriggerCombinationFactory.export());
		
		// Start serial communication with the right settings
		setUpCommunicator();

		// Launch UI
		if (DO_DRAW_GRAPH) {
			if (DO_CHECK_FOR_TRIGGERS && DO_TRIGGER_VISUALIZATION) {
				graphUI.setTriggerCombinationManager(triggerCombinationManager);
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGui();
				}
			});
		}

		// Schedule port-checking service
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(Main::checkPort, 0, 10, TimeUnit.SECONDS);
	}

	private static void setUpCommunicator() {
		Communicator comm = new Communicator();
		if (DO_DRAW_GRAPH) {
			comm.setGraphUI(graphUI);
		}
		if (DO_CHECK_FOR_TRIGGERS) {
			comm.setTriggerCombinationManager(triggerCombinationManager);
		}
		comm.init();
	}
	
	// Periodically checking the port seems to prevent it from closing
	private static void checkPort() {
		System.out.println("Checking port...");
		if (SerialPort.getCommPort(Communicator.PORT_NAME).isOpen()) {
			System.out.println("Port is open.");
		} else {
			System.out.println("Port is closed.");
		}
	}

	private static void createAndShowGui() {
		graphUI.setPreferredSize(new Dimension(800, 600));
		JFrame frame = new JFrame("Sound Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(graphUI);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
