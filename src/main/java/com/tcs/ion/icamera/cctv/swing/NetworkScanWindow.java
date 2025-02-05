package com.tcs.ion.icamera.cctv.swing;

import com.tcs.ion.icamera.cctv.onvif.OnvifNetworkScan;
import com.tcs.ion.icamera.cctv.rtsp.RtspPortScan;

import javax.swing.*;

public class NetworkScanWindow extends SwingWindow {

    private static final short TOTAL_TASKS = 2;
    private final Runnable onvifScanTask;
    private final Runnable rtspScanTask;
    private short completedTasks = 0;

    public NetworkScanWindow() {
        super("Scanning Network for CCTVs");
        onvifScanTask = OnvifNetworkScan::scan;
        rtspScanTask = RtspPortScan::scan;
    }

    public NetworkScanWindow(String ip1, String ip2) {
        super("Scanning Network for CCTVs");
        onvifScanTask = () -> OnvifNetworkScan.scan(ip1, ip2);
        rtspScanTask = () -> RtspPortScan.scan(ip1, ip2);
    }

    @Override
    protected void buildUiAndFunctionality() {

        // Status Label
        JLabel onvifStatusLabel = createLabel("Scanning for ONVIF devices:");
        JLabel rtspStatusLabel = createLabel("Scanning for RTSP hosts:");

        // Progress Bar
        JProgressBar onvifProgressBar = runWithProgress(onvifStatusLabel, onvifScanTask, OnvifNetworkScan::getProgress, OnvifNetworkScan::getCount, OnvifNetworkScan::getTotalCount, OnvifNetworkScan::isComplete, this::next);
        JProgressBar rtspProgressBar = runWithProgress(rtspStatusLabel, rtspScanTask, RtspPortScan::getProgress, RtspPortScan::getCount, RtspPortScan::getTotalCount, RtspPortScan::isComplete, this::next);

        // Add the components
        add(onvifStatusLabel);
        add(onvifProgressBar);
        add(rtspStatusLabel);
        add(rtspProgressBar);
    }

    protected void next() {
        if (++completedTasks == TOTAL_TASKS) {
            next(new IdentifiedCctvWindow());
        }
    }
}