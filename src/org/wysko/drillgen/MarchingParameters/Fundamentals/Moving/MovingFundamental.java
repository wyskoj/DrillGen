package org.wysko.drillgen.MarchingParameters.Fundamentals.Moving;

import org.wysko.drillgen.MarchingParameters.Fundamentals.Fundamental;
import org.wysko.drillgen.MarchingParameters.StepSize;

/**
 * A type of {@link Fundamental} that travels a performer some non-zero, indefinite distance across the field.
 */
public abstract class MovingFundamental extends Fundamental {
	
	/**
	 * The distance to travel in the fundamental.
	 */
	final int length;
	
	/**
	 * The {@link StepSize} of the fundamental.
	 */
	final StepSize stepSize;
	
	/**
	 * @param length   the distance to travel in the fundamental
	 * @param stepSize the {@link StepSize} of the fundamental
	 */
	protected MovingFundamental(int length, StepSize stepSize) {
		this.length = length;
		this.stepSize = stepSize;
	}
}
