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

import org.wysko.drillgen.MarchingParameters.Fundamentals.Fundamental;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.Box;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.March;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.MovingFundamental;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Transition.RockAndRoll;
import org.wysko.drillgen.MarchingParameters.RelativeDirection;
import org.wysko.drillgen.MarchingParameters.StepSize;

import java.util.ArrayList;
import java.util.List;

public class Drill {
	private final List<Fundamental> drills;
	
	public Drill() {
		this.drills = new ArrayList<>();
	}
	
	public void add(Fundamental fundamental) {
		drills.add(fundamental);
	}
	
	public Fundamental peek() {
		return drills.get(drills.size() - 1);
	}
	
	public String asString(boolean showRockAndRolls, boolean showStepSizeIfOnlyEightToFive) {
		String s;
		if (!showRockAndRolls) {
			s = drills.toString();
		} else {
			List<Fundamental> temp = new ArrayList<>(drills);
			for (int i = 0; i < temp.size(); i++) {
				if (i + 1 >= temp.size()) break;
				if (
						(
								temp.get(i) instanceof March
										&& temp.get(i + 1) instanceof March
										&& ((March) temp.get(i)).direction != ((March) temp.get(i + 1)).direction
						)
								||
								(
										temp.get(i) instanceof March && ((March) temp.get(i)).direction == RelativeDirection.YDirection.BACKWARDS && temp.get(i + 1) instanceof Box
								)
				) {
					temp.add(i + 1, new RockAndRoll());
					
				}
			}
			s = temp.toString();
		}
		if (showStepSizeIfOnlyEightToFive) {
			boolean allEightToFive = drills
					.stream()
					.filter(fundamental -> fundamental instanceof MovingFundamental)
					.noneMatch(fundamental -> ((MovingFundamental) fundamental).stepSize != StepSize.EIGHT_TO_FIVE);
			if (allEightToFive) {
				s = s.replaceAll("@8:5", "");
			}
		}
		return s;
	}
	
	public boolean isEmpty() {
		return drills.isEmpty();
	}
}
