package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.onvif.OnvifNetworkScan;

import javax.swing.*;
import java.awt.*;

public class DiscoveryStatusWindow {

    private final JFrame frame;

    public DiscoveryStatusWindow() {
        frame = new JFrame("CCTV Discovery");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 245)); // Light gray background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // Insets for spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Discovered CCTV Label
        JLabel discoveredCountLabel = new JLabel("Discovered CCTVs: ");
        discoveredCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST; // Align left
        frame.add(discoveredCountLabel, gbc);

        // Discovered CCTV Count Value
        int cctvCount = DataStore.getDiscoveredCctvCount();
        JLabel discoveredCountValue = new JLabel(String.valueOf(cctvCount));
        discoveredCountValue.setFont(new Font("Arial", Font.BOLD, 16));
        discoveredCountValue.setForeground(new Color(0, 102, 204)); // Light Blue
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST; // Align right
        frame.add(discoveredCountValue, gbc);

        // Network Scan Button
        JButton networkScanButton = createStyledButton("Scan Network for more CCTVs");
        networkScanButton.setToolTipText("Discover more CCTVs by scanning your network.");
        networkScanButton.addActionListener(e -> {
            frame.dispose();
            new NetworkScanWindow(OnvifNetworkScan::scan);
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span across both columns
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(networkScanButton, gbc);

        // IP Range Scan Button
        JButton ipRangeScanButton = createStyledButton("Scan Network with IP Range");
        ipRangeScanButton.setToolTipText("Manually provide an IP range to scan for more CCTVs.");
        ipRangeScanButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(IPAddressInput::new);
        });
        gbc.gridy = 2;
        frame.add(ipRangeScanButton, gbc);

        // Verify Button (only when devices are discovered)
        if (cctvCount > 0) {
            JButton verifyButton = createStyledButton("Proceed with " + cctvCount + " CCTVs");
            verifyButton.setToolTipText("Click to proceed with the discovered CCTVs.");
            verifyButton.addActionListener(e -> {
                frame.dispose();
                new IdentifiedCctvWindow();
            });
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            frame.add(verifyButton, gbc);
        }

        // Finalizing frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Method to create a styled button
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 153, 76)); // Green
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 51), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }
}