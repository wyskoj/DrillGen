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
 * Created by JFormDesigner on Sun Jan 17 15:25:59 EST 2021
 */

package org.wysko.drillgen;

import java.awt.event.*;
import java.beans.*;
import javax.swing.event.*;
import javax.swing.table.*;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import org.japura.gui.*;

/**
 * @author Jacob Wysko
 */
public class Generate extends JFrame {
	public static void main(String[] args) {
		Generate generate = new Generate();
		generate.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		generate.setVisible(true);
		
	}
	
	private void setEnabledContainer(Container container, boolean enabled) {
		LinkedList<Component> queue = new LinkedList<>(Arrays.asList(container.getComponents()));
		container.setEnabled(enabled);
		while(!queue.isEmpty()) {
			Component head = queue.poll();
			head.setEnabled(enabled);
			if(head instanceof Container) {
				Container headCast = (Container) head;
				queue.addAll(Arrays.asList(headCast.getComponents()));
			}
		}
	}
	
	public Generate() {
		FlatLightLaf.install();
		initComponents();
		setEnabledContainer(startingPositionPanel, false);
	}

	private void minCountFundamentalsSpinnerStateChanged(ChangeEvent e) {
		if (((Integer) minCountFundamentalsSpinner.getValue()) > ((Integer) maxCountFundamentalsSpinner.getValue())) {
			minCountFundamentalsSpinner.setValue(((Integer) minCountFundamentalsSpinner.getValue()) - 1);
		}
	}

	private void maxCountFundamentalsSpinnerStateChanged(ChangeEvent e) {
		if (((Integer) maxCountFundamentalsSpinner.getValue()) < ((Integer) minCountFundamentalsSpinner.getValue())) {
			maxCountFundamentalsSpinner.setValue(((Integer) maxCountFundamentalsSpinner.getValue()) + 1);
		}
	}

	private void assumeInfiniteFieldCheckBoxActionPerformed(ActionEvent e) {
		setEnabledContainer(startingPositionPanel, assumeInfiniteFieldCheckBox.isSelected());
	}
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel2 = new JPanel();
		panel7 = new JPanel();
		assumeInfiniteFieldCheckBox = new JCheckBox();
		startingPositionPanel = new JPanel();
		label3 = new JLabel();
		stepsFromSidelineSpinner = new JSpinner();
		label5 = new JLabel();
		label4 = new JLabel();
		directionComboBox = new JComboBox<>();
		panel10 = new JPanel();
		flankToOriginalDirectionAfterBoxCheckBox = new JCheckBox();
		panel3 = new JPanel();
		beginnerRadioButton = new JRadioButton();
		easyRadioButton = new JRadioButton();
		mediumRadioButton = new JRadioButton();
		hardRadioButton = new JRadioButton();
		expertRadioButton = new JRadioButton();
		customRadioButton = new JRadioButton();
		panel4 = new JPanel();
		panel5 = new JPanel();
		marchCheckBox = new JCheckBox();
		markTimeCheckBox = new JCheckBox();
		flankCheckBox = new JCheckBox();
		boxCheckBox = new JCheckBox();
		panel6 = new JPanel();
		label1 = new JLabel();
		minCountFundamentalsSpinner = new JSpinner();
		label2 = new JLabel();
		maxCountFundamentalsSpinner = new JSpinner();
		panel9 = new JPanel();
		allowBackwardsFlanksCheckBox = new JCheckBox();
		allowBackwardsMarchFromBoxCheckBox = new JCheckBox();
		allowFlanksIntoMarkTimesCheckBox = new JCheckBox();
		panel1 = new JPanel();
		checkBox1 = new JCheckBox();
		scrollPane1 = new JScrollPane();
		list1 = new JList<>();
		checkBox3 = new JCheckBox();
		scrollPane3 = new JScrollPane();
		list3 = new JList<>();
		checkBox2 = new JCheckBox();
		scrollPane2 = new JScrollPane();
		list2 = new JList<>();
		checkBox4 = new JCheckBox();
		scrollPane4 = new JScrollPane();
		list4 = new JList<>();

