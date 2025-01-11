package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.util.HttpSoapClient;
import com.cs.on.icamera.cctv.util.Network;
import com.cs.on.icamera.cctv.util.NetworkScan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

import static com.cs.on.icamera.cctv.util.UrlParser.getOnvifDeviceServiceUrl;

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
        getLatentOnvifUrls().forEach(onvifUrl -> {
            if (checkPossibleOnvifDevice(onvifUrl)) {
                DataStore.addScannedCctv(new Cctv().withOnvifDeviceUrl(onvifUrl));
            }
        });

        logger.info("Discovered {} ONVIF devices by network scan.", DataStore.getScannedCctvCount());
    }

    private static Set<String> getLatentOnvifUrls() {
        Set<String> onvifUrls = new HashSet<>();
        scanner = new NetworkScan(
                // For all possible IP addresses of all network interfaces
                Network.getInetAddressesInSubnet(),
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

    public int progress() {
        return scanner.getProgress();
    }
}
