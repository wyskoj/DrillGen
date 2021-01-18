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

import org.wysko.drillgen.MarchingParameters.Fundamentals.Fundamental;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.Box;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.March;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Stationary.MarkTime;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Transition.Flank;
import org.wysko.drillgen.MarchingParameters.StepSize;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

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
		while (!queue.isEmpty()) {
			Component head = queue.poll();
			head.setEnabled(enabled);
			if (head instanceof Container) {
				Container headCast = (Container) head;
				queue.addAll(Arrays.asList(headCast.getComponents()));
			}
		}
	}
	
	public Generate() {
//		FlatLightLaf.install();
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception ignored) {
		}
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
		setEnabledContainer(startingPositionPanel, !assumeInfiniteFieldCheckBox.isSelected());
	}
	
	private void generateButtonActionPerformed(ActionEvent e) {
		Generator2 generator2 = new Generator2();
		Generator2.GeneratorSettings settings = new Generator2.GeneratorSettings();
		for (Component component : fundamentalsPanel.getComponents()) {
			JCheckBox checkBox = (JCheckBox) component;
			switch (checkBox.getName()) {
				case "March" -> settings.validFundamentals.add(March.class);
				case "MarkTime" -> settings.validFundamentals.add(MarkTime.class);
				case "Flank" -> settings.validFundamentals.add(Flank.class);
				case "Box" -> settings.validFundamentals.add(Box.class);
			}
		}
		settings.setMinCountFundamentals(((Integer) minCountFundamentalsSpinner.getValue()))
				.setMaxCountFundamentals(((Integer) maxCountFundamentalsSpinner.getValue()))
				.setAllowBackwardsFlanks(allowBackwardsFlanksCheckBox.isSelected())
				.setAllowBackwardsMarchFromBox(allowBackwardsMarchFromBoxCheckBox.isSelected())
				.setAllowFlanksIntoMarkTimes(allowFlanksIntoMarkTimesCheckBox.isSelected())
				.setAssumeInfiniteField(assumeInfiniteFieldCheckBox.isSelected())
				.setStepsFromSideline(((Integer) stepsFromSidelineSpinner.getValue()))
				.setStartingDirection(CardinalDirection.valueOf(((String) Objects.requireNonNull(directionComboBox.getSelectedItem())).toUpperCase(Locale.ROOT)))
				.setFlankToOriginalDirectionAfterBox(flankToOriginalDirectionAfterBoxCheckBox.isSelected());
		
		/* Add step sizes */
		addStepSizeAndLength(settings, eightToFiveCheckBox, eightToFiveList, StepSize.EIGHT_TO_FIVE);
		addStepSizeAndLength(settings, sixteenToFiveCheckBox, sixteenToFiveList, StepSize.SIXTEEN_TO_FIVE);
		addStepSizeAndLength(settings, sixToFiveCheckBox, sixteenToFiveList, StepSize.SIX_TO_FIVE);
		addStepSizeAndLength(settings, twelveToFiveCheckBox, twelveToFiveList, StepSize.TWELVE_TO_FIVE);
		
		List<Stack<Fundamental>> drills = new ArrayList<>();
		for (int i = 0; i < (Integer) numberOfDrillsSpinner.getValue(); i++) {
			drills.add(generator2.generateDrill(settings));
		}
		DefaultListModel<String> model = new DefaultListModel<>();
		List<String> drillStrings = new ArrayList<>();
		drillStrings = drills.stream().map(Vector::toString).collect(Collectors.toList());
		model.addAll(drillStrings);
		//noinspection unchecked
		generatedDrillsList.setModel(model);
	}
	
	private void addStepSizeAndLength(Generator2.GeneratorSettings settings, JCheckBox eightToFiveCheckBox,
	                                  JList<String> eightToFiveList, StepSize eightToFive) {
		if (eightToFiveCheckBox.isSelected()) {
			ArrayList<Integer> valuesToAdd = new ArrayList<>();
			for (String s : eightToFiveList.getSelectedValuesList()) {
				valuesToAdd.add(Integer.valueOf(s));
			}
			settings.stepSizeLengths.put(eightToFive, valuesToAdd);
		}
	}
	
	private void setAllBasedOnDiff(Generator2.GeneratorSettings settings) {
		marchCheckBox.setSelected(settings.validFundamentals.contains(March.class));
		markTimeCheckBox.setSelected(settings.validFundamentals.contains(MarkTime.class));
		flankCheckBox.setSelected(settings.validFundamentals.contains(Flank.class));
		boxCheckBox.setSelected(settings.validFundamentals.contains(Box.class));
		minCountFundamentalsSpinner.setValue(settings.minCountFundamentals);
		maxCountFundamentalsSpinner.setValue(settings.maxCountFundamentals);
		allowBackwardsFlanksCheckBox.setSelected(settings.allowBackwardsFlanks);
		allowBackwardsMarchFromBoxCheckBox.setSelected(settings.allowBackwardsMarchFromBox);
		allowFlanksIntoMarkTimesCheckBox.setSelected(settings.allowFlanksIntoMarkTimes);
		eightToFiveCheckBox.setSelected(settings.stepSizeLengths.containsKey(StepSize.EIGHT_TO_FIVE));
		sixteenToFiveCheckBox.setSelected(settings.stepSizeLengths.containsKey(StepSize.SIXTEEN_TO_FIVE));
		sixToFiveCheckBox.setSelected(settings.stepSizeLengths.containsKey(StepSize.SIX_TO_FIVE));
		twelveToFiveCheckBox.setSelected(settings.stepSizeLengths.containsKey(StepSize.TWELVE_TO_FIVE));
		selectValuesInStepSizeList(settings, StepSize.EIGHT_TO_FIVE, eightToFiveList);
		selectValuesInStepSizeList(settings, StepSize.SIX_TO_FIVE, sixToFiveList);
		selectValuesInStepSizeList(settings, StepSize.SIXTEEN_TO_FIVE, sixteenToFiveList);
		selectValuesInStepSizeList(settings, StepSize.TWELVE_TO_FIVE, twelveToFiveList);
	}
	
	private void selectValuesInStepSizeList(Generator2.GeneratorSettings settings, StepSize eightToFive,
	                                        JList<String> eightToFiveList) {
		if (settings.stepSizeLengths.containsKey(eightToFive)) {
			List<Integer> indicesToSelect = new ArrayList<>();
			for (int i = 0; i < eightToFiveList.getModel().getSize(); i++) {
				if (settings.stepSizeLengths.get(eightToFive).contains(
						Integer.valueOf(eightToFiveList.getModel().getElementAt(i))
				)) {
					indicesToSelect.add(i);
				}
			}
			eightToFiveList.setSelectedIndices(indicesToSelect.stream().mapToInt(i -> i).toArray());
		}
	}
	
	
	private void stepSizeCheckBoxStateChanged(ChangeEvent e) {
		eightToFiveList.setEnabled(eightToFiveCheckBox.isSelected());
		sixteenToFiveList.setEnabled(sixteenToFiveCheckBox.isSelected());
		sixToFiveList.setEnabled(sixToFiveCheckBox.isSelected());
		twelveToFiveList.setEnabled(twelveToFiveCheckBox.isSelected());
	}
	
	private void presetButtonAction(ActionEvent e) {
		if (beginnerRadioButton.isSelected()) {
			setAllBasedOnDiff(Generator2.GeneratorSettings.BEGINNER);
		}
		if (easyRadioButton.isSelected()) {
			setAllBasedOnDiff(Generator2.GeneratorSettings.EASY);
		}
		if (mediumRadioButton.isSelected()) {
			setAllBasedOnDiff(Generator2.GeneratorSettings.MEDIUM);
		}
		if (hardRadioButton.isSelected()) {
			setAllBasedOnDiff(Generator2.GeneratorSettings.HARD);
		}
		if (expertRadioButton.isSelected()) {
			setAllBasedOnDiff(Generator2.GeneratorSettings.EXPERT);
		}
	}
	
	private void aChangeWasMade(ActionEvent e) {
		setCustomRadioEnabled();
	}
	
	private void listChanged(ListSelectionEvent e) {
		
	}
	
	private void setCustomRadioEnabled() {
		customRadioButton.setSelected(true);
	}
	
	private void listChanged(MouseEvent e) {
		if (e.getComponent().isEnabled())
			setCustomRadioEnabled();
	}
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel2 = new JPanel();
		panel4 = new JPanel();
		fundamentalsPanel = new JPanel();
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
		stepSizesPanel = new JPanel();
		eightToFiveCheckBox = new JCheckBox();
		scrollPane1 = new JScrollPane();
		eightToFiveList = new JList<>();
		sixteenToFiveCheckBox = new JCheckBox();
		scrollPane3 = new JScrollPane();
		sixteenToFiveList = new JList<>();
		sixToFiveCheckBox = new JCheckBox();
		scrollPane2 = new JScrollPane();
		sixToFiveList = new JList<>();
		twelveToFiveCheckBox = new JCheckBox();
		scrollPane4 = new JScrollPane();
		twelveToFiveList = new JList<>();
		panel3 = new JPanel();
		beginnerRadioButton = new JRadioButton();
		easyRadioButton = new JRadioButton();
		mediumRadioButton = new JRadioButton();
		hardRadioButton = new JRadioButton();
		expertRadioButton = new JRadioButton();
		customRadioButton = new JRadioButton();
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
		panel8 = new JPanel();
		label6 = new JLabel();
		numberOfDrillsSpinner = new JSpinner();
		scrollPane5 = new JScrollPane();
		generatedDrillsList = new JList();
		generateButton = new JButton();
		
		//======== this ========
		setTitle("Drill Generator");
		var contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		//======== panel2 ========
		{
			panel2.setLayout(new GridBagLayout());
			((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
			((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {272, 288, 0};
			((GridBagLayout) panel2.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
			((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};
			
			//======== panel4 ========
			{
				panel4.setBorder(new TitledBorder("Customization"));
				panel4.setLayout(new GridBagLayout());
				((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {117, 0, 0};
				((GridBagLayout) panel4.getLayout()).rowHeights = new int[] {0, 0, 238, 0};
				((GridBagLayout) panel4.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
				((GridBagLayout) panel4.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
				
				//======== fundamentalsPanel ========
				{
					fundamentalsPanel.setBorder(new TitledBorder("Fundamentals"));
					fundamentalsPanel.setLayout(new GridBagLayout());
					((GridBagLayout) fundamentalsPanel.getLayout()).columnWidths = new int[] {0, 0};
					((GridBagLayout) fundamentalsPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
					((GridBagLayout) fundamentalsPanel.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
					((GridBagLayout) fundamentalsPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
					
					//---- marchCheckBox ----
					marchCheckBox.setText("March");
					marchCheckBox.setSelected(true);
					marchCheckBox.setName("March");
					marchCheckBox.addActionListener(e -> aChangeWasMade(e));
					fundamentalsPanel.add(marchCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, 5, 0), 0, 0));
					
					//---- markTimeCheckBox ----
					markTimeCheckBox.setText("Mark time");
					markTimeCheckBox.setSelected(true);
					markTimeCheckBox.setName("MarkTime");
					markTimeCheckBox.addActionListener(e -> aChangeWasMade(e));
					fundamentalsPanel.add(markTimeCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, 5, 0), 0, 0));
					
					//---- flankCheckBox ----
					flankCheckBox.setText("Flank");
					flankCheckBox.setName("Flank");
					flankCheckBox.addActionListener(e -> aChangeWasMade(e));
					fundamentalsPanel.add(flankCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, 5, 0), 0, 0));
					
					//---- boxCheckBox ----
					boxCheckBox.setText("Box");
					boxCheckBox.setName("Box");
					boxCheckBox.addActionListener(e -> aChangeWasMade(e));
					fundamentalsPanel.add(boxCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, 0, 0), 0, 0));
				}
				panel4.add(fundamentalsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 5), 0, 0));
				
				//======== panel6 ========
				{
					panel6.setBorder(new TitledBorder("Length of drill"));
					panel6.setLayout(new GridBagLayout());
					((GridBagLayout) panel6.getLayout()).columnWidths = new int[] {0, 68, 0};
					((GridBagLayout) panel6.getLayout()).rowHeights = new int[] {0, 0, 0};
					((GridBagLayout) panel6.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
					((GridBagLayout) panel6.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};
					
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
					((GridBagLayout) panel9.getLayout()).columnWidths = new int[] {0, 0, 0};
					((GridBagLayout) panel9.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
					((GridBagLayout) panel9.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
					((GridBagLayout) panel9.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
					
					//---- allowBackwardsFlanksCheckBox ----
					allowBackwardsFlanksCheckBox.setText("Allow backwards flanks");
					allowBackwardsFlanksCheckBox.setToolTipText("<html>A backwards flank has a backwards march either before or after the flank.<br>Enabling this allows backwards flanks to appear in the drill.</html>");
					allowBackwardsFlanksCheckBox.addActionListener(e -> aChangeWasMade(e));
					panel9.add(allowBackwardsFlanksCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, 5, 5), 0, 0));
					
					//---- allowBackwardsMarchFromBoxCheckBox ----
					allowBackwardsMarchFromBoxCheckBox.setText("Allow backwards march after box");
					allowBackwardsMarchFromBoxCheckBox.setToolTipText("When enabled, allows a backwards march to follow a box.");
					allowBackwardsMarchFromBoxCheckBox.addActionListener(e -> aChangeWasMade(e));
					panel9.add(allowBackwardsMarchFromBoxCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, 5, 5), 0, 0));
					
					//---- allowFlanksIntoMarkTimesCheckBox ----
					allowFlanksIntoMarkTimesCheckBox.setText("Allow flanks into mark times");
					allowFlanksIntoMarkTimesCheckBox.setToolTipText("When enabled, allows mark times to follow flanks.");
					allowFlanksIntoMarkTimesCheckBox.addActionListener(e -> aChangeWasMade(e));
					panel9.add(allowFlanksIntoMarkTimesCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, 0, 5), 0, 0));
				}
				panel4.add(panel9, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 0), 0, 0));
				
				//======== stepSizesPanel ========
				{
					stepSizesPanel.setBorder(new TitledBorder("Step sizes"));
					stepSizesPanel.setLayout(new GridBagLayout());
					((GridBagLayout) stepSizesPanel.getLayout()).columnWidths = new int[] {95, 0, 132, 0, 0};
					((GridBagLayout) stepSizesPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
					((GridBagLayout) stepSizesPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 0.0, 1.0E-4};
					((GridBagLayout) stepSizesPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};
					
					//---- eightToFiveCheckBox ----
					eightToFiveCheckBox.setText("8:5");
					eightToFiveCheckBox.setSelected(true);
					eightToFiveCheckBox.addChangeListener(e -> stepSizeCheckBoxStateChanged(e));
					eightToFiveCheckBox.addActionListener(e -> aChangeWasMade(e));
					stepSizesPanel.add(eightToFiveCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0), 0, 0));
					
					//======== scrollPane1 ========
					{
						
						//---- eightToFiveList ----
						eightToFiveList.setModel(new AbstractListModel<String>() {
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
							public int getSize() {
								return values.length;
							}
							
							@Override
							public String getElementAt(int i) {
								return values[i];
							}
						});
						eightToFiveList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								listChanged(e);
							}
						});
						scrollPane1.setViewportView(eightToFiveList);
					}
					stepSizesPanel.add(scrollPane1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0), 0, 0));
					
					//---- sixteenToFiveCheckBox ----
					sixteenToFiveCheckBox.setText("16:5");
					sixteenToFiveCheckBox.addChangeListener(e -> stepSizeCheckBoxStateChanged(e));
					sixteenToFiveCheckBox.addActionListener(e -> aChangeWasMade(e));
					stepSizesPanel.add(sixteenToFiveCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0), 0, 0));
					
					//======== scrollPane3 ========
					{
						
						//---- sixteenToFiveList ----
						sixteenToFiveList.setModel(new AbstractListModel<String>() {
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
							public int getSize() {
								return values.length;
							}
							
							@Override
							public String getElementAt(int i) {
								return values[i];
							}
						});
						sixteenToFiveList.setEnabled(false);
						sixteenToFiveList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								listChanged(e);
							}
						});
						scrollPane3.setViewportView(sixteenToFiveList);
					}
					stepSizesPanel.add(scrollPane3, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0), 0, 0));
					
					//---- sixToFiveCheckBox ----
					sixToFiveCheckBox.setText("6:5");
					sixToFiveCheckBox.addChangeListener(e -> stepSizeCheckBoxStateChanged(e));
					sixToFiveCheckBox.addActionListener(e -> aChangeWasMade(e));
					stepSizesPanel.add(sixToFiveCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0), 0, 0));
					
					//======== scrollPane2 ========
					{
						
						//---- sixToFiveList ----
						sixToFiveList.setModel(new AbstractListModel<String>() {
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
							public int getSize() {
								return values.length;
							}
							
							@Override
							public String getElementAt(int i) {
								return values[i];
							}
						});
						sixToFiveList.setEnabled(false);
						sixToFiveList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								listChanged(e);
							}
						});
						scrollPane2.setViewportView(sixToFiveList);
					}
					stepSizesPanel.add(scrollPane2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0), 0, 0));
					
					//---- twelveToFiveCheckBox ----
					twelveToFiveCheckBox.setText("12:5");
					twelveToFiveCheckBox.addChangeListener(e -> stepSizeCheckBoxStateChanged(e));
					twelveToFiveCheckBox.addActionListener(e -> aChangeWasMade(e));
					stepSizesPanel.add(twelveToFiveCheckBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0), 0, 0));
					
					//======== scrollPane4 ========
					{
						
						//---- twelveToFiveList ----
						twelveToFiveList.setModel(new AbstractListModel<String>() {
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
							public int getSize() {
								return values.length;
							}
							
							@Override
							public String getElementAt(int i) {
								return values[i];
							}
						});
						twelveToFiveList.setEnabled(false);
						twelveToFiveList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								listChanged(e);
							}
						});
						scrollPane4.setViewportView(twelveToFiveList);
					}
					stepSizesPanel.add(scrollPane4, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0), 0, 0));
				}
				panel4.add(stepSizesPanel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
			}
			panel2.add(panel4, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));
			
			//======== panel3 ========
			{
				panel3.setBorder(new TitledBorder("Presets"));
				panel3.setLayout(new GridBagLayout());
				((GridBagLayout) panel3.getLayout()).columnWidths = new int[] {95, 0};
				((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
				((GridBagLayout) panel3.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
				((GridBagLayout) panel3.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
				
				//---- beginnerRadioButton ----
				beginnerRadioButton.setText("Beginner");
				beginnerRadioButton.setSelected(true);
				beginnerRadioButton.addActionListener(e -> presetButtonAction(e));
				panel3.add(beginnerRadioButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 0), 0, 0));
				
				//---- easyRadioButton ----
				easyRadioButton.setText("Easy");
				easyRadioButton.addActionListener(e -> presetButtonAction(e));
				panel3.add(easyRadioButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 0), 0, 0));
				
				//---- mediumRadioButton ----
				mediumRadioButton.setText("Medium");
				mediumRadioButton.addActionListener(e -> presetButtonAction(e));
				panel3.add(mediumRadioButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 0), 0, 0));
				
				//---- hardRadioButton ----
				hardRadioButton.setText("Hard");
				hardRadioButton.addActionListener(e -> presetButtonAction(e));
				panel3.add(hardRadioButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 0), 0, 0));
				
				//---- expertRadioButton ----
				expertRadioButton.setText("Expert");
				expertRadioButton.addActionListener(e -> presetButtonAction(e));
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
			
			//======== panel7 ========
			{
				panel7.setBorder(new TitledBorder("Settings"));
				panel7.setLayout(new GridBagLayout());
				((GridBagLayout) panel7.getLayout()).columnWidths = new int[] {0, 0};
				((GridBagLayout) panel7.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
				((GridBagLayout) panel7.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
				((GridBagLayout) panel7.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
				
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
					((GridBagLayout) startingPositionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
					((GridBagLayout) startingPositionPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
					((GridBagLayout) startingPositionPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
					((GridBagLayout) startingPositionPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};
					
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
							"North",
							"East",
							"South",
							"West"
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
					((GridBagLayout) panel10.getLayout()).columnWidths = new int[] {0, 0};
					((GridBagLayout) panel10.getLayout()).rowHeights = new int[] {0, 0};
					((GridBagLayout) panel10.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
					((GridBagLayout) panel10.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
					
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
			panel2.add(panel7, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));
			
			//======== panel8 ========
			{
				panel8.setBorder(new TitledBorder("Generate"));
				panel8.setLayout(new GridBagLayout());
				((GridBagLayout) panel8.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
				((GridBagLayout) panel8.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
				((GridBagLayout) panel8.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
				((GridBagLayout) panel8.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
				
				//---- label6 ----
				label6.setText("Number of drills:");
				panel8.add(label6, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 5), 0, 0));
				
				//---- numberOfDrillsSpinner ----
				numberOfDrillsSpinner.setModel(new SpinnerNumberModel(1, 1, 100, 1));
				panel8.add(numberOfDrillsSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 5), 0, 0));
				
				//======== scrollPane5 ========
				{
					scrollPane5.setViewportView(generatedDrillsList);
				}
				panel8.add(scrollPane5, new GridBagConstraints(2, 0, 1, 3, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
				
				//---- generateButton ----
				generateButton.setText("Generate");
				generateButton.addActionListener(e -> generateButtonActionPerformed(e));
				panel8.add(generateButton, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 5), 0, 0));
			}
			panel2.add(panel8, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
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
	private JPanel panel4;
	private JPanel fundamentalsPanel;
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
	private JPanel stepSizesPanel;
	private JCheckBox eightToFiveCheckBox;
	private JScrollPane scrollPane1;
	private JList<String> eightToFiveList;
	private JCheckBox sixteenToFiveCheckBox;
	private JScrollPane scrollPane3;
	private JList<String> sixteenToFiveList;
	private JCheckBox sixToFiveCheckBox;
	private JScrollPane scrollPane2;
	private JList<String> sixToFiveList;
	private JCheckBox twelveToFiveCheckBox;
	private JScrollPane scrollPane4;
	private JList<String> twelveToFiveList;
	private JPanel panel3;
	private JRadioButton beginnerRadioButton;
	private JRadioButton easyRadioButton;
	private JRadioButton mediumRadioButton;
	private JRadioButton hardRadioButton;
	private JRadioButton expertRadioButton;
	private JRadioButton customRadioButton;
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
	private JPanel panel8;
	private JLabel label6;
	private JSpinner numberOfDrillsSpinner;
	private JScrollPane scrollPane5;
	private JList generatedDrillsList;
	private JButton generateButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
