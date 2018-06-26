package main;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Contains a collection of Triggers. Executes an action when all Triggers 
 * are completed in the right order and within a reasonable time frame.
 * 
 * @author Hans Kruijsse
 */
public class TriggerCombination {
	final List<Trigger> triggers;
	
	int currentTriggerIndex = 0;
	private long lastTriggeringTimestamp = 0;
	
	/** Amount of milliseconds allowed between the Triggers. Increase if you 
	 * find it hard to hit all Triggers in a row without it expiring. */
	private static final int INTERVAL_MILLIS = 1500;
	
	private Color color;
	
	public TriggerCombination (List<Trigger> triggers) {
		Random rand = new Random();
		color = new Color(rand.nextFloat(), rand.nextFloat() / 2f, rand.nextFloat());
		this.triggers = triggers;
	}
	
	public void checkCurrentTrigger(Map<Integer, Integer> valueMap) {
		if (currentTriggerIndex != 0 && System.currentTimeMillis() - lastTriggeringTimestamp > INTERVAL_MILLIS) {
			System.out.println("fail, time expired. resetting.");
			this.reset();
			return;
		}
		
		Trigger trigger = triggers.get(currentTriggerIndex);
		Integer value = valueMap.get(trigger.getFrequency());
		if (value != null && value >= trigger.getLevel()) {
			lastTriggeringTimestamp = System.currentTimeMillis();
			System.out.println("TRIGGER SUCCESS (trigger " + currentTriggerIndex + ")");
			if (currentTriggerIndex < triggers.size()-1) {
				currentTriggerIndex++;
				return;
			} else { // when the last trigger from the list has succeeded
				currentTriggerIndex++;
				this.doAction();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.reset();
			}
		}
	}
	
	public Color getColor() {
		return this.color;
	}
	
	private void doAction() {
		System.out.println("DOING THE ACTION");
		// Add action of your choice here
	}
	
	private void reset() {
		currentTriggerIndex = 0;
		lastTriggeringTimestamp = 0;
	}
}
