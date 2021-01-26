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

import com.formdev.flatlaf.FlatDarculaLaf;
import com.itextpdf.text.Font;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.ini4j.Ini;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.Box;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Moving.March;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Stationary.MarkTime;
import org.wysko.drillgen.MarchingParameters.Fundamentals.Transition.Flank;
import org.wysko.drillgen.MarchingParameters.StepSize;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * GUI to generate drills.
 *
 * @author Jacob Wysko
 */
public class Generate extends JFrame {
	static Generate generate;
	
	public static void main(String[] args) {
		generate = new Generate();
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
		FlatDarculaLaf.install();
		initComponents();
		setEnabledContainer(startingPositionPanel, false);
		selectValuesInStepSizeList(Generator2.GeneratorSettings.BEGINNER, StepSize.EIGHT_TO_FIVE, eightToFiveList);
		beginnerRadioButton.setSelected(true);
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
		/* Easy fails */
		
		/* At least one fundamental checkbox */
		if (!marchCheckBox.isSelected()
				&& !markTimeCheckBox.isSelected()
				&& !flankCheckBox.isSelected()
				&& !boxCheckBox.isSelected()) {
			JOptionPane.showMessageDialog(this, "At least one fundamental must be selected."
					, "Generation failed", JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon")
			);
			return;
		}
		
		/* At least one step size */
		if (!eightToFiveCheckBox.isSelected()
				&& !sixToFiveCheckBox.isSelected()
				&& !sixteenToFiveCheckBox.isSelected()
				&& !twelveToFiveCheckBox.isSelected()) {
			JOptionPane.showMessageDialog(this, "At least one step size must be selected."
					, "Generation failed", JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon")
			);
			return;
		}
		
		/* At least one length for each available step size */
		if ((eightToFiveCheckBox.isSelected() && eightToFiveList.getSelectedIndices().length == 0)
				|| (sixToFiveCheckBox.isSelected() && sixToFiveList.getSelectedIndices().length == 0)
				|| (sixteenToFiveCheckBox.isSelected() && sixteenToFiveList.getSelectedIndices().length == 0)
				|| (twelveToFiveCheckBox.isSelected() && twelveToFiveList.getSelectedIndices().length == 0)) {
			JOptionPane.showMessageDialog(this, "At least one length must be selected for each enabled step size."
					, "Generation failed", JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon")
			);
			return;
		}
		
		
		Generator2 generator2 = new Generator2();
		Generator2.GeneratorSettings settings = new Generator2.GeneratorSettings();
		for (Component component : fundamentalsPanel.getComponents()) {
			JCheckBox checkBox = (JCheckBox) component;
			switch (checkBox.getName()) {
				case "March":
					if (checkBox.isSelected())
						settings.validFundamentals.add(March.class);
					break;
				case "MarkTime":
					if (checkBox.isSelected())
						settings.validFundamentals.add(MarkTime.class);
					break;
				case "Flank":
					if (checkBox.isSelected())
						settings.validFundamentals.add(Flank.class);
					break;
				case "Box":
					if (checkBox.isSelected())
						settings.validFundamentals.add(Box.class);
					break;
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
		
		List<Drill> drills = new ArrayList<>();
		boolean notEnoughUnique = false;
		for (int i = 0; i < (Integer) numberOfDrillsSpinner.getValue(); i++) {
			Drill e1;
			int k = 0;
			do {
				k++;
				e1 = generator2.generateDrill(settings);
			} while (drills.contains(e1) && uniqueDrillsCheckBox.isSelected() && k < 100);
			if (k != 100)
				drills.add(e1);
			else
				notEnoughUnique = true;
		}
		
		if (notEnoughUnique) {
			JOptionPane.showMessageDialog(this,
					"There are not enough unique drills to fulfill the generation request."
					, "Generation failed", JOptionPane.WARNING_MESSAGE, UIManager.getIcon("OptionPane.warningIcon"));
		}
		
		List<Drill> nonNullDrills = drills.stream().filter(Objects::nonNull).collect(Collectors.toList());
		if (nonNullDrills.isEmpty()) {
			JOptionPane.showMessageDialog(this, "The generator couldn't generate any drills.",
					"Generation failed", JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"));
		} else if (drills.contains(null)) {
			JOptionPane.showMessageDialog(this, "The generator reached a state where the drill could not be completed," +
							" one or more times. The actual number of results may not equal the specified number.",
					"Generation failed", JOptionPane.WARNING_MESSAGE, UIManager.getIcon("OptionPane.warningIcon"));
		}
		DefaultListModel<String> model = new DefaultListModel<>();
		List<String> drillStrings =
				nonNullDrills.stream().map(drill -> drill.asString(displayRockAndRollCheckBox.isSelected(),
						showOnlyEightToFiveCheckBox.isSelected())).collect(Collectors.toList());
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
		
		/* Set it twice to bypass correction */
		maxCountFundamentalsSpinner.setValue(settings.maxCountFundamentals);
		minCountFundamentalsSpinner.setValue(settings.minCountFundamentals);
		maxCountFundamentalsSpinner.setValue(settings.maxCountFundamentals);
		minCountFundamentalsSpinner.setValue(settings.minCountFundamentals);
		
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
	
	private void selectValuesInStepSizeList(Generator2.GeneratorSettings settings, StepSize stepSize,
	                                        JList<String> list) {
		if (settings.stepSizeLengths.containsKey(stepSize)) {
			List<Integer> indicesToSelect = new ArrayList<>();
			for (int i = 0; i < list.getModel().getSize(); i++) {
				if (settings.stepSizeLengths.get(stepSize).contains(
						Integer.valueOf(list.getModel().getElementAt(i))
				)) {
					indicesToSelect.add(i);
				}
			}
			list.setSelectedIndices(indicesToSelect.stream().mapToInt(i -> i).toArray());
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
			// Reset after "custom"
			beginnerRadioButton.setSelected(true);
		}
		if (easyRadioButton.isSelected()) {
			setAllBasedOnDiff(Generator2.GeneratorSettings.EASY);
			easyRadioButton.setSelected(true);
		}
		if (mediumRadioButton.isSelected()) {
			setAllBasedOnDiff(Generator2.GeneratorSettings.MEDIUM);
			mediumRadioButton.setSelected(true);
		}
		if (hardRadioButton.isSelected()) {
			setAllBasedOnDiff(Generator2.GeneratorSettings.HARD);
			hardRadioButton.setSelected(true);
		}
		if (expertRadioButton.isSelected()) {
			setAllBasedOnDiff(Generator2.GeneratorSettings.EXPERT);
			expertRadioButton.setSelected(true);
		}
	}
	
	private void aChangeWasMade(ActionEvent e) {
		setCustomRadioEnabled();
	}
	
	private void setCustomRadioEnabled() {
		customRadioButton.setSelected(true);
	}
	
	private void listChanged(ListSelectionEvent e) {
		setCustomRadioEnabled();
	}
	
	private void aChangeWasMade(ChangeEvent e) {
		setCustomRadioEnabled();
	}
	
	private void copyToClipboardButtonActionPerformed(ActionEvent e) {
		putDrillsOnClipboard();
	}
	
	private void putDrillsOnClipboard() {
		StringSelection stringSelection = new StringSelection(getDrillsAsString());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}
	
	private String getDrillsAsString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < generatedDrillsList.getModel().getSize(); i++) {
			builder.append(generatedDrillsList.getModel().getElementAt(i)).append("\n");
		}
		return builder.toString();
	}
	
	private void saveToFileButtonActionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser() {
			@Override
			public void approveSelection() {
				File f = getSelectedFile();
				if (f.exists() && getDialogType() == SAVE_DIALOG) {
					int result = JOptionPane.showConfirmDialog(this, "The selected file already exists, overwrite?",
							"Existing file",
							JOptionPane.YES_NO_CANCEL_OPTION);
					switch (result) {
						case JOptionPane.YES_OPTION:
							super.approveSelection();
							return;
						case JOptionPane.NO_OPTION:
						case JOptionPane.CLOSED_OPTION:
							return;
						case JOptionPane.CANCEL_OPTION:
							cancelSelection();
							return;
					}
				}
				super.approveSelection();
			}
		};
		chooser.setSize(this.getSize());
		chooser.setDialogTitle("Save as...");
		FileFilter txtFileFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".txt") || f.isDirectory();
			}
			
