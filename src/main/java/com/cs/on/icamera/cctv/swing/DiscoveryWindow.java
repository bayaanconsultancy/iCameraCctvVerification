package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.onvif.OnvifDiscovery;

import javax.swing.*;

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
        JLabel waitLabel = new JLabel("Discovering CCTVs. Please wait...");
        waitLabel.setFont(waitLabel.getFont().deriveFont(java.awt.Font.ITALIC));
        waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(waitLabel);
        frame.setVisible(true);
        new Thread(this::doDiscoveryInBackground).start();
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
