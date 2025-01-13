package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.error.VerificationException;
import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.xssf.SheetNames;
import com.cs.on.icamera.cctv.xssf.TemplateColumns;
import com.cs.on.icamera.cctv.xssf.Workbook;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MainWindow {
	private final JFrame frame;

	public MainWindow() {
		frame = new JFrame("iCAMERA CCTV Verification");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setLayout(new GridBagLayout());

		JButton discoverButton = new JButton("Discover CCTV");
		discoverButton.setToolTipText("Click to discover available CCTV devices on the network.");
		JButton verifyButton = new JButton("Verify CCTV using Excel");
		verifyButton.setToolTipText("Click to verify available CCTV devices using Excel file.");
		GridBagConstraints gbc = new GridBagConstraints();

		discoverButton.addActionListener(e -> {
			frame.dispose();
			new DiscoveryWindow();
		});

		verifyButton.addActionListener(e -> uploadExcelTemplate());

		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		frame.add(discoverButton, gbc);

		gbc.gridy = 1;
		frame.add(verifyButton, gbc);

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void uploadExcelTemplate() {
		try {
			File file = FileDialogUtils.openExcelFileDialog();
			if (file != null) {
				List<Cctv> cctvs = Workbook.readCctvsFromExcel(file, SheetNames.TEMPLATE, TemplateColumns.values());
				if (cctvs.isEmpty()) {
					throw new VerificationException("Excel file is empty.");
				}
				DataStore.setCctvsToVerify(cctvs);
				frame.dispose();
				new VerifyCctvWindow();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error reading template excel: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(MainWindow::new);
	}
}
