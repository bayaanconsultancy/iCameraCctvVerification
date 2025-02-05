package com.tcs.ion.icamera.cctv.swing;

import com.tcs.ion.icamera.cctv.rtsp.CctvVerification;

import javax.swing.*;

public class VerifyCctvWindow extends SwingWindow {
    public VerifyCctvWindow() {
        super("Verifying CCTVs");
    }

    @Override
    protected void buildUiAndFunctionality() {
        JLabel statusLabel = createLabel("Verifying CCTVs. Please wait...");
        JProgressBar progressBar = runWithProgress(statusLabel, CctvVerification::verify, CctvVerification::progress, CctvVerification::getCount, CctvVerification::getTotalCount, CctvVerification::isComplete, new ExportExcelWindow());
        add(statusLabel);
        add(progressBar);
    }
}
