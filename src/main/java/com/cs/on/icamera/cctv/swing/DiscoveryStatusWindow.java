package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.data.DataStore;

import javax.swing.*;
import java.awt.*;

public class DiscoveryStatusWindow {

	private final JFrame frame;

	public DiscoveryStatusWindow() {
		frame = new JFrame("CCTV Discovery");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		JLabel discoveredCountLabel = new JLabel("Discovered CCTVs: ");
		discoveredCountLabel.setFont(discoveredCountLabel.getFont().deriveFont(Font.BOLD));
		gbc.gridx = 0;
		gbc.gridy = 0;
		frame.add(discoveredCountLabel, gbc);

		int cctvCount = DataStore.getDiscoveredCctvCount();
		discoveredCountLabel = new JLabel(String.valueOf(cctvCount));
		discoveredCountLabel.setFont(discoveredCountLabel.getFont().deriveFont(Font.BOLD));
		discoveredCountLabel.setForeground(Color.BLUE);
		gbc.gridx = 1;
		gbc.gridy = 0;
		frame.add(discoveredCountLabel, gbc);

		JButton networkScanButton = new JButton("Scan Network for more CCTVs");
		networkScanButton.setToolTipText(
				"If the number of discovered CCTVs is less than the total number of CCTVs, try running network scan to identify more CCTVs on the network.");
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		networkScanButton.addActionListener(e -> {
			frame.dispose();
			new NetworkScanWindow();
		});
		frame.add(networkScanButton, gbc);

		if (cctvCount > 0) {
			JButton verifyButton = new JButton("Proceed with " + cctvCount + " CCTVs");
			verifyButton.setToolTipText("Click to proceed with the " + cctvCount + " discovered CCTVs.");
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridwidth = 2;
			verifyButton.addActionListener(e -> {
				frame.dispose();
				new IdentifiedCctvWindow();
			});
			frame.add(verifyButton, gbc);
		}

		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
}
