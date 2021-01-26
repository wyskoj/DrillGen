/*
 * MIT License
 *
 * Copyright (c) 2021 Jacob Wysko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.wysko.drillgen;

import org.jetbrains.annotations.NotNull;
import org.wysko.drillgen.MarchingParameters.Direction.CardinalDirection;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Fundamental;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.Box;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.March;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Stationary.MarkTime;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Transition.Flank;
import org.wysko.drillgen.MarchingParameters.Direction.RelativeDirection;
import org.wysko.drillgen.MarchingParameters.StepSize;

import javax.annotation.Nullable;
import java.util.*;

import static org.wysko.drillgen.Utils.listRand;

/**
 * Generates drills, customizable with {@link GeneratorSettings}.
 */
@SuppressWarnings("RedundantIfStatement")
public class DrillGenerator {
	
	/**
	 * Generates a {@link Drill} that represents the drill, based on settings.
	 *
	 * @param settings the generator settings
	 * @return the drill, null if the generation reaches an unmarchable state
	 */
	@Nullable
	public Drill generateDrill(GeneratorSettings settings) {
		Drill drill = new Drill();
		int numberOfFundamentals = Utils.randIntRange(settings.getMinCountFundamentals(),
				settings.getMaxCountFundamentals());
		
		int sidelineDistance = settings.stepsFromSideline;
		CardinalDirection currentDirection = settings.startingDirection;
		
		for (int i = 0; i < numberOfFundamentals; i++) {
			List<Fundamental> possibleFundamentals = allPossibleFundamentalsHere(settings, drill, numberOfFundamentals,
					sidelineDistance, currentDirection, i);
			
			/* Null out if impossible to finish */
			if (possibleFundamentals.isEmpty())
				return null;
			
			/* Pick a random one we just generated */
			Fundamental e = listRand(possibleFundamentals);
			drill.add(e);
			
			/* Update distance, direction */
			if (e.getClass() == March.class) {
				March march = ((March) e);
				sidelineDistance = updateDistanceToSidelineAfterMarch(sidelineDistance, currentDirection,
						march.direction, march.length, march.stepSize);
			} else if (e.getClass() == Flank.class) {
				Flank flank = ((Flank) e);
				currentDirection = updateDirectionAfterFlank(currentDirection, flank.direction);
			} else if (e.getClass() == Box.class) {
				Box box = ((Box) e);
				currentDirection = updateDirectionAfterBox(settings, currentDirection, box.direction);
			}
			
		}
		return drill;
	}
	
	/**
	 * Given a current state, returns the full list of legal {@link Fundamental}s that can be executed here.
	 *
	 * @param settings             the generator settings
	 * @param drill                the current drill
	 * @param numberOfFundamentals the total number of fundamentals to execute
	 * @param sidelineDistance     the current distance to the sideline
	 * @param currentDirection     the current direction
	 * @param i                    the index of the drill
	 * @return the full list of legal fundamentals
	 */
	@NotNull
	private List<Fundamental> allPossibleFundamentalsHere(GeneratorSettings settings, Drill drill,
	                                                      int numberOfFundamentals,
	                                                      int sidelineDistance, CardinalDirection currentDirection,
	                                                      int i) {
		List<Fundamental> possibleFundamentals = new ArrayList<>();
		/* For each fundamental type */
		
		for (Class<? extends Fundamental> validFundamental : settings.validFundamentals) {
			/* * * * * * * MARCH * * * * * * */
			if (validFundamental == March.class) {
				/* For each valid step size */
				for (Map.Entry<StepSize, List<Integer>> stepSizeLengthsEntry : settings.stepSizeLengths.entrySet()) {
					/* For each direction */
					for (RelativeDirection.YDirection yDirection : RelativeDirection.YDirection.values()) {
						/* For each valid length */
						for (int length : stepSizeLengthsEntry.getValue()) {
							if (!isMarchValid(settings, drill, sidelineDistance, currentDirection, yDirection, length))
								continue;
							possibleFundamentals.add(new March(length, stepSizeLengthsEntry.getKey(), yDirection));
						}
					}
				}
				/* * * * * * * BOX * * * * * * */
			} else if (validFundamental == Box.class) {
				for (Map.Entry<StepSize, List<Integer>> stepSizeLengthsEntry : settings.stepSizeLengths.entrySet()) {
					for (RelativeDirection.XDirection xDirection : RelativeDirection.XDirection.values()) {
						for (int length : stepSizeLengthsEntry.getValue()) {
							if (!isBoxValid(settings, sidelineDistance, currentDirection, xDirection, length))
								continue;
							possibleFundamentals.add(new Box(length, stepSizeLengthsEntry.getKey(), xDirection));
							/* Update direction */
						}
					}
				}
				/* * * * * * * FLANK * * * * * * */
			} else if (validFundamental == Flank.class) {
				for (RelativeDirection.XDirection xDirection : RelativeDirection.XDirection.values()) {
					if (!isFlankValid(settings, drill, numberOfFundamentals, i)) continue;
					possibleFundamentals.add(new Flank(xDirection));
				}
				/* * * * * * * MARK TIME * * * * * * */
			} else if (validFundamental == MarkTime.class) {
				if (!isMarkTimeValid(drill)) continue;
				for (int length : settings.validMarkTimeLengths) {
					possibleFundamentals.add(new MarkTime(length));
				}
			}
		}
		return possibleFundamentals;
	}
	
