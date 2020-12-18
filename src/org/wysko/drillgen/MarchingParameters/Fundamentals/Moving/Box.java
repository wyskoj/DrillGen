package org.wysko.drillgen.MarchingParameters.Fundamentals.Moving;

import org.wysko.drillgen.MarchingParameters.Direction;
import org.wysko.drillgen.MarchingParameters.StepSize;

/**
 * A box consists of four forwards marches (if non traversed) or three forwards and one backwards (if traversed) to
 * form a square. <i>This class does not represents the components of the box and rather treats the whole box as one
 * fundamental.</i>
 */
public class Box extends MovingFundamental {
	
	/**
	 * The {@link Direction.XDirection} of the box.
	 */
	final Direction.XDirection direction;
	
	/**
	 * @param length    the length of the box
	 * @param stepSize  the {@link StepSize} of the box
	 * @param direction the {@link Direction.XDirection} of the box
	 */
	protected Box(int length, StepSize stepSize, Direction.XDirection direction) {
		super(length, stepSize);
		this.direction = direction;
	}
	
	@Override
	public String toString() {
		return String.format("Box%s%d@%s", direction, length, stepSize);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Box box = (Box) o;
		
		return direction == box.direction && length == box.length && stepSize == box.stepSize;
	}
}
