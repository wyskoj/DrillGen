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

import org.wysko.drillgen.GUI.ExceptionViewer;
import org.wysko.drillgen.GUI.Generate;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

/**
 * Various utilities
 */
public class Utils {
	/**
	 * Opens the system's default browser and displays a webpage with the given URL.
	 *
	 * @param url the URL to open
	 */
	public static void displayWebpage(String url) {
		Desktop desktop = java.awt.Desktop.getDesktop();
		try {
			//specify the protocol along with the URL
			URI oURL = new URI(url);
			desktop.browse(oURL);
		} catch (URISyntaxException | IOException exec) {
			showExceptionWithFrame(exec);
			exec.printStackTrace();
		}
	}
	
	/**
	 * Displays a {@link JOptionPane} showing an exception with {@link ExceptionViewer}.
	 *
	 * @param exec the exception to display
	 */
	public static void showExceptionWithFrame(Exception exec) {
		ExceptionViewer exceptionViewer = new ExceptionViewer();
		exceptionViewer.setExceptionTextArea(exec.toString());
		JOptionPane.showMessageDialog(Generate.generate, exceptionViewer, "Error", JOptionPane.ERROR_MESSAGE,
				UIManager.getIcon(
						"OptionPane.errorIcon"));
	}
	
	/**
	 * Gets a random item from a specified list.
	 *
	 * @param list the list
	 * @return a random item from the list
	 */
	static <T extends List<R>, R> R listRand(T list) {
		return list.get((int) (Math.random() * list.size()));
	}
	
	/**
	 * Generates a random number in an all-inclusive range.
	 *
	 * @param min the min number to generate
	 * @param max the max number to generate
	 * @return the randomly generated number
	 */
	public static int randIntRange(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}
	
}
