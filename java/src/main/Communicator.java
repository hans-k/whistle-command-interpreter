package main;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * Listens to all data coming in over the specified Serial Port. Crafts a Map containing 
 * decoded frequency measurements and how much they have been seen in the last 20
 * readings. This Map is an input for {@link GraphPanel} and {@link TriggerCombinationManager}.
 * 
 * @author Hans Kruijsse
 */
public class Communicator {
	private static final int BAUD_RATE = 115200;
	public static final String PORT_NAME = "COM3";

	private static final int COMM_BUFFER_SIZE = 10;
	private static char[] buffer = new char[COMM_BUFFER_SIZE];
	private static int bufferPointer = 0;

	/** The max. number of measurement values to be remembered at any one time. 
	 * Changing this impacts performance and probably unbalances configured whistle combo's. */
	private static final int VALUE_BUFFER_SIZE = 20;
	
	/** Keeps track of the last 20 measurements, in order. */
	private static List<Integer> linearValueBuffer = new LinkedList<>();
	
	/** Keeps track of the last 20 measurements. The first Integer corresponds to 
	 * frequency, the second to how much it has been seen in the last 20 readings. */
    private Map<Integer, Integer> valueMap = new ConcurrentHashMap<Integer, Integer>();
	
	private GraphPanel graphUI;
	private TriggerCombinationManager triggerCombinationManager;
	
	public Communicator() {
		
	}
	
	public void init() {
		SerialPort userPort = SerialPort.getCommPort(PORT_NAME);
		userPort.setBaudRate(BAUD_RATE);
		userPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 0);
		// userPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 100, 0);
		
		userPort.openPort();
		if (userPort.isOpen()) {
			System.out.println("Port initialized!");
		} else {
			throw new RuntimeException("Port '" + PORT_NAME + "' not found.");
		}

		userPort.addDataListener(new SerialPortDataListener() {
			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
			}

			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
					return;
				byte[] bytes = new byte[userPort.bytesAvailable()];
				userPort.readBytes(bytes, bytes.length);

				for (int i = 0; i < bytes.length; i++) {
					char c = (char) (bytes[i] & 0xFF);
					if (bufferPointer == 8) {
						dumpBuffer();
					} else if (c >= '0' && c <= ':') {
						buffer[bufferPointer] = c;
						bufferPointer++;
					} else if (c == ';' || bufferPointer == COMM_BUFFER_SIZE - 1) {
						dumpBuffer();
					}
				}
			}
		});
	}
	
	/**
	 * When the communication byte buffer is full, this method 'dumps' its
	 * content to a numeric measurement and hands it over to handleNewMeasurement().
	 */
	private void dumpBuffer() {
		String result = "";
		for (int i = 0; i < bufferPointer; i++) {
			result += buffer[i];
		}
		// System.out.println(result);
		try {
			handleNewMeasurement(Integer.parseInt(result.substring(1)));
		} catch (NumberFormatException e) {
			System.out.println("No number recognized (probably still starting up): '" + result + "'");
		}
		buffer = new char[COMM_BUFFER_SIZE];
		bufferPointer = 0;
	}

	/**
	 * Adds a new measurement to both the linear buffer and the Map. If the 
	 * measurement buffer is full, the oldest measurement is removed to make space.
	 */
	private void handleNewMeasurement(int newMeasurement) {
		if (linearValueBuffer.size() >= VALUE_BUFFER_SIZE) {
			Integer removedInteger = linearValueBuffer.remove(0);
			removeMeasurementFromMap(removedInteger);
		}
		addMeasurementToMap(newMeasurement);
        recalculateAndRepaint();
		linearValueBuffer.add(newMeasurement);
	}
    
    private void recalculateAndRepaint() {
        if (graphUI != null)
        	graphUI.setValuesAndRepaint(valueMap);
        if (triggerCombinationManager != null)
        	triggerCombinationManager.calculate(valueMap);
    }
    
    /**
     * Adds a measurement to the Map, as key. If the key is already in
     * there, increase its corresponding value by 1.
     */
    private void addMeasurementToMap(Integer key) {
    	Integer value = valueMap.get(key);
    	if (value == null) {
    		this.valueMap.put(key, 1);
    	} else {
    		valueMap.put(key, value + 1);
    	}
    }

    /**
     * Removes a measurement (a key) from the Map. If its corresponding value is bigger
     * than 1 however, it subtracts 1 from the value instead of removing the key.
     */
    private void removeMeasurementFromMap(Integer key) {
    	Integer value = valueMap.get(key);
    	if (value == null || value == 1) {
    		this.valueMap.remove(key);
    	} else {
    		valueMap.put(key, value - 1);
    	}
    }
	
    
	public void setGraphUI(GraphPanel graphUI) {
		this.graphUI = graphUI;
	}
	
	public void setTriggerCombinationManager(TriggerCombinationManager tcManager) {
		this.triggerCombinationManager = tcManager;
	}
	
}
