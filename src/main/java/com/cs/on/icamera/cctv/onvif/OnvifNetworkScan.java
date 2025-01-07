package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.util.NetworkScan;

import java.util.ArrayList;
import java.util.List;

import static com.cs.on.icamera.cctv.util.UrlParser.getOnvifDeviceServiceUrl;

public class OnvifNetworkScan {
    private OnvifNetworkScan() {}

    public static final List<Integer> ONVIF_PORTS = List.of(80);
    public static final int ONVIF_PORT_SCAN_TIMEOUT = 1000;

    /**
     * Scans the network for ONVIF devices and returns a list of their service URLs.
     *
     * @param ipAddresses The list of IP addresses to scan.
     * @return The list of ONVIF device service URLs found.
     */
    public static List<String> getOnvifServiceUrls(List<String> ipAddresses) {
        List<String> onvifServiceUrls = new ArrayList<>();
        NetworkScan.getReachablePortsMap(ipAddresses, ONVIF_PORTS, ONVIF_PORT_SCAN_TIMEOUT);
        return onvifServiceUrls;
    }
}
