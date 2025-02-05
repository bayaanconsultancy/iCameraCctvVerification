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

/**
 * The OnvifNetworkScan class provides functionality to discover ONVIF-compatible devices
 * on a network. This class scans a given IP range or subnet for devices that respond
 * on specific ONVIF service ports. It checks for ONVIF compliance by analyzing responses
 * from the devices.
 * <p>
 * This utility uses a predefined set of common ONVIF ports to probe devices and validate
 * their compatibility based on the ONVIF protocol standard. Scanning can include an entire
 * subnet or specific IP ranges, and devices identified as ONVIF-compliant are stored in
 * a central DataStore for further use.
 * <p>
 * The scanning process involves the following steps:
 * - Scanning all IPs within a specified range or network subnet.
 * - Querying devices on known ONVIF ports.
 * - Validating ONVIF capability by sending SOAP requests and inspecting responses.
 * <p>
 * This class supports progress tracking for the current network scan process.
 * <p>
 * Note: This class cannot be instantiated.
 */
public class OnvifNetworkScan {
    // Common ONVIF Service Ports
    public static final Set<Integer> ONVIF_PORTS = Set.of(80, 8000, 8080, 8899);
    // Timeout for ONVIF port scans
    public static final int ONVIF_PORT_SCAN_TIMEOUT = 1000;

    private static final Logger logger = LogManager.getLogger(OnvifNetworkScan.class);
    private static NetworkScan scanner;

    private OnvifNetworkScan() {
    }

    /**
     * Initiates a network scan to discover ONVIF-compatible devices.
     * <p>
     * This method retrieves all IP addresses within the subnets available
     * on the local network interfaces using {@link Network#getInetAddressesInSubnet()}.
     * These IPs are then scanned to identify potentially ONVIF-compatible devices.
     * <p>
     * The discovered ONVIF devices are added to the application's data store
     * for further processing.
     */
    public static void scan() {
        scan(Network.getInetAddressesInSubnet());
    }

    /**
     * Initiates a network scan on a range of IP addresses to discover ONVIF-compatible devices.
     * The method scans all IPs between the given start and end IP addresses.
     *
     * @param ip1 the starting IPv4 address of the range to scan (inclusive)
     * @param ip2 the ending IPv4 address of the range to scan (inclusive)
     */
    public static void scan(String ip1, String ip2) {
        scan(Network.inetAddressesBetween(ip1, ip2));
    }

    /**
     * Scans a set of provided IP addresses to identify ONVIF-compatible devices.
     * <p>
     * This method iterates through the given IP addresses, attempting to detect
     * ONVIF devices by checking their compatibility. Discovered devices are added
     * to the application's data store for further processing.
     *
     * @param ips the set of IP addresses to scan for ONVIF-compatible devices
     */
    private static void scan(Set<String> ips) {
        logger.info("Scanning IPs {}", ips);
        getLatentOnvifUrls(ips).forEach(onvifUrl -> {
            if (checkPossibleOnvifDevice(onvifUrl)) {
                DataStore.addScannedCctv(new Cctv().withOnvifDeviceUrl(onvifUrl));
            }
        });

        logger.info("Discovered {} ONVIF devices by network scan.", DataStore.getScannedCctvCount());
    }

    /**
     * Scans a set of IP addresses to discover potential ONVIF-compatible URLs.
     * <p>
     * This method performs a network scan over the provided IPs and predefined ONVIF ports.
     * For each reachable IP and port, it constructs a possible ONVIF device service
     * URL and adds it to the resultant set.
     *
     * @param ips the set of IP addresses to be scanned for ONVIF-compatible devices
     * @return a set of discovered ONVIF device service URLs
     */
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

    /**
     * Checks if the provided URL corresponds to an ONVIF-compatible device.
     * <p>
     * This method sends a SOAP XML request to the given URL to verify if the target
     * device responds in a manner that adheres to the ONVIF protocol. If the device
     * is ONVIF-compliant, the method returns true; otherwise, it returns false.
     * Exceptions during the process are logged, and false is returned in case of errors.
     *
     * @param onvifUrl The URL of the device to be checked for ONVIF compatibility.
     * @return {@code true} if the device is ONVIF-compliant, {@code false} otherwise.
     */
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

    /**
     * Retrieves the progress of the ONVIF network scan.
     * <p>
     * The method returns the progress as a percentage. If the scanner is not initialized, it returns 0.
     *
     * @return the progress of the ONVIF network scan as an integer percentage, or 0 if the scanner is null.
     */
    public static int getProgress() {
        return scanner == null ? 0 : scanner.getProgress();
    }

    /**
     * Checks whether the ONVIF network scan process has been completed.
     * <p>
     * This method verifies the state of the internal scanner. If the scanner is initialized and its
     * own progress indicates completion, the method returns true. Otherwise, it returns false.
     *
     * @return {@code true} if the scanner is initialized and the scan process is complete;
     *         {@code false} otherwise.
     */
    public static boolean isComplete() {
        return scanner != null && scanner.isComplete();
    }

    /**
     * Retrieves the total count of discovered ONVIF-compatible devices.
     * If the scanner is not initialized, the method returns 0.
     *
     * @return the total count of discovered devices if the scanner is initialized,
     *         or 0 if the scanner is null.
     */
    public static int getTotalCount() {
        return scanner == null ? 0 : scanner.getTotalCount();
    }

    /**
     * Retrieves the current count of detected ONVIF-compatible devices.
     * If the scanner has not been initialized, the method returns 0.
     *
     * @return the current count of detected ONVIF-compatible devices,
     *         or 0 if the scanner is null.
     */
    public static int getCount() {
        return scanner == null ? 0 : scanner.getCount();
    }
}
