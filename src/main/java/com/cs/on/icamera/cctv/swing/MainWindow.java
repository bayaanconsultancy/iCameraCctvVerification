package com.cs.on.icamera.cctv.swing;


import javax.swing.*;
import java.awt.*;

public class MainWindow {
    public MainWindow() {
        JFrame frame = new JFrame("CCTV Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridBagLayout());

        JButton discoverButton = new JButton("Discover CCTV");
        discoverButton.setToolTipText("Click to discover available CCTV devices on the network.");
        JButton verifyButton = new JButton("Verify CCTV using Excel");
        verifyButton.setToolTipText("Click to verify available CCTV devices using Excel file.");
        GridBagConstraints gbc = new GridBagConstraints();

        discoverButton.addActionListener(e -> {
            // Logic for discovering CCTV
        });

        verifyButton.addActionListener(e -> {
            // Logic for verifying CCTV using Excel
        });

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(discoverButton, gbc);

        gbc.gridy = 1;
        frame.add(verifyButton, gbc);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
