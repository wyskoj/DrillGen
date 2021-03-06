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

import org.wysko.drillgen.MarchingParameters.Fundamentals.Fundamental;
import org.wysko.drillgen.MarchingParameters.StepSize;

/**
 * A type of {@link Fundamental} that travels a performer some non-zero, indefinite distance across the field.
 */
public abstract class MovingFundamental extends Fundamental {
	
	/**
	 * The distance to travel in the fundamental.
	 */
	public final int length;
	
	/**
	 * The {@link StepSize} of the fundamental.
	 */
	public final StepSize stepSize;
	
	/**
	 * @param length   the distance to travel in the fundamental
	 * @param stepSize the {@link StepSize} of the fundamental
	 */
	protected MovingFundamental(int length, StepSize stepSize) {
		this.length = length;
		this.stepSize = stepSize;
	}
}
