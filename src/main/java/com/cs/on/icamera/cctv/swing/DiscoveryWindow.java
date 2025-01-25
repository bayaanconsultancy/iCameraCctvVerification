package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.onvif.OnvifDiscovery;

import javax.swing.*;
import java.awt.*;

public class DiscoveryWindow {
    private JFrame frame;

    public DiscoveryWindow() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("CCTV Discovery");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 245)); // Light gray background

        // Setting up GridBagLayout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("CCTV Discovery", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 153)); // Dark blue
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(titleLabel, gbc);

        // Wait Message Label
        JLabel waitLabel = new JLabel("Discovering CCTVs. Please wait...");
        waitLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        waitLabel.setForeground(new Color(102, 102, 102)); // Gray
        waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        frame.add(waitLabel, gbc);

        // Progress Bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // Indeterminate mode for discovery process
        progressBar.setPreferredSize(new Dimension(300, 25));
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(new Color(0, 153, 76)); // Green
        gbc.gridy = 2;
        gbc.ipady = 20; // Add height to the progress bar
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(progressBar, gbc);

        // Cancel Button
        JButton cancelButton = createStyledButton("Cancel");
        cancelButton.addActionListener(e -> {
            // Dispose window on cancel (or stop discovery logic if applicable)
            frame.dispose();
        });
        gbc.gridy = 3;
        gbc.ipady = 0; // Reset height for the button
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(cancelButton, gbc);

        // Make frame visible
        frame.setVisible(true);

        // Start discovery in the background
        new Thread(this::doDiscoveryInBackground).start();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(204, 51, 51)); // Red
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(153, 0, 0), 1), // Dark Red Border
                BorderFactory.createEmptyBorder(5, 15, 5, 15) // Padding
        ));
        return button;
    }

    private void doDiscoveryInBackground() {
        OnvifDiscovery.discover();
        SwingUtilities.invokeLater(this::openNextWindow);
    }

    private void openNextWindow() {
        frame.dispose();
        new DiscoveryStatusWindow();
    }
}