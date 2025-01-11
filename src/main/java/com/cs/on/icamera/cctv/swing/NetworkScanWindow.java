package com.cs.on.icamera.cctv.swing;

import javax.swing.*;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkScanWindow {
    private final JFrame frame;
    private final JProgressBar progressBar;
    private final JButton nextButton;
    private Timer timer;

    public NetworkScanWindow() {
        frame = new JFrame("Network Scan");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new BorderLayout());

        JLabel waitLabel = new JLabel("Please wait...");
        waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(waitLabel, BorderLayout.NORTH);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        frame.add(progressBar, BorderLayout.CENTER);

        nextButton = new JButton("Next");
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> showNextWindow());
        frame.add(nextButton, BorderLayout.SOUTH);

        startNetworkScan();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startNetworkScan() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            private int progress = 0;

            @Override
            public void run() {
                if (progress < 100) {
                    progress += 10; // Simulate scan progress
                    progressBar.setValue(progress);
                } else {
                    timer.cancel();
                    nextButton.setEnabled(true);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 5000);
    }

    private void showNextWindow() {
        // Logic to show the next window
        JOptionPane.showMessageDialog(frame, "Proceeding to the next window...");
    }
}
