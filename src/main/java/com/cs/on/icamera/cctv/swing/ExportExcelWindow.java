package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.xssf.ExportColumns;
import com.cs.on.icamera.cctv.xssf.SheetNames;
import com.cs.on.icamera.cctv.xssf.TemplateColumns;
import com.cs.on.icamera.cctv.xssf.Workbook;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ExportExcelWindow {

	private final List<Cctv> allCctvs;
	private final List<Cctv> okCctvs;

	public ExportExcelWindow() {
		this.allCctvs = DataStore.getCctvsToVerify();
		this.okCctvs = DataStore.getCorrectCctvs();

		JFrame frame = new JFrame("Download Excel");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;

		JTextArea messageArea = getJTextArea();
		frame.add(messageArea, gbc);

		JButton downloadButton = new JButton("Download Excel");
		JButton exitButton = new JButton("Exit");
		exitButton.setEnabled(false);

		downloadButton.addActionListener(e -> downloadExcelTemplate(exitButton));
		exitButton.addActionListener(e -> System.exit(0));

		gbc.gridx = 0;
		gbc.gridy = 1;
		frame.add(downloadButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		frame.add(exitButton, gbc);

		frame.setVisible(true);
	}

	private JTextArea getJTextArea() {
		int errorCount = allCctvs.size() - okCctvs.size();
		JTextArea messageArea = new JTextArea("""
				%s encountered while checking the RTSP feeds.

				1. Please download the CCTV details template Excel,
				2. Fill necessary details like CCTV Name, Inside Room, etc. in 'CCTVs' sheet,
				3. Correct the information which are incorrect,
				4. Then re-verify the updated CCTV details template using this utility.
				""".formatted(errorCount == 0 ? "No errors" : errorCount + " errors"));
		messageArea.setEditable(false);
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);
		messageArea.setPreferredSize(new Dimension(480, 360));
		return messageArea;
	}

	private void downloadExcelTemplate(JButton exitButton) {
		try {
			File file = FileDialogUtils.saveFileDialog("iCamera-cctv-details");
			if (file != null) {
				Workbook.writeCctvsToExcel(file, true, SheetNames.TEMPLATE, false, TemplateColumns.values(), allCctvs);
				Workbook.writeCctvsToExcel(file, false, SheetNames.EXPORT, true, ExportColumns.values(), okCctvs);
				JOptionPane.showMessageDialog(null, "Template Excel downloaded successfully.");
				exitButton.setEnabled(true);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Error creating excel: " + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage()),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}