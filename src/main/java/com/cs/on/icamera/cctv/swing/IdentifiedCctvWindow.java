package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.onvif.OnvifEnquiry;
import com.cs.on.icamera.cctv.xssf.SheetNames;
import com.cs.on.icamera.cctv.xssf.TemplateColumns;
import com.cs.on.icamera.cctv.xssf.Workbook;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class IdentifiedCctvWindow {

	private final JFrame frame;
	private final JTextField usernameField;
	private final JPasswordField passwordField;

	public IdentifiedCctvWindow() {
		frame = new JFrame("Verify CCTVs");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		JLabel discoveredCountLabel = new JLabel("Discovered CCTVs: ");
		discoveredCountLabel.setFont(discoveredCountLabel.getFont().deriveFont(Font.BOLD));
		gbc.gridx = 0;
		gbc.gridy = 0;
		frame.add(discoveredCountLabel, gbc);

		int discoveredCount = DataStore.getDiscoveredCctvCount();
		JLabel discoveredCountValue = new JLabel(String.valueOf(discoveredCount));
		discoveredCountLabel.setFont(discoveredCountValue.getFont().deriveFont(Font.BOLD));
		gbc.gridx = 1;
		gbc.gridy = 0;
		frame.add(discoveredCountValue, gbc);

		JLabel usernameLabel = new JLabel("Username:");
		gbc.gridx = 0;
		gbc.gridy = 1;
		frame.add(usernameLabel, gbc);
		usernameField = new JTextField(20);
		usernameField.setText("admin");
		gbc.gridx = 1;
		gbc.gridy = 1;
		frame.add(usernameField, gbc);

		JLabel passwordLabel = new JLabel("Password:");
		gbc.gridx = 0;
		gbc.gridy = 2;
		frame.add(passwordLabel, gbc);
		passwordField = new JPasswordField(20);
		passwordField.setText("Aminul@24");
		gbc.gridx = 1;
		gbc.gridy = 2;
		frame.add(passwordField, gbc);

		String hintText;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		if (discoveredCount > 0) {
			hintText = "Enter credentials that are valid for the majority of CCTVs.";
			discoveredCountLabel.setForeground(Color.BLUE);

			JButton getDetailsButton = getGetDetailsButton(discoveredCount, gbc);
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 2;
			frame.add(getDetailsButton, gbc);
		} else {
			hintText = "Download the template and manually fill CCTV details to verify.";
			discoveredCountLabel.setForeground(Color.RED);
			usernameField.setEnabled(false);
			passwordField.setEnabled(false);

			JButton downloadTemplateButton = new JButton("Download Template Excel");
			downloadTemplateButton.addActionListener(e -> downloadExcelTemplate());
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 2;
			frame.add(downloadTemplateButton, gbc);
		}

		JLabel hintLabel = new JLabel(hintText);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		frame.add(hintLabel, gbc);

		frame.setVisible(true);
	}

	private void downloadExcelTemplate() {
		try {
			File file = FileDialogUtils.saveFileDialog("iCamera-cctv-template");
			if (file != null) {
				Workbook.writeCctvsToExcel(file, true, SheetNames.TEMPLATE, false, TemplateColumns.values(),
						DataStore.getIdentifiedCctvs());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Error creating excel template: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private JButton getGetDetailsButton(int discoveredCount, GridBagConstraints gbc) {
		JButton getDetailsButton = new JButton("Get Details of " + discoveredCount + " CCTVs");
		getDetailsButton.addActionListener(e -> {
			frame.setEnabled(false);
			JLabel waitLabel = new JLabel("Please wait...");
			waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 2;
			frame.add(waitLabel, gbc);
			frame.revalidate();
			frame.repaint();
			new Thread(() -> {
				DataStore.setOnvifCredential(usernameField.getText(), String.valueOf(passwordField.getPassword()));
				OnvifEnquiry.enquire();
				frame.dispose();
				SwingUtilities.invokeLater(DownloadTemplateWindow::new);
			}).start();
		});
		return getDetailsButton;
	}
}
