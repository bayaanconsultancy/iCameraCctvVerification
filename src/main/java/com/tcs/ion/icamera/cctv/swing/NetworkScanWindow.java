package com.tcs.ion.icamera.cctv.swing;

import com.tcs.ion.icamera.cctv.onvif.OnvifNetworkScan;

import javax.swing.*;

public class NetworkScanWindow extends SwingWindow {

    private final Runnable scanTask;

    public NetworkScanWindow(Runnable scanTask) {
        super("Scanning Network");
        this.scanTask = scanTask;
    }

    @Override
    protected void buildUiAndFunctionality() {

        // Status Label
        JLabel statusLabel = createLabel("Starting network scan...");
        add(statusLabel);

        // Progress Bar
        JProgressBar progressBar = runWithProgress(statusLabel, scanTask, OnvifNetworkScan::progress, OnvifNetworkScan::getCount, OnvifNetworkScan::getTotalCount, OnvifNetworkScan::isComplete, new IdentifiedCctvWindow());
        add(progressBar);
    }
}