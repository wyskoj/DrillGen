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

/*
 * Created by JFormDesigner on Mon Jan 18 16:52:22 EST 2021
 */

package org.wysko.drillgen;

import java.awt.*;
import javax.swing.*;

/**
 * @author Jacob Wysko
 */
public class ExceptionViewer extends JPanel {
	public ExceptionViewer() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label1 = new JLabel();
		scrollPane1 = new JScrollPane();
		exceptionTextArea = new JTextArea();

		//======== this ========
		setLayout(new BorderLayout());

		//---- label1 ----
		label1.setText("The file could not be saved. The exception is shown below.");
		add(label1, BorderLayout.NORTH);

		//======== scrollPane1 ========
		{

			//---- exceptionTextArea ----
			exceptionTextArea.setFont(new Font(Font.MONOSPACED, exceptionTextArea.getFont().getStyle(), exceptionTextArea.getFont().getSize()));
			scrollPane1.setViewportView(exceptionTextArea);
		}
		add(scrollPane1, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
	
	public void setExceptionTextArea(String string) {
		this.exceptionTextArea.setText(string);
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label1;
	private JScrollPane scrollPane1;
	private JTextArea exceptionTextArea;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
