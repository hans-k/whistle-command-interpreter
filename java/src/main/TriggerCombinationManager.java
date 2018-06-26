package main;

import java.util.List;
import java.util.Map;

/**
 * Keeps a list of all TriggerCombinations and can check if their
 * Triggers are met.
 * 
 * @author Hans Kruijsse
 */
public class TriggerCombinationManager {
	List<TriggerCombination> triggerCombinationList;
	
	public TriggerCombinationManager(List<TriggerCombination> triggerCombinationList) {
		this.triggerCombinationList = triggerCombinationList;
	}

	/**
	 * Calculates for all TriggerCombinations if their current Trigger's
	 * condition is met with this new list of graph points.
	 * 
	 * @param valueMap contains all graph points.
	 */
	public void calculate(Map<Integer, Integer> valueMap) {
        for (TriggerCombination tc : triggerCombinationList) {
        	tc.checkCurrentTrigger(valueMap);
        }
	}
}
