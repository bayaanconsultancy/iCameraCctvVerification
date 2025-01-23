package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.ffmpeg.CctvVerification;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class VerifyCctvWindow {
    private final JFrame frame;
    private final JProgressBar progressBar;
    private Timer timer;

    public VerifyCctvWindow() {
        frame = new JFrame("Verifying CCTVs");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLayout(new BorderLayout());

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(600, 30));

        frame.add(progressBar, BorderLayout.CENTER);

        verify();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void verify() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                progressBar.setValue(CctvVerification.progress());

                if (CctvVerification.isComplete()) {
                    timer.cancel();
                    frame.dispose();
                    new ExportExcelWindow();
                }
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 5000);

        new Thread(CctvVerification::verify).start();
    }
}