	/**
	 * Checks whether adding a {@link MarkTime} is legal at this position.
	 *
	 * @param drill the previously generated drills
	 * @return true if it is a valid mark time, false otherwise
	 */
	private boolean isMarkTimeValid(Drill drill) {
		/* Don't mark time after a mark time */
		return drill.isEmpty() || drill.peek().getClass() != MarkTime.class;
	}
	
	/**
	 * Returns the updated direction after a flank
	 *
	 * @param currentDirection the current direction
	 * @param xDirection       the direction of the flank
	 * @return the updated direction
	 */
	private CardinalDirection updateDirectionAfterFlank(CardinalDirection currentDirection,
	                                                    RelativeDirection.XDirection xDirection) {
		/* Update direction */
		if (xDirection == RelativeDirection.XDirection.LEFT) {
			currentDirection = switch (currentDirection) {
				case NORTH -> CardinalDirection.WEST;
				case EAST -> CardinalDirection.NORTH;
				case SOUTH -> CardinalDirection.EAST;
				case WEST -> CardinalDirection.SOUTH;
			};
		} else {
			currentDirection = switch (currentDirection) {
				case NORTH -> CardinalDirection.EAST;
				case EAST -> CardinalDirection.SOUTH;
				case SOUTH -> CardinalDirection.WEST;
				case WEST -> CardinalDirection.NORTH;
			};
		}
		return currentDirection;
	}
	
	/**
	 * Returns the updated direction after a box.
	 *
	 * @param settings         the generator settings
	 * @param currentDirection the current direction
	 * @param xDirection       the direction of the box
	 * @return the new direction after the box
	 */
	private CardinalDirection updateDirectionAfterBox(GeneratorSettings settings, CardinalDirection currentDirection,
	                                                  RelativeDirection.XDirection xDirection) {
		if (!settings.flankToOriginalDirectionAfterBox) {
			if (xDirection == RelativeDirection.XDirection.LEFT) {
				currentDirection = switch (currentDirection) {
					case NORTH -> CardinalDirection.EAST;
					case EAST -> CardinalDirection.SOUTH;
					case SOUTH -> CardinalDirection.WEST;
					case WEST -> CardinalDirection.NORTH;
				};
			} else {
				currentDirection = switch (currentDirection) {
					case NORTH -> CardinalDirection.WEST;
					case EAST -> CardinalDirection.NORTH;
					case SOUTH -> CardinalDirection.EAST;
					case WEST -> CardinalDirection.SOUTH;
				};
			}
		}
		return currentDirection;
	}
	
