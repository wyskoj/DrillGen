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
 * Created by JFormDesigner on Mon Jan 25 23:19:15 EST 2021
 */

package org.wysko.drillgen.GUI;

import javax.swing.event.*;
import org.wysko.drillgen.Utils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Jacob Wysko
 */
public class About extends JFrame {
	public About() {
		initComponents();
	}
	
	private void createUIComponents() {
		// TODO: add custom component creation code here
	}
	
	private void gnuHyperlink(HyperlinkEvent e) {
		showUrlOnActive(e);
	}
	
	private void showUrlOnActive(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
			Utils.displayWebpage(
					e.getURL().toString()
			);
	}
	
	private void websiteHyperlink(HyperlinkEvent e) {
		showUrlOnActive(e);
	}
	
	private void websiteHyperlinkl(HyperlinkEvent e) {
		// TODO add your code here
	}
	
	private void websiteLabelMouseEntered(MouseEvent e) {
		websiteLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	private void websiteLabelMouseExited(MouseEvent e) {
		websiteLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void websiteLabelMouseClicked(MouseEvent e) {
		Utils.displayWebpage("https://github.com/wyskoj/DrillGen");
	}
	
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		this2 = new JPanel();
		label1 = new JLabel();
		label2 = new JLabel();
		label3 = new JLabel();
		label4 = new JLabel();
		websiteLabel = new JLabel();
		label6 = new JLabel();
		label7 = new JLabel();
		label9 = new JEditorPane();

		//======== this ========
		setTitle("About DrillGen");
		setResizable(false);
		setIconImage(new ImageIcon(getClass().getResource("/logo.png")).getImage());
		var contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== this2 ========
		{
			this2.setLayout(new GridBagLayout());
			((GridBagLayout)this2.getLayout()).columnWidths = new int[] {0, 0};
			((GridBagLayout)this2.getLayout()).rowHeights = new int[] {126, 38, 30, 30, 19, 35, 0, 0, 0, 0};
			((GridBagLayout)this2.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
			((GridBagLayout)this2.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

			//---- label1 ----
			label1.setIcon(new ImageIcon(getClass().getResource("/logo.png")));
			this2.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.SOUTH, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));

			//---- label2 ----
			label2.setText("DrillGen");
			label2.setFont(label2.getFont().deriveFont(label2.getFont().getStyle() | Font.BOLD));
			this2.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 0, 0));

			//---- label3 ----
			label3.setText("v 0.0.1");
			this2.add(label3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 0, 0));

			//---- label4 ----
			label4.setText("Generates marching band drills.");
			this2.add(label4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 0, 0));

			//---- websiteLabel ----
			websiteLabel.setFont(new Font("Segoe UI", websiteLabel.getFont().getStyle(), websiteLabel.getFont().getSize()));
			websiteLabel.setBackground(UIManager.getColor("EditorPane.background"));
			websiteLabel.setText("<html><a href>Website</a></html>");
			websiteLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					websiteLabelMouseClicked(e);
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					websiteLabelMouseEntered(e);
				}
				@Override
				public void mouseExited(MouseEvent e) {
					websiteLabelMouseExited(e);
				}
			});
			this2.add(websiteLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));

			//---- label6 ----
			label6.setText("<html>Copyright &copy; 2021 Jacob Wysko</html>");
			label6.setFont(label6.getFont().deriveFont(label6.getFont().getSize() - 2f));
			this2.add(label6, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));

			//---- label7 ----
			label7.setText("<html>This program comes with absolutely no warranty.</html>\n");
			label7.setFont(label7.getFont().deriveFont(label7.getFont().getSize() - 2f));
			label7.setHorizontalAlignment(SwingConstants.CENTER);
			this2.add(label7, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 0, 0));

			//---- label9 ----
			label9.setFont(new Font("Segoe UI", label9.getFont().getStyle(), label9.getFont().getSize() - 2));
			label9.setBackground(UIManager.getColor("EditorPane.background"));
			label9.setContentType("text/html");
			label9.setText("See the <a href=\"https://opensource.org/licenses/MIT\">MIT License</a> for details.");
			label9.setEditable(false);
			label9.addHyperlinkListener(e -> gnuHyperlink(e));
			this2.add(label9, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 0, 0));
		}
		contentPane.add(this2, BorderLayout.CENTER);
		setSize(445, 365);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
	
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel this2;
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JLabel label4;
	private JLabel websiteLabel;
	private JLabel label6;
	private JLabel label7;
	private JEditorPane label9;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
