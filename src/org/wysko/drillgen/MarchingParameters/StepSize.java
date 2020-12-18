package org.wysko.drillgen.MarchingParameters;

/**
 * The spacing in between steps at which performers execute fundamentals.
 */
public enum StepSize {
	
	/**
	 * Eight steps for every five yards marched. 22.5 inches in between each step.
	 */
	EIGHT_TO_FIVE("8:5"),
	
	/**
	 * Sixteen steps for every five yards marched. 11.25 inches in between each step.
	 */
	SIXTEEN_TO_FIVE("16:5"),
	
	/**
	 * Six steps for every five yards marched. 30 inches in between each step.
	 */
	SIX_TO_FIVE("6:5"),
	
	/**
	 * Twelve steps for every five yards marched. 15 inches in between each step.
	 */
	TWELVE_TO_FIVE("12:5");
	
	final String s;
	
	StepSize(String s) {
		this.s = s;
	}
	
	@Override
	public String toString() {
		return s;
	}
}
