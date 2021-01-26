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

import org.junit.Before;
import org.junit.Test;
import org.wysko.drillgen.Drill;
import org.wysko.drillgen.MarchingParameters.Direction.RelativeDirection;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.Box;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.March;
import org.wysko.drillgen.MarchingParameters.StepSize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DrillTest {
	
	Drill drill;
	
	@Before
	public void setUp() {
		drill = new Drill();
	}
	
	@Test
	public void add() {
		drill.add(new March(8, StepSize.EIGHT_TO_FIVE, RelativeDirection.YDirection.FORWARDS));
		assertFalse(drill.isEmpty());
	}
	
	@Test
	public void peek() {
		drill.add(new Box(4, StepSize.SIX_TO_FIVE, RelativeDirection.XDirection.LEFT));
		assertEquals(new Box(4, StepSize.SIX_TO_FIVE, RelativeDirection.XDirection.LEFT), drill.peek());
	}
	
	@Test
	public void asString() {
		drill.add(new March(8,StepSize.EIGHT_TO_FIVE, RelativeDirection.YDirection.BACKWARDS));
		drill.add(new March(4,StepSize.EIGHT_TO_FIVE, RelativeDirection.YDirection.FORWARDS));
		String expected1 = "[BM8@8:5, FM4@8:5]";
		String expected2 = "[BM8, FM4]";
		String expected3 = "[BM8@8:5, RR, FM4@8:5]";
		String expected4 = "[BM8, RR, FM4]";
		assertEquals(expected1,drill.asString(false,false));
		assertEquals(expected2,drill.asString(false,true));
		assertEquals(expected3,drill.asString(true,false));
		assertEquals(expected4,drill.asString(true,true));
	}
	
	@Test
	public void testEquals() {
		Drill drill = new Drill();
		Drill drill1 = new Drill();
		assertEquals(drill,drill1);
		drill.add(new March(4,StepSize.EIGHT_TO_FIVE, RelativeDirection.YDirection.FORWARDS));
		drill1.add(new March(4,StepSize.EIGHT_TO_FIVE, RelativeDirection.YDirection.FORWARDS));
		assertEquals(drill,drill1);
	}
}