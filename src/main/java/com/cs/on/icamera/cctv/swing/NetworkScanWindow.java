package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.onvif.OnvifNetworkScan;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkScanWindow {

    private final JFrame frame;
    private final JProgressBar progressBar;
    private final JLabel statusLabel;
    private final Timer timer;
    private final Runnable scanTask;

    public NetworkScanWindow(Runnable scanTask) {
        this.scanTask = scanTask;
        this.timer = new Timer();

        frame = new JFrame("Scanning Network");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 300);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 245)); // Light gray background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("Network Scan in Progress");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 153));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        frame.add(titleLabel, gbc);

        // Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Arial", Font.BOLD, 14));
        progressBar.setForeground(new Color(76, 175, 80)); // Green color for progress bar
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span across two columns
        gbc.ipady = 15; // Increase height of progress bar
        frame.add(progressBar, gbc);

        // Status Label
        statusLabel = new JLabel("Starting network scan...");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        statusLabel.setForeground(new Color(90, 90, 90)); // Subtle gray text
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span across two columns
        gbc.ipady = 0; // Reset height adjustment
        frame.add(statusLabel, gbc);

        // Cancel Button
        JButton cancelButton = createStyledButton("Cancel");
        cancelButton.addActionListener(e -> {
            timer.cancel();
            frame.dispose();
        });
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Center the button
        gbc.ipady = 10;
        frame.add(cancelButton, gbc);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        startNetworkScan();
    }

    private void startNetworkScan() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                int progress = OnvifNetworkScan.progress();
                progressBar.setValue(progress);

                statusLabel.setText(String.format("Scanning IPs: %d of %d processed.",
                        OnvifNetworkScan.getCount(), OnvifNetworkScan.getTotalCount()));

                if (OnvifNetworkScan.isComplete()) {
                    timer.cancel();
                    frame.dispose();
                    new IdentifiedCctvWindow();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000); // Update every 1 second
        new Thread(scanTask).start();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(220, 53, 69)); // Red background for cancel
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 0, 0), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return button;
    }
}