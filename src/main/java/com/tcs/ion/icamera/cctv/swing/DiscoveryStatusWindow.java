package com.tcs.ion.icamera.cctv.swing;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.onvif.OnvifNetworkScan;

import javax.swing.*;

public class DiscoveryStatusWindow extends SwingWindow {
    public DiscoveryStatusWindow() {
        super("CCTV Discovery Status");
    }

    @Override
    protected void buildUiAndFunctionality() {
        int cctvCount = DataStore.getDiscoveredCctvCount();

        JLabel discoveredCountLabel = createLabel("CCTVs Discovered:");
        JLabel discoveredCountValue = createLabel(String.valueOf(cctvCount));
        add(discoveredCountLabel, discoveredCountValue);

        JButton networkScanButton = createOkButton("Scan Network for more CCTVs", "Click to scan your entire network for more CCTVs.");
        networkScanButton.addActionListener(e -> next(new NetworkScanWindow(OnvifNetworkScan::scan)));
        add(networkScanButton);

        JButton ipRangeScanButton = createOkButton("Scan Network with IP Range", "Click to provide an IP range to scan for more CCTVs.");
        ipRangeScanButton.addActionListener(e -> next(new IPAddressInput()));
        add(ipRangeScanButton);

        if (cctvCount > 0) {
            JButton verifyButton = createOkButton("Proceed with " + cctvCount + " CCTVs", "Click to proceed with the discovered CCTVs.");
            verifyButton.addActionListener(e -> next(new IdentifiedCctvWindow()));
            add(verifyButton);
        }
    }
}