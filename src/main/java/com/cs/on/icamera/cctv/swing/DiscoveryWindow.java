package com.cs.on.icamera.cctv.swing;

import javax.swing.*;

public class DiscoveryWindow {
    private JFrame frame;

    public DiscoveryWindow() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("CCTV Discovery");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(200, 100);
        frame.setLocationRelativeTo(null);
        JLabel waitLabel = new JLabel("Please wait until CCTV discovery is completed...");
        frame.add(waitLabel);
        frame.setVisible(true);
        new Thread(this::doDiscoveryInBackground).start();
    }

    private void doDiscoveryInBackground() {
        SwingUtilities.invokeLater(this::openNextWindow);
    }

    private void openNextWindow() {
        frame.dispose();
        new NetworkScanWindow();
    }
}
