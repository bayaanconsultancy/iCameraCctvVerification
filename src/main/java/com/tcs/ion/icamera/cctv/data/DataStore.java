package com.tcs.ion.icamera.cctv.data;

import com.tcs.ion.icamera.cctv.model.Cctv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DataStore class serves as a centralized static container for managing and tracking
 * discovered, scanned, and verified CCTVs. It provides various utility methods to query,
 * add, and manipulate CCTV data, including retrieving specific subsets of data, counting
 * CCTVs by various metrics, and logging identified and verified CCTVs.
 * <p>
 * This class is implemented as a singleton utility with private constructor to prevent
 * instantiation. Interactions with the store are performed exclusively through static methods.
 */
public class DataStore {
    private static final Logger logger = LogManager.getLogger(DataStore.class);
    private static final Map<String, Cctv> identifiedCctvs = new HashMap<>();
    private static final List<Cctv> excelCctvs = new ArrayList<>();
    private static int discoveredCctvCount;
    private static int scannedCctvCount;

    private DataStore() {}

    /**
     * Adds a discovered CCTV to the identified list if it is not already present
     * and increments the discovered CCTV count.
     *
     * @param cctv the CCTV object representing the camera to add
     */
    public static void addDiscoveredCctv(Cctv cctv) {
        if (identifiedCctvs.put(cctv.getOnvifUrl(), cctv) == null) discoveredCctvCount++;
    }

    /**
     * Retrieves the count of discovered CCTV devices.
     *
     * @return the number of discovered CCTV devices
     */
    public static int getDiscoveredCctvCount() {
        return discoveredCctvCount;
    }

    /**
     * Adds a scanned CCTV to the identified list if it is not already present
     * and increments the scanned CCTV count.
     *
     * @param cctv the CCTV object representing the camera to add
     */
    public static void addScannedCctv(Cctv cctv) {
        if (!identifiedCctvs.containsKey(cctv.getOnvifUrl())) {
            identifiedCctvs.put(cctv.getOnvifUrl(), cctv);
            scannedCctvCount++;
        }
    }

    /**
     * Retrieves the count of scanned CCTV devices.
     *
     * @return the number of scanned CCTV devices
     */
    public static int getScannedCctvCount() {
        return scannedCctvCount;
    }

    /**
     * Retrieves the list of identified CCTV objects.
     *
     * @return a list containing all identified CCTV objects
     */
    public static List<Cctv> getIdentifiedCctvs() {
        return new ArrayList<>(identifiedCctvs.values());
    }

    /**
     * Retrieves the count of identified CCTV devices.
     *
     * @return the number of identified CCTV devices
     */
    public static int getIdentifiedCctvCount() {
        return identifiedCctvs.size();
    }

    /**
     * Prints a list of identified CCTV devices to the logger.
     * <br>
     * If no CCTV devices have been identified, a message indicating that
     * no CCTVs were identified is logged. Otherwise, logs the identified
     * CCTV devices by iterating through each entry in the identifiedCctvs map.
     */
    public static void printIdentifiedCctvs() {
        if (identifiedCctvs.isEmpty()) {
            logger.info("No CCTVs identified.");
        } else {
            logger.info("Discovered CCTVs: ");
            for (Cctv cctv : identifiedCctvs.values()) {
                logger.info("-- {}", cctv);
            }
        }
    }

    /**
     * Logs the list of verified CCTV devices to the application logger.
     * If no CCTV devices have been verified, a message will be logged
     * indicating that no CCTVs have been verified. Otherwise, logs a
     * detailed list of the verified CCTVs.
     */
    public static void printVerifiedCctvs() {
        if (excelCctvs.isEmpty()) {
            logger.info("No CCTVs verified.");
        } else {
            logger.info("Verified CCTVs: ");
            for (Cctv cctv : excelCctvs) {
                logger.info("-- {}", cctv);
            }
        }
    }

    /**
     * Calculates the number of identified CCTV devices that have reported errors.
     * It filters the identified CCTVs and counts those whose status indicates failure
     * based on the success method of the Cctv object.
     *
     * @return the count of CCTVs with errors
     */
    public static int getOnvifCctvErrorCount() {
        return identifiedCctvs.values().stream().filter(cctv -> !cctv.success()).toList().size();
    }

    /**
     * Retrieves the list of CCTV objects that are sourced from an Excel file.
     *
     * @return a list containing all CCTV objects obtained from the Excel data
     */
    public static List<Cctv> getExcelCctvs() {
        return excelCctvs;
    }

    /**
     * Updates the list of CCTV objects sourced from an Excel file.
     * Clears the existing list and adds all the provided CCTVs.
     *
     * @param cctvs the new list of CCTV objects to be set as the Excel-sourced data
     */
    public static void setExcelCctvs(List<Cctv> cctvs) {
        excelCctvs.clear();
        excelCctvs.addAll(cctvs);
    }

    /**
     * Retrieves the list of CCTV objects from the Excel-sourced data
     * that have been successfully processed.
     *
     * @return a list of successfully processed CCTV objects.
     */
    public static List<Cctv> getCameraResources() {
        return excelCctvs.stream().filter(Cctv::success).toList();
    }

    /**
     * Retrieves a list of identified CCTV objects that have an ONVIF URL.
     *
     * @return a list of CCTV objects from the identified list that include an ONVIF URL
     */
    public static List<Cctv> getRefuteOnvifCctvs() {
        return identifiedCctvs.values().stream().filter(Cctv::hasOnvifUrl).toList();
    }

    /**
     * Retrieves a list of identified CCTV objects that have an RTSP port.
     *
     * @return a list of CCTV objects from the identified list that include an RTSP port
     */
    public static List<Cctv> getRefuteRtspCctvs() {
        return identifiedCctvs.values().stream().filter(Cctv::hasRtspPort).toList();
    }

    /**
     * Retrieves the number of identified CCTV objects that include an ONVIF URL.
     * This count is determined by the size of the list returned from the method
     * that filters CCTVs with an ONVIF URL.
     *
     * @return the count of CCTV objects with an ONVIF URL
     */
    public static int getRefuteOnvifCctvCount() {
        return getRefuteOnvifCctvs().size();
    }

    /**
     * Calculates and retrieves the total count of identified CCTV objects that
     * either have an ONVIF URL or an RTSP port. This is determined by summing the
     * sizes of the lists returned from `getRefuteOnvifCctvs` and `getRefuteRtspCctvs`.
     *
     * @return the total number of identified CCTVs with either an ONVIF URL or an RTSP port
     */
    public static int getRefuteCctvCount() {
        return getRefuteOnvifCctvs().size() + getRefuteRtspCctvs().size();
    }
}
