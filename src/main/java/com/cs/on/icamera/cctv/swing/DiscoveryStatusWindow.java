package com.cs.on.icamera.cctv.swing;


import com.cs.on.icamera.cctv.data.DataStore;

import javax.swing.*;
import java.awt.*;

public class DiscoveryStatusWindow {

    private final JFrame frame;

    public DiscoveryStatusWindow() {
        frame = new JFrame("CCTV Discovery Status");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel discoveredCountLabel = new JLabel("Discovered CCTVs: ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(discoveredCountLabel, gbc);

        discoveredCountLabel = new JLabel(String.valueOf(DataStore.getDiscoveredCctvCount()));
        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(discoveredCountLabel, gbc);

        JButton networkScanButton = new JButton("Scan Network");
        networkScanButton.setToolTipText("If the number of discovered CCTVs is less than the total number of CCTVs, try running network scan to identify more CCTVs on the network.");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        networkScanButton.addActionListener(e -> {
            frame.dispose();
            //new MainWindow().networkScan();
        });
        frame.add(networkScanButton, gbc);

        JButton verifyButton = new JButton("Verify CCTVs");
        verifyButton.setToolTipText("Verify the identified CCTVs using Excel file.");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        verifyButton.addActionListener(e -> {
            frame.dispose();
            //new MainWindow().verifyCctvs();
        });
        frame.add(verifyButton, gbc);

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
