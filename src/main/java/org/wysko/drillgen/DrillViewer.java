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

import processing.core.PApplet;
import processing.core.PShape;

/**
 * Visually displays drills.
 *
 * @deprecated
 */
@Deprecated
public class DrillViewer extends PApplet {
	
	/**
	 * The left flank SVG object.
	 */
	PShape leftFlank;
	
	public static void main(String[] args) {
		PApplet.main("org.wysko.drillgen.DrillViewer");
	}
	
	@Override
	public void settings() {
		size(400, 400);
	}
	
	@Override
	public void setup() {
		shapeMode(CENTER);
		
		leftFlank = loadShape(getClass().getResource("/leftarrow.svg").getPath());
	}
	
	@Override
	public void draw() {
		background(255, 255, 255);
		drawField();
		drawDashedLine(new FieldCoordinate(0, 0), new FieldCoordinate(-3, 0));
		drawStart(new FieldCoordinate(0, 0));
		drawLeftFlank(new FieldCoordinate(-3, 0), EndFlankDir.S);
	}
	
	/**
	 * Draws a left flank.
	 *
	 * @param coordinate  the coordinate to draw
	 * @param endFlankDir the ending direction of the flank
	 */
	public void drawLeftFlank(FieldCoordinate coordinate, EndFlankDir endFlankDir) {
		final PixelCoordinate p = coordinate.toPixelCoordinate();
		rotate(radians(45));
		shape(leftFlank, p.x, p.y, 50, 50);
	}
	
	/**
	 * Draws a dashed line from the start to the end {@link FieldCoordinate}s.
	 *
	 * @param start the start
	 * @param end   the end
	 */
	public void drawDashedLine(FieldCoordinate start, FieldCoordinate end) {
		double distance = FieldCoordinate.distance(start, end);
		stroke(255, 0, 0);
		strokeWeight(3);
		for (double d = 0; d + 0.01 < 1; d += 0.2 / distance) {
			final FieldCoordinate lerp = FieldCoordinate.lerp(start, end, d);
			final FieldCoordinate lerp2 = FieldCoordinate.lerp(start, end, d + 0.01);
			final PixelCoordinate p = lerp.toPixelCoordinate();
			final PixelCoordinate p2 = lerp2.toPixelCoordinate();
			line(p.x, p.y, p2.x, p2.y);
		}
	}
	
	/**
	 * Draws the start at the specified coordinate.
	 *
	 * @param coordinate the coordinate to draw the start
	 */
	public void drawStart(FieldCoordinate coordinate) {
		// Draw circle
		final PixelCoordinate p = coordinate.toPixelCoordinate();
		strokeWeight(3);
		stroke(0);
		fill(255, 0, 0);
		ellipse(p.x, p.y, 20, 20);
	}
	
	/**
	 * Draws the field.
	 */
	public void drawField() {
		// Draw grid lines
		strokeWeight(2);
		stroke(150);
		for (int i = 0; i <= 400; i += 50) {
			line(i, 20, i, 400);
		}
		
		for (int i = 20; i <= 400; i += 50) {
			line(0, i, 400, i);
		}
		
		// Draw sideline
		stroke(0);
		strokeWeight(10);
		line(0, 20, 400, 20);
		
		// Draw yard lines
		strokeWeight(5);
		for (int i = 100; i <= 300; i += 100) {
			line(i, 20, i, 400);
		}
	}
	
	/**
	 * Direction after flanking.
	 */
	enum EndFlankDir {
		/**
		 * North.
		 */
		N,
		/**
		 * South.
		 */
		S,
		/**
		 * East.
		 */
		E,
		/**
		 * West.
		 */
		W;
	}
	
	/**
	 * A coordinate of the field. Intersection of 50 yard line and home sideline is (0, 0).
	 */
	static class FieldCoordinate {
		/**
		 * The x-coordinate of the field.
		 */
		final double x;
		/**
		 * The y-coordinate of the field.
		 */
		final double y;
		
		/**
		 * @param x the x-coordinate of the field
		 * @param y the y-coordinate of the field
		 */
		public FieldCoordinate(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Calculates the distance between two {@code FieldCoordinate}s.
		 *
		 * @param a the first coordinate
		 * @param b the second coordinate
		 * @return the distance, in terms of 4 steps.
		 */
		static double distance(FieldCoordinate a, FieldCoordinate b) {
			return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
		}
		
		/**
		 * Linearly interpolates between two {@code FieldCoordinate}s.
		 *
		 * @param a the first coordinate
		 * @param b the second coordinate
		 * @param x the value of interpolation (0 = a, 1 = b)
		 * @return the linear interpolation
		 */
		static FieldCoordinate lerp(FieldCoordinate a, FieldCoordinate b, double x) {
			return new FieldCoordinate(
					((b.x - a.x) * x) + a.x,
					((b.y - a.y) * x) + a.y
			);
		}
		
		/**
		 * @return this as a {@link PixelCoordinate}
		 */
		PixelCoordinate toPixelCoordinate() {
			return new PixelCoordinate((int) ((x * 50) + 200), (int) ((y * 50) + 20));
		}
	}
	
	/**
	 * A coordinate on the screen.
	 */
	static class PixelCoordinate {
		/**
		 * The x-coordinate of the screen.
		 */
		final int x;
		/**
		 * The y-coordinate of the screen.
		 */
		final int y;
		
		/**
		 * @param x the x-coordinate of the screen
		 * @param y the y-coordinate of the screen
		 */
		public PixelCoordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
