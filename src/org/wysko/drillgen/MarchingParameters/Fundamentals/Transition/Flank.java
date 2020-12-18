package org.wysko.drillgen.MarchingParameters.Fundamentals.Transition;

import org.wysko.drillgen.MarchingParameters.Direction;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Fundamental;

/**
 * A short snappy movement that turns a marcher to their left or right. Takes 0 beats to complete.
 */
public class Flank extends Fundamental {
	
	/**
	 * The {@link Direction.XDirection} of the flank.
	 */
	final Direction.XDirection direction;
	
	/**
	 * A left flank.
	 */
	public static final Flank LEFT_FLANK = new Flank(Direction.XDirection.LEFT);
	/**
	 * A right flank.
	 */
	public static final Flank RIGHT_FLANK = new Flank(Direction.XDirection.RIGHT);
	
	/**
	 * @param direction the {@link Direction.XDirection} of the flank
	 */
	private Flank(Direction.XDirection direction) {
		this.direction = direction;
	}
	
	@Override
	public String toString() {
		return direction.toString() + "F";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Flank flank = (Flank) o;
		
		return direction == flank.direction;
	}
}
