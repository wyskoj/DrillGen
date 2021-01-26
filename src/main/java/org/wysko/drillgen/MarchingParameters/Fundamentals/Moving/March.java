/*
 * MIT License
 *
 * Copyright (c) 2020 Jacob Wysko
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

package org.wysko.drillgen.MarchingParameters.Fundamentals.Moving;

import org.wysko.drillgen.MarchingParameters.Direction.RelativeDirection;
import org.wysko.drillgen.MarchingParameters.StepSize;

/**
 * A continued roll step to move the performer forwards or backwards.
 */
public class March extends MovingFundamental {
	
	/**
	 * The {@link RelativeDirection.YDirection} of the march.
	 */
	public final RelativeDirection.YDirection direction;
	
	/**
	 * @param length    the length of the march
	 * @param stepSize  the {@link StepSize} of the march
	 * @param direction the {@link RelativeDirection.YDirection} of the march
	 */
	public March(int length, StepSize stepSize, RelativeDirection.YDirection direction) {
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
