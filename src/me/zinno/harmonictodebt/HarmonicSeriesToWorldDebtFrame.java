package me.zinno.harmonictodebt;

import me.zinno.harmonictodebt.util.HarmonicSeries;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class HarmonicSeriesToWorldDebtFrame extends JFrame{
	
	private long billionsOfIterations;
	private double normHarmonicTot;
	private double altHarmonicTot;
	private long timeToCalcBillion;
	
	private boolean hasReachedWorldDebt = false;
	
	private File saveFile;
	
	private final CustomPanel intervalPanel;
	private final CustomPanel harmonicPanel;
	private final CustomPanel altHarmonicPanel;
	private final CustomPanel worldDebtPanel;
	
	public HarmonicSeriesToWorldDebtFrame() throws IOException {
		setInitialDefaults();
		setInitialValues();
		
		this.intervalPanel = new CustomPanel("Iterations:", Long.toString(billionsOfIterations) + " Billion");
		this.harmonicPanel = new CustomPanel("Harmonic Series:", Double.toString(normHarmonicTot));
		this.altHarmonicPanel = new CustomPanel("Alt. Harmonic Series", Double.toString(altHarmonicTot));
		this.worldDebtPanel = new CustomPanel("Est. Time Until Iterations Equals World Debt (USD)", "infinity");
		
		setPanels();
		setFinalDefaults();
	}
	
	private void setInitialDefaults() {
		setLayout(new GridLayout(1, 4));
		setBackground(new Color(255, 236, 179));
		setSize(new Dimension(900, 300));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Harmonic Series Calculator");
	}
	
	private void setInitialValues() throws IOException {
		
		try { // Makes sure the save file has a chance to load into memory while in startup files
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		this.saveFile = new File("Harmonic Series to World Debt (Save File).txt");
		
		if(!saveFile.exists() || saveFile.isDirectory())
			return;
		
		Scanner scanner = new Scanner(saveFile);
		
		billionsOfIterations = Long.parseLong(scanner.nextLine());
		normHarmonicTot = Double.parseDouble(scanner.nextLine());
		altHarmonicTot = Double.parseDouble(scanner.nextLine());
		
		scanner.close();
	}
	
	private void setPanels() {
		add(intervalPanel);
		add(harmonicPanel);
		add(altHarmonicPanel);
		add(worldDebtPanel);
	}
	
	private void setFinalDefaults() {
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				try {
					saveData();
				} catch (IOException exception) {
					System.out.println("Could not save file");
				}
			}
		});
	}
	
	public void iterateOneBillionTimes() throws IOException{
		timeToCalcBillion = System.currentTimeMillis();
		for (long c = billionsOfIterations * 1000000000; c < (billionsOfIterations + 1) * 1000000000; c++) {
			normHarmonicTot += HarmonicSeries.calcNextNormHarmVal(c);
			altHarmonicTot += HarmonicSeries.calcNextAltHarmVal(c);
		}
		
		System.out.println(billionsOfIterations + "\t" + normHarmonicTot + "\t" + altHarmonicTot);
		
		billionsOfIterations += 1;
		if(billionsOfIterations % 5 == 0)
			saveData();
		updatePanelInfo();
		if (billionsOfIterations == 71687)
			hasReachedWorldDebt = true;
	}
	
	private void saveData() throws IOException{
		PrintWriter writer = new PrintWriter(saveFile);
		
		writer.println(billionsOfIterations);
		writer.println(normHarmonicTot);
		writer.println(altHarmonicTot);
		writer.close();
	}
	
	private void updatePanelInfo() {
		this.intervalPanel.updateInfo(Long.toString(billionsOfIterations) + " Billion");
		this.harmonicPanel.updateInfo(Double.toString(normHarmonicTot));
		this.altHarmonicPanel.updateInfo(Double.toString(altHarmonicTot));
		this.worldDebtPanel.updateInfo(millisToDHMS(calcTimeUntilWorldDebt()));
		
		setPanels();
		revalidate();
	}
	
	private long calcTimeUntilWorldDebt() {
		timeToCalcBillion -= System.currentTimeMillis();
		timeToCalcBillion = Math.abs(timeToCalcBillion); // Amount of time to do 1 billion iterations (positive)
		timeToCalcBillion *= 71686 - billionsOfIterations; // time to calc billion *  billions of iterations to world debt remaining
		return timeToCalcBillion;
	}
	
	public boolean hasReachedWorldDebt() {
		return hasReachedWorldDebt;
	}
	
	private static String millisToDHMS(long value) {
		
		String dhms = String.format("%02dD %02dH %02dM %02dS",
				TimeUnit.MILLISECONDS.toDays(value),
				TimeUnit.MILLISECONDS.toHours(value) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(value)),
				TimeUnit.MILLISECONDS.toMinutes(value) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(value)),
				TimeUnit.MILLISECONDS.toSeconds(value) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(value)));
		
		return dhms;
	}
	
	private static class CustomPanel extends JPanel {
		
		private final CustomTitle customTitle;
		private CustomInfo customInfo;
		
		public CustomPanel(String name, String info) {
			setLayout(new GridLayout(2, 1));
			this.customTitle = new CustomTitle(name);
			this.customInfo = new CustomInfo(info);
			setLabels();
		}
		
		public void updateInfo(String info) {
			this.customInfo = new CustomInfo(info);
			
			setLabels();
		}
		
		private void setLabels() {
			removeAll();
			add(this.customTitle);
			add(this.customInfo);
		}
		
		private static class CustomTitle extends JLabel {
			public CustomTitle(String name) {
				setText("<html><p align='center'><font size='6' face='georgia' color='#F57C00'>" + name + "</font></p></html>");
				setBorder(new EmptyBorder(10, 10, 0, 10));
			}
		}
		
		private static class CustomInfo extends JLabel {
			public CustomInfo(String info) {
				setText("<html><p align='center'><font size='5' face='georgia' color='#FFB300'>" + info + "</font></p></html>");
				setBorder(new EmptyBorder(0, 10, 10, 10));
			}
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		HarmonicSeriesToWorldDebtFrame frame = new HarmonicSeriesToWorldDebtFrame();

		while (!frame.hasReachedWorldDebt()) {
			frame.iterateOneBillionTimes();
		}
	}
	
}
