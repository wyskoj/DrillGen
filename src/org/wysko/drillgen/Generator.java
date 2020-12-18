package org.wysko.drillgen;

import org.wysko.drillgen.MarchingParameters.Direction;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Fundamental;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.March;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Transition.Flank;
import org.wysko.drillgen.MarchingParameters.StepSize;

import java.util.*;

public class Generator {
	static Random rand;
	
	static {
		rand = new Random();
		rand.setSeed(System.currentTimeMillis());
	}
	
	public static void main(String[] args) throws InvalidDifficultyStepSizeCombo {
		
		
		// EASY (2-3)
		// Forwards march
		// Backwards march
		// Left/Right flank
		// 8:5
		
		// MEDIUM (2-4)
		// Forwards march
		// Backwards march
		// Left/Right flank
		// Boxes
		// 8:5
		// 16:5
		
		// HARD (3-4)
		// Forwards march
		// Backwards march
		// Left/Right flank
		// Boxes
		// 8:5
		// 16:5
		// 6:5
		
		// EXPERT (4-5)
		// Forwards march
		// Backwards march
		// Left/Right flank
		// Boxes
		// 8:5
		// 16:5
		// 6:5
		// 12:5
		
		final Difficulty difficulty = Difficulty.BEGINNER;
		List<Stack<Fundamental>> drills = new ArrayList<>();
		for (int i = 0; i < 1E6; i++) {
			Stack<Fundamental> e = makeADrill(difficulty);
			if (!drills.contains(e))
				drills.add(e);
		}
		drills.forEach(System.out::println);
	}
	
	
	private static Stack<Fundamental> makeADrill(Difficulty difficulty) throws InvalidDifficultyStepSizeCombo {
		Stack<Fundamental> drill = new Stack<>();
		switch (difficulty) {
			case BEGINNER:
				// BEGINNER (1)
				// Forwards march
				// Backwards march
				// 8:5
				Direction.YDirection direction1 = Direction.randomYDirection();
				
				drill.push(new March(randLength(difficulty, StepSize.EIGHT_TO_FIVE), StepSize.EIGHT_TO_FIVE,
						direction1));
				if (rand.nextBoolean()) {
					
					drill.push(new March(randLength(difficulty, StepSize.EIGHT_TO_FIVE), StepSize.EIGHT_TO_FIVE,
							direction1 == Direction.YDirection.FORWARDS ? Direction.YDirection.BACKWARDS : Direction.YDirection.FORWARDS));
				}
				break;
			case EASY:
				int var = rand.nextInt(3);
				switch (var) {
					case 0:
						Direction.YDirection direction = Direction.randomYDirection();
						drill.push(new March(
								randLength(Difficulty.EASY, StepSize.EIGHT_TO_FIVE),
								StepSize.EIGHT_TO_FIVE,
								direction
						));
						drill.push(new March(
								randLength(Difficulty.EASY, StepSize.EIGHT_TO_FIVE),
								StepSize.EIGHT_TO_FIVE,
								direction == Direction.YDirection.FORWARDS ? Direction.YDirection.BACKWARDS :
										Direction.YDirection.FORWARDS
						));
						break;
					case 1:
						drill.push(new March(
								randLength(Difficulty.EASY, StepSize.EIGHT_TO_FIVE),
								StepSize.EIGHT_TO_FIVE,
								Direction.YDirection.FORWARDS
						));
						drill.push(rand.nextBoolean() ? Flank.RIGHT_FLANK : Flank.LEFT_FLANK);
						drill.push(new March(
								randLength(Difficulty.EASY, StepSize.EIGHT_TO_FIVE),
								StepSize.EIGHT_TO_FIVE,
								Direction.YDirection.FORWARDS
						));
						break;
					case 2:
						direction = Direction.randomYDirection();
						for (int i = 0; i < 3; i++) {
							drill.push(new March(
									randLength(Difficulty.EASY, StepSize.EIGHT_TO_FIVE),
									StepSize.EIGHT_TO_FIVE,
									i % 2 == 0 ? (direction) : (direction == Direction.YDirection.FORWARDS ?
											Direction.YDirection.BACKWARDS : Direction.YDirection.FORWARDS)
							)); // nested ternary just flips direction on every other
						}
						break;
				}
				break;
		}
		return drill;
	}
	
