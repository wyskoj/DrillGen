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

package org.wysko.drillgen.MarchingParameters.Direction;

/**
 * An orthogonal, relative, theoretical line relative to the marcher's body.
 */
public class RelativeDirection {
	/**
	 * Directions relative to the sagittal plane of the marcher's body.
	 */
	public enum YDirection {
		
		/**
		 * The direction toward the front, opposite of back.
		 */
		FORWARDS("F"),
		
		/**
		 * The direction toward the back, opposite of front.
		 */
		BACKWARDS("B");
		
		final String s;
		
		YDirection(String s) {
			this.s = s;
		}
		
		@Override
		public String toString() {
			return s;
		}
	}
	
	
	/**
	 * Directions relative to the coronal plane of the marcher's body.
	 */
	public enum XDirection {
		
		/**
		 * The opposite of right; toward the west when one is facing north.
		 */
		LEFT("L"),
		
		/**
		 * The opposite of left; toward the east when one is facing north.
		 */
		RIGHT("R");
		
		final String s;
		
		XDirection(String s) {
			this.s = s;
		}
		
		@Override
		public String toString() {
			return s;
		}
	}
}