			@Override
			public String getDescription() {
				return "Text file (*.txt)";
			}
		};
		chooser.addChoosableFileFilter(txtFileFilter);
		chooser.setFileFilter(txtFileFilter);
		int userSelection = chooser.showSaveDialog(new JFrame());
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = chooser.getSelectedFile();
			String name = fileToSave.getAbsolutePath();
			if (!name.toLowerCase().endsWith(".txt")) {
				fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
			}
			String drillsAsString = getDrillsAsString();
			try {
				Files.write(fileToSave.toPath(), drillsAsString.getBytes(StandardCharsets.UTF_8));
				if (drillsAsString.isEmpty()) {
					JOptionPane.showMessageDialog(this, "The file was written empty since there were no drills.",
							"Empty file", JOptionPane.WARNING_MESSAGE, UIManager.getIcon(
									"OptionPane.warningIcon"));
				}
			} catch (Exception exception) {
				Utils.showExceptionWithFrame(exception);
			}
			
		}
	}
	
	private void printMenuItemActionPerformed(ActionEvent e) {
		try {
			String drillsAsString = getDrillsAsString();
			if (drillsAsString.isEmpty()) {
				int result = JOptionPane.showConfirmDialog(this, "There are currently no generated drills. Do you want to print anyways?"
						, "No drills", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION)
					return;
			}
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			Document document = new Document();
			String pdfPath = System.getProperty("user.home") + "\\AppData\\Local" +
					"\\Temp\\drills.pdf";
			PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
			document.open();
			Paragraph header = new Paragraph();
			header.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20f, BaseColor.BLACK));
			header.setSpacingAfter(20f);
			header.add("Marching Band Drills");
			header.setAlignment(Element.ALIGN_CENTER);
			document.add(header);
			Paragraph content = new Paragraph();
			BaseFont bf = BaseFont.createFont(
					BaseFont.COURIER, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
			content.setFont(new Font(bf, 12));
			content.add(drillsAsString);
			document.add(content);
			document.close();
			
			PrinterJob pj = PrinterJob.getPrinterJob();
			pj.setJobName("Marching Band Drills");
			PDDocument pdf = PDDocument.load(new File(pdfPath));
			
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			if (pj.printDialog()) {
				try {
					pj.setPageable(new PDFPageable(pdf));
					pj.print();
				} catch (PrinterException exc) {
					final ExceptionViewer message = new ExceptionViewer();
					message.setExceptionTextArea(exc.getMessage());
					JOptionPane.showMessageDialog(this, message, "Print error", JOptionPane.ERROR_MESSAGE, UIManager.getIcon(
							"OptionPane.errorIcon"));
					exc.printStackTrace();
				}
			}
			pdf.close();
		} catch (Exception ex) {
			Utils.showExceptionWithFrame(ex);
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	private void copyMenuItemActionPerformed(ActionEvent e) {
		putDrillsOnClipboard();
	}
	
	private void viewBookMenuItemActionPerformed(ActionEvent e) {
		Utils.displayWebpage("https://wyskoj.github.io/haslettvmbdocs");
	}
	
	final FileFilter dginiFileFilter = new FileFilter() {
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) return true;
			else
				return f.getName().toLowerCase().endsWith(".dgini");
		}
		
		@Override
		public String getDescription() {
			return "Preset files (*.dgini)";
		}
	};
	
	private void loadPresetMenuItemActionPerformed(ActionEvent e) {
		try {
			JFileChooser chooser = new JFileChooser();
			
			chooser.addChoosableFileFilter(dginiFileFilter);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setMultiSelectionEnabled(false);
			int chose = chooser.showDialog(this, "Open");
			if (chose == JFileChooser.APPROVE_OPTION) {
				Ini ini = new Ini(chooser.getSelectedFile());
				
				marchCheckBox.setSelected(Boolean.parseBoolean(ini.get("fundamentals", "march")));
				markTimeCheckBox.setSelected(Boolean.parseBoolean(ini.get("fundamentals", "marktime")));
				flankCheckBox.setSelected(Boolean.parseBoolean(ini.get("fundamentals", "flank")));
				boxCheckBox.setSelected(Boolean.parseBoolean(ini.get("fundamentals", "box")));
				/* Twice because reasons */
				minCountFundamentalsSpinner.setValue(Integer.parseInt(ini.get("length", "min")));
				maxCountFundamentalsSpinner.setValue(Integer.parseInt(ini.get("length", "max")));
				minCountFundamentalsSpinner.setValue(Integer.parseInt(ini.get("length", "min")));
				maxCountFundamentalsSpinner.setValue(Integer.parseInt(ini.get("length", "max")));
				
				allowBackwardsFlanksCheckBox.setSelected(Boolean.parseBoolean(ini.get("advanced", "backwardsflanks")));
				allowBackwardsMarchFromBoxCheckBox.setSelected(Boolean.parseBoolean(ini.get("advanced", "backwardsmarchafterbox")));
				allowFlanksIntoMarkTimesCheckBox.setSelected(Boolean.parseBoolean(ini.get("advanced", "flanksintomarktimes")));
				eightToFiveCheckBox.setSelected(Boolean.parseBoolean(ini.get("stepsizes", "eighttofive")));
				sixteenToFiveCheckBox.setSelected(Boolean.parseBoolean(ini.get("stepsizes", "sixteentofive")));
				sixToFiveCheckBox.setSelected(Boolean.parseBoolean(ini.get("stepsizes", "sixtofive")));
				twelveToFiveCheckBox.setSelected(Boolean.parseBoolean(ini.get("stepsizes", "twelvetofive")));
				
				setListFromIni(ini, "eighttofive", eightToFiveList);
				setListFromIni(ini, "sixteentofive", sixteenToFiveList);
				setListFromIni(ini, "sixtofive", sixToFiveList);
				setListFromIni(ini, "twelvetofive", twelveToFiveList);
				setCustomRadioEnabled();
			}
		} catch (IOException ioException) {
			Utils.showExceptionWithFrame(ioException);
			ioException.printStackTrace();
		}
		
	}
	
	private void setListFromIni(Ini ini, String stepSize, JList<String> list) {
		String s = ini.get("lengths", stepSize);
		List<Integer> lengths = Arrays.stream(s.substring(1, s.length() - 1).split("\\D+"))
				.mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
		List<Integer> indices = IntStream.range(0, list.getModel().getSize())
				.filter(i -> lengths.contains(Integer.parseInt(list.getModel().getElementAt(i))))
				.boxed().collect(Collectors.toList());
		list.setSelectedIndices(indices.stream().mapToInt(i -> i).toArray());
	}
	
	private void savePresetMenuItemActionPerformed(ActionEvent e) {
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Save as...");
			chooser.addChoosableFileFilter(dginiFileFilter);
			chooser.setFileFilter(dginiFileFilter);
			Action details = chooser.getActionMap().get("viewTypeDetails");
			details.actionPerformed(null);
			int userSelection = chooser.showSaveDialog(new JFrame());
			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File fileToSave = chooser.getSelectedFile();
				String name = fileToSave.getAbsolutePath();
				if (!name.toLowerCase().endsWith(".dgini")) {
					fileToSave = new File(fileToSave.getAbsolutePath() + ".dgini");
				}
				fileToSave.createNewFile();
				Ini ini = new Ini(fileToSave);
				ini.put("fundamentals", "march", marchCheckBox.isSelected());
				ini.put("fundamentals", "marktime", markTimeCheckBox.isSelected());
				ini.put("fundamentals", "flank", flankCheckBox.isSelected());
				ini.put("fundamentals", "box", boxCheckBox.isSelected());
				ini.put("length", "min", minCountFundamentalsSpinner.getValue());
				ini.put("length", "max", maxCountFundamentalsSpinner.getValue());
				ini.put("advanced", "backwardsflanks", allowBackwardsFlanksCheckBox.isSelected());
				ini.put("advanced", "backwardsmarchafterbox", allowBackwardsMarchFromBoxCheckBox.isSelected());
				ini.put("advanced", "flanksintomarktimes", allowFlanksIntoMarkTimesCheckBox.isSelected());
				ini.put("stepsizes", "eighttofive", eightToFiveCheckBox.isSelected());
				ini.put("stepsizes", "sixteentofive", sixteenToFiveCheckBox.isSelected());
				ini.put("stepsizes", "sixtofive", sixToFiveCheckBox.isSelected());
				ini.put("stepsizes", "twelvetofive", twelveToFiveCheckBox.isSelected());
				List<Integer> eightToFives = getSelectedLengths(eightToFiveList);
				List<Integer> sixToFives = getSelectedLengths(sixToFiveList);
				List<Integer> sixteenToFives = getSelectedLengths(sixteenToFiveList);
				List<Integer> twelveToFives = getSelectedLengths(twelveToFiveList);
				ini.put("lengths", "eighttofive", eightToFives.toString());
				ini.put("lengths", "sixteentofive", sixteenToFives.toString());
				ini.put("lengths", "sixtofive", sixToFives.toString());
				ini.put("lengths", "twelvetofive", twelveToFives.toString());
				ini.store();
			}
			
		} catch (IOException ioException) {
			Utils.showExceptionWithFrame(ioException);
			ioException.printStackTrace();
		}
	}
	
	private List<Integer> getSelectedLengths(JList<String> list) {
		List<Integer> lengths = new ArrayList<>();
		for (String s : list.getSelectedValuesList()) {
			lengths.add(Integer.valueOf(s));
		}
		return lengths;
	}
	
	private void aboutMenuItemActionPerformed(ActionEvent e) {
		About about = new About();
		about.setVisible(true);
	}
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		loadPresetMenuItem = new MenuItemResizedIcon();
		savePresetMenuItem = new MenuItemResizedIcon();
		saveDrillsMenuItem = new MenuItemResizedIcon();
		printMenuItem = new MenuItemResizedIcon();
		exitMenuItem = new MenuItemResizedIcon();
		menu2 = new JMenu();
		copyMenuItem = new MenuItemResizedIcon();
		menu3 = new JMenu();
		viewBookMenuItem = new MenuItemResizedIcon();
		helpMenuItem = new MenuItemResizedIcon();
		aboutMenuItem = new MenuItemResizedIcon();
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
		label7 = new JLabel();
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
		displayRockAndRollCheckBox = new JCheckBox();
		showOnlyEightToFiveCheckBox = new JCheckBox();
		panel8 = new JPanel();
		label6 = new JLabel();
		numberOfDrillsSpinner = new JSpinner();
		scrollPane5 = new JScrollPane();
		generatedDrillsList = new JList();
		uniqueDrillsCheckBox = new JCheckBox();
		generateButton = new JButton();
		separator1 = new JSeparator();
		copyToClipboardButton = new JButton();

		//======== this ========
		setTitle("DrillGen");
		setIconImage(new ImageIcon(getClass().getResource("/logo.png")).getImage());
		var contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== menuBar1 ========
		{

			//======== menu1 ========
			{
				menu1.setText("File");

				//---- loadPresetMenuItem ----
				loadPresetMenuItem.setText("Load preset....");
				loadPresetMenuItem.setIcon(new ImageIcon(getClass().getResource("/load.png")));
				loadPresetMenuItem.addActionListener(e -> loadPresetMenuItemActionPerformed(e));
				menu1.add(loadPresetMenuItem);

				//---- savePresetMenuItem ----
				savePresetMenuItem.setText("Save preset...");
				savePresetMenuItem.setIcon(new ImageIcon(getClass().getResource("/save.png")));
				savePresetMenuItem.addActionListener(e -> savePresetMenuItemActionPerformed(e));
				menu1.add(savePresetMenuItem);
				menu1.addSeparator();

				//---- saveDrillsMenuItem ----
				saveDrillsMenuItem.setText("Save drills...");
				saveDrillsMenuItem.setIcon(new ImageIcon(getClass().getResource("/save.png")));
				saveDrillsMenuItem.addActionListener(e -> saveToFileButtonActionPerformed(e));
				menu1.add(saveDrillsMenuItem);

				//---- printMenuItem ----
				printMenuItem.setText("Print drills...");
				printMenuItem.setIcon(new ImageIcon(getClass().getResource("/print.png")));
				printMenuItem.addActionListener(e -> printMenuItemActionPerformed(e));
				menu1.add(printMenuItem);
				menu1.addSeparator();

				//---- exitMenuItem ----
				exitMenuItem.setText("Exit");
				exitMenuItem.setIcon(new ImageIcon(getClass().getResource("/exit.png")));
				menu1.add(exitMenuItem);
			}
			menuBar1.add(menu1);

			//======== menu2 ========
			{
				menu2.setText("Edit");

				//---- copyMenuItem ----
				copyMenuItem.setText("Copy drills");
				copyMenuItem.setIcon(new ImageIcon(getClass().getResource("/copy.png")));
				copyMenuItem.addActionListener(e -> copyMenuItemActionPerformed(e));
				menu2.add(copyMenuItem);
			}
			menuBar1.add(menu2);

			//======== menu3 ========
			{
				menu3.setText("Help");

				//---- viewBookMenuItem ----
				viewBookMenuItem.setText("View book");
				viewBookMenuItem.setIcon(new ImageIcon(getClass().getResource("/book.png")));
				viewBookMenuItem.addActionListener(e -> viewBookMenuItemActionPerformed(e));
				menu3.add(viewBookMenuItem);

				//---- helpMenuItem ----
				helpMenuItem.setText("Help");
				helpMenuItem.setIcon(new ImageIcon(getClass().getResource("/help.png")));
				menu3.add(helpMenuItem);

				//---- aboutMenuItem ----
				aboutMenuItem.setText("About");
				aboutMenuItem.setIcon(new ImageIcon(getClass().getResource("/about.png")));
				aboutMenuItem.addActionListener(e -> aboutMenuItemActionPerformed(e));
				menu3.add(aboutMenuItem);
			}
			menuBar1.add(menu3);
		}
		setJMenuBar(menuBar1);

		//======== panel2 ========
		{
			panel2.setLayout(new GridBagLayout());
			((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
			((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {272, 288, 0};
			((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
			((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

			//======== panel4 ========
			{
				panel4.setBorder(new TitledBorder("Customization"));
				panel4.setLayout(new GridBagLayout());
				((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {117, 0, 0};
				((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0, 238, 0};
				((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
				((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

				//======== fundamentalsPanel ========
				{
					fundamentalsPanel.setBorder(new TitledBorder("Fundamentals"));
					fundamentalsPanel.setLayout(new GridBagLayout());
					((GridBagLayout)fundamentalsPanel.getLayout()).columnWidths = new int[] {0, 0};
					((GridBagLayout)fundamentalsPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
					((GridBagLayout)fundamentalsPanel.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
					((GridBagLayout)fundamentalsPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

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
					((GridBagLayout)panel6.getLayout()).columnWidths = new int[] {0, 68, 0};
					((GridBagLayout)panel6.getLayout()).rowHeights = new int[] {0, 0, 0};
					((GridBagLayout)panel6.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
					((GridBagLayout)panel6.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

					//---- label1 ----
					label1.setText("Minimum:");
					panel6.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 5, 5), 0, 0));

					//---- minCountFundamentalsSpinner ----
					minCountFundamentalsSpinner.setModel(new SpinnerNumberModel(1, 1, 10, 1));
					minCountFundamentalsSpinner.addChangeListener(e -> {
			minCountFundamentalsSpinnerStateChanged(e);
			aChangeWasMade(e);
		});
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
					maxCountFundamentalsSpinner.addChangeListener(e -> {
			maxCountFundamentalsSpinnerStateChanged(e);
			aChangeWasMade(e);
		});
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
					((GridBagLayout)stepSizesPanel.getLayout()).columnWidths = new int[] {95, 0, 132, 0, 0};
					((GridBagLayout)stepSizesPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
					((GridBagLayout)stepSizesPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 0.0, 1.0E-4};
					((GridBagLayout)stepSizesPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0, 1.0E-4};

					//---- label7 ----
					label7.setText("<html>Hold <i>Ctrl</i> while clicking to select multiple<br>lengths in the lists. Hold <i>Shift</i> to select ranges.");
					stepSizesPanel.add(label7, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));

					//---- eightToFiveCheckBox ----
					eightToFiveCheckBox.setText("8:5");
					eightToFiveCheckBox.setSelected(true);
					eightToFiveCheckBox.addChangeListener(e -> stepSizeCheckBoxStateChanged(e));
					eightToFiveCheckBox.addActionListener(e -> aChangeWasMade(e));
					stepSizesPanel.add(eightToFiveCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
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
							public int getSize() { return values.length; }
							@Override
							public String getElementAt(int i) { return values[i]; }
						});
						eightToFiveList.addListSelectionListener(e -> listChanged(e));
						scrollPane1.setViewportView(eightToFiveList);
					}
					stepSizesPanel.add(scrollPane1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

					//---- sixteenToFiveCheckBox ----
					sixteenToFiveCheckBox.setText("16:5");
					sixteenToFiveCheckBox.addChangeListener(e -> stepSizeCheckBoxStateChanged(e));
					sixteenToFiveCheckBox.addActionListener(e -> aChangeWasMade(e));
					stepSizesPanel.add(sixteenToFiveCheckBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
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
							public int getSize() { return values.length; }
							@Override
							public String getElementAt(int i) { return values[i]; }
						});
						sixteenToFiveList.setEnabled(false);
						sixteenToFiveList.addListSelectionListener(e -> listChanged(e));
						scrollPane3.setViewportView(sixteenToFiveList);
					}
					stepSizesPanel.add(scrollPane3, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

					//---- sixToFiveCheckBox ----
					sixToFiveCheckBox.setText("6:5");
					sixToFiveCheckBox.addChangeListener(e -> stepSizeCheckBoxStateChanged(e));
					sixToFiveCheckBox.addActionListener(e -> aChangeWasMade(e));
					stepSizesPanel.add(sixToFiveCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
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
							public int getSize() { return values.length; }
							@Override
							public String getElementAt(int i) { return values[i]; }
						});
						sixToFiveList.setEnabled(false);
						sixToFiveList.addListSelectionListener(e -> listChanged(e));
						scrollPane2.setViewportView(sixToFiveList);
					}
					stepSizesPanel.add(scrollPane2, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

					//---- twelveToFiveCheckBox ----
					twelveToFiveCheckBox.setText("12:5");
					twelveToFiveCheckBox.addChangeListener(e -> stepSizeCheckBoxStateChanged(e));
					twelveToFiveCheckBox.addActionListener(e -> aChangeWasMade(e));
					stepSizesPanel.add(twelveToFiveCheckBox, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
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
							public int getSize() { return values.length; }
							@Override
							public String getElementAt(int i) { return values[i]; }
						});
						twelveToFiveList.setEnabled(false);
						twelveToFiveList.addListSelectionListener(e -> listChanged(e));
						scrollPane4.setViewportView(twelveToFiveList);
					}
					stepSizesPanel.add(scrollPane4, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
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
				((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {95, 0};
				((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
				((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
				((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

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
				((GridBagLayout)panel7.getLayout()).columnWidths = new int[] {0, 0};
				((GridBagLayout)panel7.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
				((GridBagLayout)panel7.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
				((GridBagLayout)panel7.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

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
					((GridBagLayout)startingPositionPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
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
					new Insets(0, 0, 5, 0), 0, 0));

				//---- displayRockAndRollCheckBox ----
				displayRockAndRollCheckBox.setText("Display implied rock & rolls (\"RR\")");
				panel7.add(displayRockAndRollCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- showOnlyEightToFiveCheckBox ----
				showOnlyEightToFiveCheckBox.setText("Hide \"@8:5\" for drills with only 8:5");
				panel7.add(showOnlyEightToFiveCheckBox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
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
				((GridBagLayout)panel8.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
				((GridBagLayout)panel8.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
				((GridBagLayout)panel8.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
				((GridBagLayout)panel8.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};

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
				panel8.add(scrollPane5, new GridBagConstraints(2, 0, 1, 7, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));

				//---- uniqueDrillsCheckBox ----
				uniqueDrillsCheckBox.setText("Unique drills");
				panel8.add(uniqueDrillsCheckBox, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- generateButton ----
				generateButton.setText("Generate");
				generateButton.addActionListener(e -> generateButtonActionPerformed(e));
				panel8.add(generateButton, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				panel8.add(separator1, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- copyToClipboardButton ----
				copyToClipboardButton.setText("Copy to Clipboard");
				copyToClipboardButton.addActionListener(e -> copyToClipboardButtonActionPerformed(e));
				panel8.add(copyToClipboardButton, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
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
	private JMenuBar menuBar1;
	private JMenu menu1;
	private MenuItemResizedIcon loadPresetMenuItem;
	private MenuItemResizedIcon savePresetMenuItem;
	private MenuItemResizedIcon saveDrillsMenuItem;
	private MenuItemResizedIcon printMenuItem;
	private MenuItemResizedIcon exitMenuItem;
	private JMenu menu2;
	private MenuItemResizedIcon copyMenuItem;
	private JMenu menu3;
	private MenuItemResizedIcon viewBookMenuItem;
	private MenuItemResizedIcon helpMenuItem;
	private MenuItemResizedIcon aboutMenuItem;
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
	private JLabel label7;
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
	private JCheckBox displayRockAndRollCheckBox;
	private JCheckBox showOnlyEightToFiveCheckBox;
	private JPanel panel8;
	private JLabel label6;
	private JSpinner numberOfDrillsSpinner;
	private JScrollPane scrollPane5;
	private JList generatedDrillsList;
	private JCheckBox uniqueDrillsCheckBox;
	private JButton generateButton;
	private JSeparator separator1;
	private JButton copyToClipboardButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
