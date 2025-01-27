package com.tcs.ion.icamera.cctv.swing;

import com.tcs.ion.icamera.cctv.onvif.OnvifNetworkScan;

import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class IPAddressInput extends SwingWindow {
    public IPAddressInput() {
        super("IP Address Scanner");

        // Information label
    }

    @Override
    protected void buildUiAndFunctionality() {
        JLabel infoLabel = createLabel("Provide start IP address and end IP address of CCTVs.");
        add(infoLabel);

        // Input for first IP
        JLabel ip1Label = createLabel("Enter start IP address:");
        JTextField ip1Field = createTextField(15);
        add(ip1Label, ip1Field);

        // Input for second IP
        JLabel ip2Label = createLabel("Enter second IP address:");
        JTextField ip2Field = createTextField(15);
        add(ip2Label, ip2Field);

        // "Scan" button
        JButton scanButton = createOkButton("Scan", "Click to scan CCTVs within the provided IP range.");
        scanButton.addActionListener(e -> {
            String ip1 = ip1Field.getText().trim();
            String ip2 = ip2Field.getText().trim();

            if (ip1.isEmpty() || ip2.isEmpty()) {
                showErrorMessage("Both IP addresses must be entered!");
                return;
            }

            String ipRegex = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";
            if (!ip1.matches(ipRegex) || !ip2.matches(ipRegex)) {
                showErrorMessage("Provide IPv4 addresses!");
                return;
            }

            if (ip1.equals(ip2)) {
                showErrorMessage("IP addresses must be different!");
                return;
            }

            try {
                // Validate IP addresses
                InetAddress inet1 = InetAddress.getByName(ip1);
                InetAddress inet2 = InetAddress.getByName(ip2);

                // Store IPs in an array
                String[] ips = {inet1.getHostAddress(), inet2.getHostAddress()};

                // Sort IPs
                Arrays.sort(ips);

                SwingUtilities.invokeLater(() -> next(                    new NetworkScanWindow(() -> OnvifNetworkScan.scan(ips[0], ips[1]))));
            } catch (UnknownHostException ex) {
                showErrorMessage("Provide valid IP addresses!");
            }
        });

        add(scanButton);
    }
}
