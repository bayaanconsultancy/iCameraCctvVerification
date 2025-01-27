package com.tcs.ion.icamera.cctv.onvif;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.util.HttpSoapClient;
import com.tcs.ion.icamera.cctv.util.Network;
import com.tcs.ion.icamera.cctv.util.NetworkScan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

import static com.tcs.ion.icamera.cctv.util.UrlParser.getOnvifDeviceServiceUrl;

public class OnvifNetworkScan {
    // Common ONVIF Service Ports
    public static final Set<Integer> ONVIF_PORTS = Set.of(80, 8000, 8080);
    // Timeout for ONVIF port scans
    public static final int ONVIF_PORT_SCAN_TIMEOUT = 1000;

    private static final Logger logger = LogManager.getLogger(OnvifNetworkScan.class);
    private static NetworkScan scanner;

    private OnvifNetworkScan() {
    }

    public static void scan() {
        scan(Network.getInetAddressesInSubnet());
    }

    public static void scan(String ip1, String ip2) {
        scan(Network.inetAddressesBetween(ip1, ip2));
    }

    private static void scan(Set<String> ips) {
        logger.info("Scanning IPs {}", ips);
        getLatentOnvifUrls(ips).forEach(onvifUrl -> {
            if (checkPossibleOnvifDevice(onvifUrl)) {
                DataStore.addScannedCctv(new Cctv().withOnvifDeviceUrl(onvifUrl));
            }
        });

        logger.info("Discovered {} ONVIF devices by network scan.", DataStore.getScannedCctvCount());
    }

    private static Set<String> getLatentOnvifUrls(Set<String> ips) {
        Set<String> onvifUrls = new HashSet<>();
        scanner = new NetworkScan(
                // For all possible IP addresses of all network interfaces
                ips,
                // For all possible ONVIF ports
                ONVIF_PORTS);

        scanner.scan(ONVIF_PORT_SCAN_TIMEOUT)
                // For each reachable IP address and port
                .forEach((ip, ports) -> ports.forEach(port ->
                        // Add discovered ONVIF devices to the latent CCTV list
                        onvifUrls.add(getOnvifDeviceServiceUrl(ip, port))));

        logger.info("Discovered {} possible devices ONVIF devices.", onvifUrls.size());
        logger.info(onvifUrls);
        return onvifUrls;
    }

    private static boolean checkPossibleOnvifDevice(String onvifUrl) {
        try {
            // Make the request to the ONVIF device
            String response = HttpSoapClient.postXml(onvifUrl, OnvifSoapMessages.ONVIF_COMPATIBILITY);
            // Parse the response to check if the device is an ONVIF compliant device
            return OnvifResponseParser.isOnvifDevice(response);
        } catch (Exception e) {
            logger.info("Error checking ONVIF compatibility for {}", onvifUrl, e);
            return false;
        }
    }

    public static int progress() {
        return scanner == null ? 0 : scanner.getProgress();
    }

    public static boolean isComplete() {
        return scanner != null && scanner.isComplete();
    }

    public static int getTotalCount() {
        return scanner == null ? 0 : scanner.getTotalCount();
    }

    public static int getCount() {
        return scanner == null ? 0 : scanner.getCount();
    }
}
