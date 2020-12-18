package org.wysko.drillgen.MarchingParameters.Fundamentals.Moving;

import org.wysko.drillgen.MarchingParameters.Direction;
import org.wysko.drillgen.MarchingParameters.StepSize;

/**
 * A continued roll step to move the performer forwards or backwards.
 */
public class March extends MovingFundamental {
	
	/**
	 * The {@link Direction.YDirection} of the march.
	 */
	public final Direction.YDirection direction;
	
	/**
	 * @param length    the length of the march
	 * @param stepSize  the {@link StepSize} of the march
	 * @param direction the {@link Direction.YDirection} of the march
	 */
	public March(int length, StepSize stepSize, Direction.YDirection direction) {
		super(length, stepSize);
		this.direction = direction;
	}
	
	@Override
	public String toString() {
		return String.format("%sM%s@%s", direction, length, stepSize);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		March march = (March) o;
		
		return direction == march.direction && length == march.length && stepSize == march.stepSize;
	}
}
