package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.onvif.OnvifDiscovery;

import javax.swing.*;
import java.awt.*;

public class MainWindow {

    public MainWindow() {
        JFrame frame = new JFrame("ONVIF CCTV Manager");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton discoverButton = new JButton("I want to discover CCTV");
        discoverButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(discoverButton);

        JButton excelButton = new JButton("I have a prepared excel");
        excelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(excelButton);

        frame.add(panel);
        frame.setVisible(true);

        discoverButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            OnvifDiscovery.discover();
            frame.dispose();
        }));
    }
}
