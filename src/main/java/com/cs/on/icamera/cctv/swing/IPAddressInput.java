package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.onvif.OnvifNetworkScan;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class IPAddressInput {
    public IPAddressInput() {
        JFrame frame = new JFrame("IP Address Scanner");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(240, 248, 255)); // Light blue

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Information label
        JLabel infoLabel = new JLabel("Provide start IP address and end IP address of CCTVs.", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoLabel.setForeground(new Color(0, 51, 102)); // Dark blue
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across 2 columns
        frame.add(infoLabel, gbc);

        // Input for first IP
        JLabel ip1Label = new JLabel("Enter first IP address:");
        ip1Label.setFont(new Font("Arial", Font.PLAIN, 14));
        ip1Label.setForeground(new Color(0, 102, 153)); // Slightly lighter blue
        JTextField ip1Field = createStyledTextField();

        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reset to single column
        frame.add(ip1Label, gbc);
        gbc.gridx = 1;
        frame.add(ip1Field, gbc);

        // Input for second IP
        JLabel ip2Label = new JLabel("Enter second IP address:");
        ip2Label.setFont(new Font("Arial", Font.PLAIN, 14));
        ip2Label.setForeground(new Color(0, 102, 153));
        JTextField ip2Field = createStyledTextField();

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(ip2Label, gbc);
        gbc.gridx = 1;
        frame.add(ip2Field, gbc);

        // "Scan" button
        JButton scanButton = createStyledButton("Scan");
        scanButton.addActionListener(e -> {
            String ip1 = ip1Field.getText().trim();
            String ip2 = ip2Field.getText().trim();

            if (ip1.isEmpty() || ip2.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Both IP addresses must be entered!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String ipRegex = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";
            if (!ip1.matches(ipRegex) || !ip2.matches(ipRegex)) {
                JOptionPane.showMessageDialog(frame, "Invalid IP address entered!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (ip1.equals(ip2)) {
                JOptionPane.showMessageDialog(frame, "IP addresses must be different!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Validate IP addresses
                InetAddress inet1 = InetAddress.getByName(ip1);
                InetAddress inet2 = InetAddress.getByName(ip2);

                // Store IPs in an array
                String[] ips = {inet1.getHostAddress(), inet2.getHostAddress()};

                // Sort IPs
                Arrays.sort(ips);

                SwingUtilities.invokeLater(() -> {
                    frame.dispose();
                    new NetworkScanWindow(() -> OnvifNetworkScan.scan(ips[0], ips[1]));
                });
            } catch (UnknownHostException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid IP Address entered!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(scanButton, gbc);

        // Finalizing frame
        frame.setLocationRelativeTo(null); // Center frame on screen
        frame.setVisible(true);
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 153), 1), // Blue border
                BorderFactory.createEmptyBorder(5, 5, 5, 5) // Padding
        ));
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 153, 76));
        button.setForeground(new Color(0, 23, 141));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 51), 1), // Border
                BorderFactory.createEmptyBorder(5, 15, 5, 15) // Padding
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
        return button;
    }
}
