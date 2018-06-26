package main;

/**
 * A Trigger holds a frequency and a 'level'. If the frequency reaches this
 * level, the Trigger is successful. The check for this happens from the 
 * TriggerCombination class.
 *
 * @author Hans Kruijsse
 */
public class Trigger {
	private final Integer frequency;
	private final Integer level;
	
	public Trigger(Integer frequency, Integer level) {
		this.frequency = frequency;
		this.level = level;
	}
	
	public Integer getFrequency() {
		return this.frequency;
	}

	public Integer getLevel() {
		return this.level;
	}
}
