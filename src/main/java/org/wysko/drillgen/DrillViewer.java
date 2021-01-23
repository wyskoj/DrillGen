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

@Deprecated
public class DrillViewer extends PApplet {
	
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
	
	PShape leftFlank;
	
	@Override
	public void draw() {
		background(255, 255, 255);
		drawField();
		drawDashedLine(new FieldCoordinate(0,0),new FieldCoordinate(-3,0));
		drawStart(new FieldCoordinate(0, 0));
		drawLeftFlank(new FieldCoordinate(-3,0), EndFlankDir.S);
	}
	
	enum EndFlankDir {
		N,S,E,W;
	}
	
	public void drawLeftFlank(FieldCoordinate coordinate, EndFlankDir endFlankDir) {
		final PixelCoordinate p = coordinate.toPixelCoordinate();
		rotate(radians(45));
		shape(leftFlank,p.x,p.y,50,50);
	}
	
	
	public void drawDashedLine(FieldCoordinate start, FieldCoordinate end) {
		double distance = FieldCoordinate.distance(start, end);
		stroke(255,0, 0);
		strokeWeight(3);
		for (double d = 0; d + 0.01 < 1; d += 0.2 / distance) {
			final FieldCoordinate lerp = FieldCoordinate.lerp(start, end, d);
			final FieldCoordinate lerp2 = FieldCoordinate.lerp(start, end, d + 0.01);
			final PixelCoordinate p = lerp.toPixelCoordinate();
			final PixelCoordinate p2 = lerp2.toPixelCoordinate();
			line(p.x, p.y, p2.x, p2.y);
		}
	}
	
	public void drawStart(FieldCoordinate coordinate) {
		// Draw circle
		final PixelCoordinate p = coordinate.toPixelCoordinate();
		strokeWeight(3);
		stroke(0);
		fill(255, 0, 0);
		ellipse(p.x, p.y, 20, 20);
	}
	
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
	
	static class FieldCoordinate {
		final double x;
		final double y;
		
		public FieldCoordinate(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		static double distance(FieldCoordinate a, FieldCoordinate b) {
			return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
		}
		
		static FieldCoordinate lerp(FieldCoordinate a, FieldCoordinate b, double x) {
			return new FieldCoordinate(
					((b.x - a.x) * x) + a.x,
					((b.y - a.y) * x) + a.y
			);
		}
		
		PixelCoordinate toPixelCoordinate() {
			return new PixelCoordinate((int) ((x * 50) + 200), (int) ((y * 50) + 20));
		}
	}
	
	static class PixelCoordinate {
		final int x;
		final int y;
		
		public PixelCoordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