	/**
	 * Returns the appropriate updated value of the distance to the sideline after a march.
	 *
	 * @param sidelineDistance the current distance to the sideline
	 * @param currentDirection the current direction
	 * @param yDirection       the direction of the march
	 * @param length           the length of the march
	 * @param stepSize         the step size of the march
	 * @return the updated distance to the side line
	 */
	private int updateDistanceToSidelineAfterMarch(int sidelineDistance, CardinalDirection currentDirection,
	                                               RelativeDirection.YDirection yDirection, int length,
	                                               StepSize stepSize) {
		if (currentDirection == CardinalDirection.NORTH) {
			switch (yDirection) {
				case FORWARDS -> sidelineDistance -= length * StepSize.stepSizeRatio.get(stepSize);
				case BACKWARDS -> sidelineDistance += length * StepSize.stepSizeRatio.get(stepSize);
			}
		} else if (currentDirection == CardinalDirection.SOUTH) {
			switch (yDirection) {
				case FORWARDS -> sidelineDistance += length * StepSize.stepSizeRatio.get(stepSize);
				case BACKWARDS -> sidelineDistance -= length * StepSize.stepSizeRatio.get(stepSize);
			}
		}
		return sidelineDistance;
	}
	
	/**
	 * Checks whether adding a {@link Box} is legal at this position.
	 *
	 * @param settings         the generator settings
	 * @param sidelineDistance the current distance to the sideline
	 * @param currentDirection the current direction
	 * @param xDirection       the direction of the box
	 * @param length           the length of the box
	 * @return true if it is a valid box, false otherwise
	 */
	private boolean isBoxValid(GeneratorSettings settings, int sidelineDistance, CardinalDirection currentDirection,
	                           RelativeDirection.XDirection xDirection, int length) {
		/* Don't run off field */
		return settings.assumeInfiniteField || ((currentDirection != CardinalDirection.NORTH
				|| sidelineDistance - length >= 0)
				&& (currentDirection != CardinalDirection.EAST
				|| xDirection != RelativeDirection.XDirection.LEFT
				|| sidelineDistance - length >= 0)
				&& (currentDirection != CardinalDirection.WEST
				|| xDirection != RelativeDirection.XDirection.RIGHT
				|| sidelineDistance - length >= 0));
	}
	