	/**
	 * Generates a random length based on {@link Difficulty} and {@link StepSize}. The following table demonstrates
	 * possible values.
	 * <table><thead><tr><th></th><th colspan="5">Difficulty</th>
	 * </tr></thead><tbody><tr><td>Step Size</td><td>Beginner</td><td>Easy</td><td>Medium</td><td>Hard</td><td>Expert</td></tr>
	 * <tr><td>8:5</td><td>4, 8</td><td>4, 8</td><td>4, 8, 16</td><td>2, 4, 8, 16</td><td>2, 4, 6, 8, 16</td></tr>
	 * <tr><td>16:5</td><td></td><td></td><td>8, 16<br></td><td>8, 16<br></td><td>4, 8, 16<br></td></tr>
	 * <tr><td>6:5</td><td></td><td></td><td></td><td>6, 12</td><td>3, 6, 12</td></tr>
	 * <tr><td>12:5</td><td></td><td></td><td></td><td></td><td>6, 12</td></tr></tbody></table>
	 *
	 * @param difficulty the {@link Difficulty} to consider
	 * @param stepSize   the {@link StepSize} to consider
	 * @return the randomly generated length, considering parameters
	 * @throws InvalidDifficultyStepSizeCombo if the provided difficulty and stepsize are not valid combinations of
	 *                                        each other
	 * @throws IllegalArgumentException       if a bad difficulty is passed
	 */
	static int randLength(Difficulty difficulty, StepSize stepSize) throws InvalidDifficultyStepSizeCombo,
			IllegalArgumentException {
		// B: 4,8 for (8:5)
		// E: 4,8 for (8:5)
		// M: 4,8,16 for (8:5); 8,16 for (16:5)
		// H: 2,4,8,16 for (8:5); 8,16 for (16:5); 6,12 for (6:5)
		// X: 2,4,6,8,16 for (8:5); 4,8,16 for (16:5); 3,6,12 for (6:5); 6,12 for (12:5)
		switch (difficulty) {
			case BEGINNER:
			case EASY:
				return listRand(Arrays.asList(4, 8));
			case MEDIUM:
				switch (stepSize) {
					case EIGHT_TO_FIVE:
						return listRand(Arrays.asList(4, 8, 16));
					case SIXTEEN_TO_FIVE:
						return listRand(Arrays.asList(8, 16));
					default:
						throw new InvalidDifficultyStepSizeCombo();
				}
			case HARD:
				switch (stepSize) {
					case EIGHT_TO_FIVE:
						return listRand(Arrays.asList(2, 4, 8, 16));
					case SIXTEEN_TO_FIVE:
						return listRand(Arrays.asList(8, 16));
					case SIX_TO_FIVE:
						return listRand(Arrays.asList(6, 12));
					default:
						throw new InvalidDifficultyStepSizeCombo();
				}
			case EXPERT:
				switch (stepSize) {
					case EIGHT_TO_FIVE:
						return listRand(Arrays.asList(2, 4, 6, 8, 16));
					case SIXTEEN_TO_FIVE:
						return listRand(Arrays.asList(4, 8, 16));
					case SIX_TO_FIVE:
						return listRand(Arrays.asList(3, 6, 12));
					case TWELVE_TO_FIVE:
						return listRand(Arrays.asList(6, 12));
					default:
						throw new InvalidDifficultyStepSizeCombo();
				}
			default:
				throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Returns a random element from the passed {@link List}.
	 *
	 * @param list the list to draw from
	 * @param <T>  list type parameter
	 * @param <R>  element type parameter
	 * @return a random element from the list
	 */
	private static <T extends List<R>, R> R listRand(T list) {
		return list.get((int) (Math.random() * list.size()));
	}
	
	/**
	 * The difficulty to generate commands at.
	 */
	enum Difficulty {
		BEGINNER, EASY, MEDIUM, HARD, EXPERT
	}
}


//				for (int i = 0; i < n; i++) {
//					Class<? extends Fundamental> nextFundamental = listRand(Arrays.asList(March.class, Flank.class));
//					if (i == 0 || i == n - 1) {
//						nextFundamental = March.class;
//					}
//					Fundamental lastFundamental = null;
//					if (!drill.empty())
//						lastFundamental = drill.peek();
//
//					/* Prevents double flanking */
//					if (lastFundamental instanceof Flank) nextFundamental = March.class;
//
//					/* Prevents flanking after backwards marching (this is doable but we won't for easy) */
//					if (nextFundamental == Flank.class
//							&& lastFundamental instanceof March
//							&& ((March) lastFundamental).direction == Direction.YDirection.BACKWARDS)
//						nextFundamental = March.class;
//
//					if (nextFundamental == March.class) {
//						Direction.YDirection direction = Direction.randomYDirection();
//						if (lastFundamental instanceof March) {
//							March peek = (March) lastFundamental;
//							/* Prevents double forwards/backwards marching */
//							direction = peek.direction == Direction.YDirection.FORWARDS ?
//									Direction.YDirection.BACKWARDS : Direction.YDirection.FORWARDS;
//
//						}
//						drill.push(new March(
//								randLength(difficulty, StepSize.EIGHT_TO_FIVE),
//								StepSize.EIGHT_TO_FIVE,
//								direction
//						));
//					} else {
//						drill.push(new Flank(Direction.randomXDirection()));
//					}
//				}
