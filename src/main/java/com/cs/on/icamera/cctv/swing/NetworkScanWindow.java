package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.onvif.OnvifNetworkScan;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkScanWindow {

    private final JFrame frame;
    private final JProgressBar progressBar;
    private Timer timer;

    public NetworkScanWindow() {
        frame = new JFrame("Scanning Network");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLayout(new BorderLayout());

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(600, 30));

        frame.add(progressBar, BorderLayout.CENTER);

        startNetworkScan();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startNetworkScan() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                progressBar.setValue(OnvifNetworkScan.progress());

                if (OnvifNetworkScan.isComplete()) {
                    timer.cancel();
                    frame.dispose();
                    new IdentifiedCctvWindow();
                }
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 5000);

        new Thread(OnvifNetworkScan::scan).start();
    }
}
