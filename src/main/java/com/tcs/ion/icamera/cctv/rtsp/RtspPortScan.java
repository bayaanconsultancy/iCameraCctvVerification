package com.tcs.ion.icamera.cctv.rtsp;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.util.Network;
import com.tcs.ion.icamera.cctv.util.NetworkScan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The RtspPortScan class is a utility for scanning networks or IP ranges to detect devices
 * with open RTSP (Real-Time Streaming Protocol) ports. These detected devices can be
 * processed further, such as being added to a device datastore or logging their details.
 * <p>
 * This class supports the following functionality:
 * 1. Scanning a specific range of IPs for common RTSP ports.
 * 2. Scanning the entire network for RTSP ports.
 * 3. Tracking the scanning progress and completion status.
 * <p>
 * It uses a NetworkScan instance to perform the actual scanning and identifies reachable
 * hosts with open RTSP ports. The results are processed to identify new devices or update
 * existing ones in a device datastore.
 * <p>
 * This class is not instantiable as its functionality is entirely static.
 */
public class RtspPortScan {
    // Common RTSP Ports
    private static final Set<Integer> RTSP_PORTS = Set.of(554, 5543, 8554);
    private static final Logger logger = LogManager.getLogger(RtspPortScan.class);
    private static NetworkScan scanner;

    private RtspPortScan() {
    }

    /**
     * Initiates an RTSP port scan for a specified range of IPv4 addresses.
     *
     * @param ip1 The starting IPv4 address of the range, inclusive.
     * @param ip2 The ending IPv4 address of the range, inclusive.
     */
    public static void scan(String ip1, String ip2) {
        logger.info("Starting RTSP scan for IP range: {} - {}", ip1, ip2);
        scan(Network.inetAddressesBetween(ip1, ip2));
    }

    /**
     * Initiates an RTSP port scan for the entire network.
     * <p>
     * This method uses the {@link Network#getInetAddressesInSubnet()} utility to
     * obtain all IPv4 addresses within the current network's subnets and scans each
     * address for RTSP open ports. The results are logged upon completion.
     */
    public static void scan() {
        logger.info("Starting RTSP scan for entire network.");
        scan(Network.getInetAddressesInSubnet());
    }

    /**
     * Initiates an RTSP port scan for a specified set of IP addresses.
     * Scans each provided IP for open RTSP ports and logs the results.
     *
     * @param ips Set of IPv4 addresses to be scanned for RTSP ports.
     */
    public static void scan(Set<String> ips) {
        scanner = new NetworkScan(ips, RTSP_PORTS);
        Map<String, List<Integer>> reachableHosts = scanner.scan();

        logger.info("RTSP scan complete. Found {} IPs with RTSP ports.", reachableHosts.size());

        // Call to process and log new RTSP devices
        processReachableHosts(reachableHosts);
    }

    /**
     * Processes the given map of reachable hosts and their corresponding ports.
     * For each entry in the map, this method checks if the host is already an identified CCTV device.
     * If not, a new CCTV instance is created and added to the datastore with an appropriate error message.
     * If the host is already identified, the RTSP port information is updated.
     *
     * @param reachableHosts A map where the key represents an IP address (as a String) and the value
     *                       is a list of integers representing the open ports for that address.
     */
    private static void processReachableHosts(Map<String, List<Integer>> reachableHosts) {
        reachableHosts.forEach((ip, ports) -> ports.forEach(port -> {
            Cctv cctv = DataStore.getIdentifiedCctvs().stream().filter(c -> ip.equals(c.getIp())).findFirst().orElse(null);

            if (cctv == null) {
                logger.info("IP {} not in identified devices. Adding as new Cctv with ONVIF not enabled error.", ip);

                // Create a new Cctv object and add it to DataStore
                Cctv newCctv = new Cctv().withIp(ip).withRtspPort(port).withError("ONVIF not enabled for this CCTV.");

                DataStore.addScannedCctv(newCctv);
            } else {
                logger.info("IP {}:{} already exists in identified devices.", ip, port);

                // Update the existing Cctv object with the RTSP port
                cctv.setRtspPort(port);
            }
        }));
    }

    /**
     * Retrieves the progress of the current RTSP port scanning operation.
     * If no scanning operation is in progress, the method returns 0.
     *
     * @return an integer representing the progress percentage of the scan. Returns 0 if no scan is active.
     */
    public static int getProgress() {
        return scanner == null ? 0 : scanner.getProgress();
    }

    /**
     * Checks if the RTSP port scanning operation is complete.
     *
     * @return true if the scanner is not null and the scanning operation is complete,
     *         false otherwise.
     */
    public static boolean isComplete() {
        return scanner != null && scanner.isComplete();
    }

    /**
     * Retrieves the total count of entries detected by the RTSP port scanning operation.
     * Returns 0 if the scanner is not initialized.
     *
     * @return the total count of detected entries if the scanner is initialized, otherwise 0.
     */
    public static int getTotalCount() {
        return scanner == null ? 0 : scanner.getTotalCount();
    }

    /**
     * Retrieves the count of detected entries from the scanner.
     * If the scanner is not initialized, this method returns 0.
     *
     * @return the count of detected entries if the scanner is initialized; otherwise 0.
     */
    public static int getCount() {
        return scanner == null ? 0 : scanner.getCount();
    }
}
