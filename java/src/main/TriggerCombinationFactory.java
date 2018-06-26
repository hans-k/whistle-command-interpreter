package main;

import java.util.ArrayList;
import java.util.List;

/**
 * Handy class to create TriggerCombinations in a clear way.
 * 
 * @author Hans Kruijsse
 */
public class TriggerCombinationFactory {
	private static List<TriggerCombination> triggerCombinationList = new ArrayList<>();
	
	public static List<TriggerCombination> export() {
		return triggerCombinationList;
	}
	
	public static void addWhistleCombination(int freq1, int level1) {
		List<Trigger> triggers = new ArrayList<>();
		triggers.add(new Trigger(freq1, level1));
        TriggerCombination tc = new TriggerCombination(triggers);
        triggerCombinationList.add(tc);
	}
	public static void addWhistleCombination(int freq1, int level1, int freq2, int level2) {
		List<Trigger> triggers = new ArrayList<>();
		triggers.add(new Trigger(freq1, level1));
		triggers.add(new Trigger(freq2, level2));
        TriggerCombination tc = new TriggerCombination(triggers);
        triggerCombinationList.add(tc);
	}
	public static void addWhistleCombination(int freq1, int level1, int freq2, int level2, int freq3, int level3) {
		List<Trigger> triggers = new ArrayList<>();
		triggers.add(new Trigger(freq1, level1));
		triggers.add(new Trigger(freq2, level2));
		triggers.add(new Trigger(freq3, level3));
        TriggerCombination tc = new TriggerCombination(triggers);
        triggerCombinationList.add(tc);
	}
}