		//======== this ========
		setTitle("Drill Generator");
		var contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== panel2 ========
		{
			panel2.setLayout(new GridBagLayout());
			((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
			((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {272, 288, 0};
			((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
			((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

			//======== panel7 ========
			{
				panel7.setBorder(new TitledBorder("Settings"));
				panel7.setLayout(new GridBagLayout());
				((GridBagLayout)panel7.getLayout()).columnWidths = new int[] {0, 0};
				((GridBagLayout)panel7.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
				((GridBagLayout)panel7.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
				((GridBagLayout)panel7.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

				//---- assumeInfiniteFieldCheckBox ----
				assumeInfiniteFieldCheckBox.setText("Assume infinite field");
				assumeInfiniteFieldCheckBox.setSelected(true);
				assumeInfiniteFieldCheckBox.addActionListener(e -> assumeInfiniteFieldCheckBoxActionPerformed(e));
				panel7.add(assumeInfiniteFieldCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//======== startingPositionPanel ========
				{
					startingPositionPanel.setBorder(new TitledBorder("Starting position"));
					startingPositionPanel.setLayout(new GridBagLayout());
					((GridBagLayout)startingPositionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
					((GridBagLayout)startingPositionPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
					((GridBagLayout)startingPositionPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
					((GridBagLayout)startingPositionPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

					//---- label3 ----
					label3.setText("Distance from sideline:");
					startingPositionPanel.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 5), 0, 0));

					//---- stepsFromSidelineSpinner ----
					stepsFromSidelineSpinner.setModel(new SpinnerNumberModel(0, 0, 84, 2));
					startingPositionPanel.add(stepsFromSidelineSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 5), 0, 0));

					//---- label5 ----
					label5.setText("steps");
					startingPositionPanel.add(label5, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 0), 0, 0));

					//---- label4 ----
					label4.setText("Direction:");
					startingPositionPanel.add(label4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 5), 0, 0));

					//---- directionComboBox ----
					directionComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
						"North (home sideline)",
						"East",
						"South",
						"West (away sideline)"
					}));
					startingPositionPanel.add(directionComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 5), 0, 0));
				}
				panel7.add(startingPositionPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//======== panel10 ========
				{
					panel10.setLayout(new GridBagLayout());
					((GridBagLayout)panel10.getLayout()).columnWidths = new int[] {0, 0};
					((GridBagLayout)panel10.getLayout()).rowHeights = new int[] {0, 0};
					((GridBagLayout)panel10.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
					((GridBagLayout)panel10.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

					//---- flankToOriginalDirectionAfterBoxCheckBox ----
					flankToOriginalDirectionAfterBoxCheckBox.setText("Flank to original direction after box");
					flankToOriginalDirectionAfterBoxCheckBox.setToolTipText("<html>Sometimes, the ending of a box is a cheat step to return to the original direction. When enabled, this maneuver is done.<br>When disabled, the direction of the last edge of the box is retained when the box is done.</html>");
					panel10.add(flankToOriginalDirectionAfterBoxCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
				}
				panel7.add(panel10, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			panel2.add(panel7, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));

			//======== panel3 ========
			{
				panel3.setBorder(new TitledBorder("Presets"));
				panel3.setLayout(new GridBagLayout());
				((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {95, 0};
				((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
				((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
				((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

				//---- beginnerRadioButton ----
				beginnerRadioButton.setText("Beginner");
				beginnerRadioButton.setSelected(true);
				panel3.add(beginnerRadioButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- easyRadioButton ----
				easyRadioButton.setText("Easy");
				panel3.add(easyRadioButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- mediumRadioButton ----
				mediumRadioButton.setText("Medium");
				panel3.add(mediumRadioButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- hardRadioButton ----
				hardRadioButton.setText("Hard");
				panel3.add(hardRadioButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- expertRadioButton ----
				expertRadioButton.setText("Expert");
				panel3.add(expertRadioButton, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- customRadioButton ----
				customRadioButton.setText("Custom");
				panel3.add(customRadioButton, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			panel2.add(panel3, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));

			//======== panel4 ========
			{
				panel4.setBorder(new TitledBorder("Customization"));
				panel4.setLayout(new GridBagLayout());
				((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {117, 0, 0};
				((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0, 238, 0};
				((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
				((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

				//======== panel5 ========
				{
					panel5.setBorder(new TitledBorder("Fundamentals"));
					panel5.setLayout(new GridBagLayout());
					((GridBagLayout)panel5.getLayout()).columnWidths = new int[] {0, 0};
					((GridBagLayout)panel5.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
					((GridBagLayout)panel5.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
					((GridBagLayout)panel5.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

					//---- marchCheckBox ----
					marchCheckBox.setText("March");
					marchCheckBox.setSelected(true);
					panel5.add(marchCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 0), 0, 0));

					//---- markTimeCheckBox ----
					markTimeCheckBox.setText("Mark time");
					markTimeCheckBox.setSelected(true);
					panel5.add(markTimeCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 0), 0, 0));

					//---- flankCheckBox ----
					flankCheckBox.setText("Flank");
					panel5.add(flankCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 0), 0, 0));

					//---- boxCheckBox ----
					boxCheckBox.setText("Box");
					panel5.add(boxCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
				}
				panel4.add(panel5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//======== panel6 ========
				{
					panel6.setBorder(new TitledBorder("Length of drill"));
					panel6.setLayout(new GridBagLayout());
					((GridBagLayout)panel6.getLayout()).columnWidths = new int[] {0, 68, 0};
					((GridBagLayout)panel6.getLayout()).rowHeights = new int[] {0, 0, 0};
					((GridBagLayout)panel6.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
					((GridBagLayout)panel6.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

					//---- label1 ----
					label1.setText("Minimum:");
					panel6.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 5), 0, 0));

					//---- minCountFundamentalsSpinner ----
					minCountFundamentalsSpinner.setModel(new SpinnerNumberModel(1, 1, 10, 1));
					minCountFundamentalsSpinner.addChangeListener(e -> minCountFundamentalsSpinnerStateChanged(e));
					panel6.add(minCountFundamentalsSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 0), 0, 0));

					//---- label2 ----
					label2.setText("Maximum:");
					panel6.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 5), 0, 0));

					//---- maxCountFundamentalsSpinner ----
					maxCountFundamentalsSpinner.setModel(new SpinnerNumberModel(2, 1, 10, 1));
					maxCountFundamentalsSpinner.addChangeListener(e -> maxCountFundamentalsSpinnerStateChanged(e));
					panel6.add(maxCountFundamentalsSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
				}
				panel4.add(panel6, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//======== panel9 ========
				{
					panel9.setBorder(new TitledBorder("Advanced maneuvers"));
					panel9.setLayout(new GridBagLayout());
					((GridBagLayout)panel9.getLayout()).columnWidths = new int[] {0, 0, 0};
					((GridBagLayout)panel9.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
					((GridBagLayout)panel9.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
					((GridBagLayout)panel9.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

					//---- allowBackwardsFlanksCheckBox ----
					allowBackwardsFlanksCheckBox.setText("Allow backwards flanks");
					allowBackwardsFlanksCheckBox.setToolTipText("<html>A backwards flank has a backwards march either before or after the flank.<br>Enabling this allows backwards flanks to appear in the drill.</html>");
					panel9.add(allowBackwardsFlanksCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 5), 0, 0));

					//---- allowBackwardsMarchFromBoxCheckBox ----
					allowBackwardsMarchFromBoxCheckBox.setText("Allow backwards march after box");
					allowBackwardsMarchFromBoxCheckBox.setToolTipText("When enabled, allows a backwards march to follow a box.");
					panel9.add(allowBackwardsMarchFromBoxCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 5), 0, 0));

					//---- allowFlanksIntoMarkTimesCheckBox ----
					allowFlanksIntoMarkTimesCheckBox.setText("Allow flanks into mark times");
					allowFlanksIntoMarkTimesCheckBox.setToolTipText("When enabled, allows mark times to follow flanks.");
					panel9.add(allowFlanksIntoMarkTimesCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 5), 0, 0));
				}
				panel4.add(panel9, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//======== panel1 ========
				{
					panel1.setBorder(new TitledBorder("Step sizes"));
					panel1.setLayout(new GridBagLayout());
					((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {95, 0, 132, 0, 0};
					((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
					((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 0.0, 1.0E-4};
					((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

					//---- checkBox1 ----
					checkBox1.setText("8:5");
					checkBox1.setSelected(true);
					panel1.add(checkBox1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

					//======== scrollPane1 ========
					{

						//---- list1 ----
						list1.setModel(new AbstractListModel<String>() {
							String[] values = {
								"2",
								"4",
								"6",
								"8",
								"10",
								"12",
								"14",
								"16",
								"18",
								"20",
								"22",
								"24",
								"26",
								"28",
								"30",
								"32"
							};
							@Override
							public int getSize() { return values.length; }
							@Override
							public String getElementAt(int i) { return values[i]; }
						});
						scrollPane1.setViewportView(list1);
					}
					panel1.add(scrollPane1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

					//---- checkBox3 ----
					checkBox3.setText("16:5");
					panel1.add(checkBox3, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

					//======== scrollPane3 ========
					{

						//---- list3 ----
						list3.setModel(new AbstractListModel<String>() {
							String[] values = {
								"2",
								"4",
								"6",
								"8",
								"10",
								"12",
								"14",
								"16",
								"18",
								"20",
								"22",
								"24",
								"26",
								"28",
								"30",
								"32"
							};
							@Override
							public int getSize() { return values.length; }
							@Override
							public String getElementAt(int i) { return values[i]; }
						});
						scrollPane3.setViewportView(list3);
					}
					panel1.add(scrollPane3, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

					//---- checkBox2 ----
					checkBox2.setText("6:5");
					panel1.add(checkBox2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

					//======== scrollPane2 ========
					{

						//---- list2 ----
						list2.setModel(new AbstractListModel<String>() {
							String[] values = {
								"2",
								"4",
								"6",
								"8",
								"10",
								"12",
								"14",
								"16",
								"18",
								"20",
								"22",
								"24",
								"26",
								"28",
								"30",
								"32"
							};
							@Override
							public int getSize() { return values.length; }
							@Override
							public String getElementAt(int i) { return values[i]; }
						});
						scrollPane2.setViewportView(list2);
					}
					panel1.add(scrollPane2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

					//---- checkBox4 ----
					checkBox4.setText("12:");
					panel1.add(checkBox4, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

					//======== scrollPane4 ========
					{

						//---- list4 ----
						list4.setModel(new AbstractListModel<String>() {
							String[] values = {
								"2",
								"4",
								"6",
								"8",
								"10",
								"12",
								"14",
								"16",
								"18",
								"20",
								"22",
								"24",
								"26",
								"28",
								"30",
								"32"
							};
							@Override
							public int getSize() { return values.length; }
							@Override
							public String getElementAt(int i) { return values[i]; }
						});
						scrollPane4.setViewportView(list4);
					}
					panel1.add(scrollPane4, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
				}
				panel4.add(panel1, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			panel2.add(panel4, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		}
		contentPane.add(panel2, BorderLayout.NORTH);
		pack();
		setLocationRelativeTo(getOwner());

		//---- presetsButtonGroup ----
		var presetsButtonGroup = new ButtonGroup();
		presetsButtonGroup.add(beginnerRadioButton);
		presetsButtonGroup.add(easyRadioButton);
		presetsButtonGroup.add(mediumRadioButton);
		presetsButtonGroup.add(hardRadioButton);
		presetsButtonGroup.add(expertRadioButton);
		presetsButtonGroup.add(customRadioButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
	
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel2;
	private JPanel panel7;
	private JCheckBox assumeInfiniteFieldCheckBox;
	private JPanel startingPositionPanel;
	private JLabel label3;
	private JSpinner stepsFromSidelineSpinner;
	private JLabel label5;
	private JLabel label4;
	private JComboBox<String> directionComboBox;
	private JPanel panel10;
	private JCheckBox flankToOriginalDirectionAfterBoxCheckBox;
	private JPanel panel3;
	private JRadioButton beginnerRadioButton;
	private JRadioButton easyRadioButton;
	private JRadioButton mediumRadioButton;
	private JRadioButton hardRadioButton;
	private JRadioButton expertRadioButton;
	private JRadioButton customRadioButton;
	private JPanel panel4;
	private JPanel panel5;
	private JCheckBox marchCheckBox;
	private JCheckBox markTimeCheckBox;
	private JCheckBox flankCheckBox;
	private JCheckBox boxCheckBox;
	private JPanel panel6;
	private JLabel label1;
	private JSpinner minCountFundamentalsSpinner;
	private JLabel label2;
	private JSpinner maxCountFundamentalsSpinner;
	private JPanel panel9;
	private JCheckBox allowBackwardsFlanksCheckBox;
	private JCheckBox allowBackwardsMarchFromBoxCheckBox;
	private JCheckBox allowFlanksIntoMarkTimesCheckBox;
	private JPanel panel1;
	private JCheckBox checkBox1;
	private JScrollPane scrollPane1;
	private JList<String> list1;
	private JCheckBox checkBox3;
	private JScrollPane scrollPane3;
	private JList<String> list3;
	private JCheckBox checkBox2;
	private JScrollPane scrollPane2;
	private JList<String> list2;
	private JCheckBox checkBox4;
	private JScrollPane scrollPane4;
	private JList<String> list4;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
