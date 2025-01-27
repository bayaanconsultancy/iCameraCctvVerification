package com.tcs.ion.icamera.cctv.swing;

import com.tcs.ion.icamera.cctv.onvif.OnvifDiscovery;

public class DiscoveryWindow extends SwingWindow {
    public DiscoveryWindow() {
        super("CCTV Discovery");
    }

    @Override
    protected void buildUiAndFunctionality() {
        add(createLabel("Discovering CCTVs. Please wait..."));
        doInBackground(OnvifDiscovery::discover, () -> next(new DiscoveryStatusWindow()::show));
    }
}