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

package org.wysko.drillgen.MarchingParameters;

/**
 * The spacing in between steps at which performers execute fundamentals.
 */
public enum StepSize {
	
	/**
	 * Eight steps for every five yards marched. 22.5 inches in between each step.
	 */
	EIGHT_TO_FIVE("8:5"),
	
	/**
	 * Sixteen steps for every five yards marched. 11.25 inches in between each step.
	 */
	SIXTEEN_TO_FIVE("16:5"),
	
	/**
	 * Six steps for every five yards marched. 30 inches in between each step.
	 */
	SIX_TO_FIVE("6:5"),
	
	/**
	 * Twelve steps for every five yards marched. 15 inches in between each step.
	 */
	TWELVE_TO_FIVE("12:5");
	
	final String s;
	
	StepSize(String s) {
		this.s = s;
	}
	
	@Override
	public String toString() {
		return s;
	}
}