	/**
	 * Checks whether adding a {@link Flank} is legal at this position.
	 *
	 * @param settings             the generator settings
	 * @param drill                the previously generated drills
	 * @param numberOfFundamentals the total number of fundamentals to generate
	 * @param i                    the current fundamental
	 * @return true if it is a valid flank, false otherwise
	 */
	private boolean isFlankValid(GeneratorSettings settings, Drill drill, int numberOfFundamentals,
	                             int i) {
		/* Can't flank after mark time */
		if (!drill.isEmpty() &&
				(drill.peek().getClass() == Flank.class || drill.peek().getClass() == MarkTime.class
				)
		) {
			return false;
		}
		/* Optionally don't flank after a backwards march */
		if (!drill.isEmpty()
				&& drill.peek().getClass() == March.class
				&& ((March) drill.peek()).direction == RelativeDirection.YDirection.BACKWARDS
				&& !settings.allowBackwardsFlanks) {
			return false;
		}
		/* Do not flank after box is return flank is implied */
		if (!drill.isEmpty()
				&& drill.peek().getClass() == Box.class
				&& settings.flankToOriginalDirectionAfterBox) {
			return false;
		}
		/* Don't start or end with a flank */
		if (i == 0 || i == numberOfFundamentals - 1) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whether adding a {@link March} is legal at this position.
	 *
	 * @param settings         the generator settings
	 * @param drill            the previously generated drills
	 * @param sidelineDistance the current distance from the sideline
	 * @param currentDirection the current direction
	 * @param yDirection       the direction of the march
	 * @param length           the length of the march
	 * @return true if it is a valid march, false otherwise
	 */
	private boolean isMarchValid(GeneratorSettings settings, Drill drill, int sidelineDistance,
	                             CardinalDirection currentDirection, RelativeDirection.YDirection yDirection,
	                             int length) {
		/* Cannot double backwards nor forwards march */
		if (!drill.isEmpty()
				&& drill.peek().getClass() == March.class &&
				((March) drill.peek()).direction == yDirection) {
			return false;
		}
		
		/* Optionally can't backwards march out of box */
		if (!drill.isEmpty()
				&& drill.peek().getClass() == Box.class
				&& yDirection == RelativeDirection.YDirection.BACKWARDS
				&& !settings.allowBackwardsMarchFromBox) {
			return false;
		}
		
		/* Don't run off of field */
		if (!settings.assumeInfiniteField && currentDirection == CardinalDirection.NORTH
				&& sidelineDistance - length * (yDirection == RelativeDirection.YDirection.BACKWARDS ? -1 : 1) < 0) {
			return false;
		}
		
		/* Optionally, don't backwards march after a flank */
		if (!drill.isEmpty()
				&& drill.peek().getClass() == Flank.class
				&& yDirection == RelativeDirection.YDirection.BACKWARDS
				&& !settings.allowBackwardsFlanks) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Provides settings for generating drills.
	 */
	public static class GeneratorSettings {
		public static final GeneratorSettings BEGINNER = new GeneratorSettings()
				.setValidFundamentals(Arrays.asList(March.class, MarkTime.class))
				.setAssumeInfiniteField(false)
				.setStartingDirection(null)
				.setStepsFromSideline(0)
				.setStartingDirection(CardinalDirection.WEST)
				.setMinCountFundamentals(1)
				.setMaxCountFundamentals(2)
				.setAllowBackwardsFlanks(false)
				.setAllowBackwardsMarchFromBox(false)
				.setAllowFlanksIntoMarkTimes(false)
				.setStepSizeLengths(new HashMap<>() {{
					put(StepSize.EIGHT_TO_FIVE, Arrays.asList(4, 8));
				}})
				.setValidMarkTimeLengths(Arrays.asList(4, 8))
				.setFlankToOriginalDirectionAfterBox(false);
		public static final GeneratorSettings EASY = new GeneratorSettings()
				.setValidFundamentals(Arrays.asList(March.class, MarkTime.class, Flank.class))
				.setAssumeInfiniteField(false)
				.setStartingDirection(null)
				.setStepsFromSideline(0)
				.setStartingDirection(CardinalDirection.WEST)
				.setMinCountFundamentals(2)
				.setMaxCountFundamentals(3)
				.setAllowBackwardsFlanks(false)
				.setAllowBackwardsMarchFromBox(false)
				.setAllowFlanksIntoMarkTimes(false)
				.setStepSizeLengths(new HashMap<>() {{
					put(StepSize.EIGHT_TO_FIVE, Arrays.asList(4, 8));
				}})
				.setValidMarkTimeLengths(Arrays.asList(4, 8))
				.setFlankToOriginalDirectionAfterBox(false);
		public static final GeneratorSettings MEDIUM = new GeneratorSettings()
				.setValidFundamentals(Arrays.asList(March.class, MarkTime.class, Flank.class, Box.class))
				.setAssumeInfiniteField(false)
				.setStartingDirection(null)
				.setStepsFromSideline(0)
				.setStartingDirection(CardinalDirection.WEST)
				.setMinCountFundamentals(2)
				.setMaxCountFundamentals(4)
				.setAllowBackwardsFlanks(false)
				.setAllowBackwardsMarchFromBox(false)
				.setAllowFlanksIntoMarkTimes(true)
				.setStepSizeLengths(new HashMap<>() {{
					put(StepSize.EIGHT_TO_FIVE, Arrays.asList(4, 8, 16));
					put(StepSize.SIXTEEN_TO_FIVE, Arrays.asList(8, 16));
				}})
				.setValidMarkTimeLengths(Arrays.asList(4, 8, 16))
				.setFlankToOriginalDirectionAfterBox(false);
		public static final GeneratorSettings HARD = new GeneratorSettings()
				.setValidFundamentals(Arrays.asList(March.class, MarkTime.class, Flank.class, Box.class))
				.setAssumeInfiniteField(false)
				.setStartingDirection(null)
				.setStepsFromSideline(0)
				.setStartingDirection(CardinalDirection.WEST)
				.setMinCountFundamentals(3)
				.setMaxCountFundamentals(4)
				.setAllowBackwardsFlanks(true)
				.setAllowBackwardsMarchFromBox(true)
				.setAllowFlanksIntoMarkTimes(true)
				.setStepSizeLengths(new HashMap<>() {{
					put(StepSize.EIGHT_TO_FIVE, Arrays.asList(2, 4, 8, 16));
					put(StepSize.SIXTEEN_TO_FIVE, Arrays.asList(8, 16));
					put(StepSize.SIX_TO_FIVE, Arrays.asList(6, 12));
				}})
				.setValidMarkTimeLengths(Arrays.asList(2, 4, 8, 12, 16))
				.setFlankToOriginalDirectionAfterBox(false);
		public static final GeneratorSettings EXPERT = new GeneratorSettings()
				.setValidFundamentals(Arrays.asList(March.class, MarkTime.class, Flank.class, Box.class))
				.setAssumeInfiniteField(false)
				.setStartingDirection(null)
				.setStepsFromSideline(0)
				.setStartingDirection(CardinalDirection.WEST)
				.setMinCountFundamentals(4)
				.setMaxCountFundamentals(5)
				.setAllowBackwardsFlanks(true)
				.setAllowBackwardsMarchFromBox(true)
				.setAllowFlanksIntoMarkTimes(true)
				.setStepSizeLengths(new HashMap<>() {{
					put(StepSize.EIGHT_TO_FIVE, Arrays.asList(2, 4, 6, 8, 16));
					put(StepSize.SIXTEEN_TO_FIVE, Arrays.asList(4, 8, 16));
					put(StepSize.SIX_TO_FIVE, Arrays.asList(3, 6, 12));
					put(StepSize.TWELVE_TO_FIVE, Arrays.asList(6, 12));
				}})
				.setValidMarkTimeLengths(Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 24, 32))
				.setFlankToOriginalDirectionAfterBox(false);
		/**
		 * List of valid fundamentals that can be generated.
		 */
		private List<Class<? extends Fundamental>> validFundamentals;
		/**
		 * If true, the program will not consider that the band can run off the field. If false, the program takes
		 * this into consideration and calculates available fundamentals based on {@link
		 * GeneratorSettings#startingDirection} and
		 * {@link GeneratorSettings#stepsFromSideline}.
		 */
		private boolean assumeInfiniteField;
		/**
		 * The starting direction. Only used when {@link GeneratorSettings#assumeInfiniteField} is true.
		 */
		private CardinalDirection startingDirection;
		/**
		 * The minimum number of fundamentals to appear in each drill.
		 */
		private int minCountFundamentals;
		/**
		 * The maximum number of fundamentals to appear in each drill.
		 */
		private int maxCountFundamentals;
		/**
		 * Allow backwards flanks? A backwards flank is one where the marching direction before the flank is
		 * backwards or the marching direction after the flank is backwards.
		 */
		private boolean allowBackwardsFlanks;
		/**
		 * The initial number of steps from the sideline. Only used when {@link GeneratorSettings#assumeInfiniteField}
		 * is true.
		 */
		private int stepsFromSideline;
		/**
		 * Allow a backwards march after coming out of a box?
		 */
		private boolean allowBackwardsMarchFromBox;
		/**
		 * Allows flanks before mark times.
		 */
		private boolean allowFlanksIntoMarkTimes;
		/**
		 * Adds another (implied) flank after a box.
		 */
		private boolean flankToOriginalDirectionAfterBox;
		/**
		 * Valid lengths to use given the step size.
		 */
		private HashMap<StepSize, List<Integer>> stepSizeLengths;
		/**
		 * Valid mark time lengths.
		 */
		private List<Integer> validMarkTimeLengths;
		
		public GeneratorSettings() {
			validFundamentals = new ArrayList<>();
			stepSizeLengths = new HashMap<>();
			validMarkTimeLengths = new ArrayList<>();
		}
		
		public GeneratorSettings setValidFundamentals(List<Class<? extends Fundamental>> validFundamentals) {
			this.validFundamentals = validFundamentals;
			return this;
		}
		
		public GeneratorSettings setAssumeInfiniteField(boolean assumeInfiniteField) {
			this.assumeInfiniteField = assumeInfiniteField;
			return this;
		}
		
		public GeneratorSettings setStartingDirection(CardinalDirection startingDirection) {
			this.startingDirection = startingDirection;
			return this;
		}
		
		public GeneratorSettings setMinCountFundamentals(int minCountFundamentals) {
			this.minCountFundamentals = minCountFundamentals;
			return this;
		}
		
		public GeneratorSettings setMaxCountFundamentals(int maxCountFundamentals) {
			this.maxCountFundamentals = maxCountFundamentals;
			return this;
		}
		
		public GeneratorSettings setAllowBackwardsFlanks(boolean allowBackwardsFlanks) {
			this.allowBackwardsFlanks = allowBackwardsFlanks;
			return this;
		}
		
		public GeneratorSettings setStepsFromSideline(int stepsFromSideline) {
			this.stepsFromSideline = stepsFromSideline;
			return this;
		}
		
		public GeneratorSettings setAllowBackwardsMarchFromBox(boolean allowBackwardsMarchFromBox) {
			this.allowBackwardsMarchFromBox = allowBackwardsMarchFromBox;
			return this;
		}
		
		public GeneratorSettings setStepSizeLengths(HashMap<StepSize, List<Integer>> stepSizeLengths) {
			this.stepSizeLengths = stepSizeLengths;
			return this;
		}
		
		public GeneratorSettings setValidMarkTimeLengths(List<Integer> validMarkTimeLengths) {
			this.validMarkTimeLengths = validMarkTimeLengths;
			return this;
		}
		
		public GeneratorSettings setAllowFlanksIntoMarkTimes(boolean allowFlanksIntoMarkTimes) {
			this.allowFlanksIntoMarkTimes = allowFlanksIntoMarkTimes;
			return this;
		}
		
		public GeneratorSettings setFlankToOriginalDirectionAfterBox(boolean flankToOriginalDirectionAfterBox) {
			this.flankToOriginalDirectionAfterBox = flankToOriginalDirectionAfterBox;
			return this;
		}
		
		public List<Class<? extends Fundamental>> getValidFundamentals() {
			return validFundamentals;
		}
		
		public boolean isAssumeInfiniteField() {
			return assumeInfiniteField;
		}
		
		public CardinalDirection getStartingDirection() {
			return startingDirection;
		}
		
		public int getMinCountFundamentals() {
			return minCountFundamentals;
		}
		
		public int getMaxCountFundamentals() {
			return maxCountFundamentals;
		}
		
		public boolean isAllowBackwardsFlanks() {
			return allowBackwardsFlanks;
		}
		
		public int getStepsFromSideline() {
			return stepsFromSideline;
		}
		
		public boolean isAllowBackwardsMarchFromBox() {
			return allowBackwardsMarchFromBox;
		}
		
		public boolean isAllowFlanksIntoMarkTimes() {
			return allowFlanksIntoMarkTimes;
		}
		
		public boolean isFlankToOriginalDirectionAfterBox() {
			return flankToOriginalDirectionAfterBox;
		}
		
		public HashMap<StepSize, List<Integer>> getStepSizeLengths() {
			return stepSizeLengths;
		}
		
		public List<Integer> getValidMarkTimeLengths() {
			return validMarkTimeLengths;
		}
	}
}
