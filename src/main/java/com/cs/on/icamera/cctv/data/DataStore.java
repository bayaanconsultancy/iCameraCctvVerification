package com.cs.on.icamera.cctv.data;

import com.cs.on.icamera.cctv.model.Cctv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {
    private static final Logger logger = LogManager.getLogger(DataStore.class);
    private static final Map<String, Cctv> identifiedDevices = new HashMap<>();
    private static int discoveredCctvCount;
    private static int scannedCctvCount;

    private DataStore() {
    }

    public static void addDiscoveredCctv(Cctv cctv) {
        if (identifiedDevices.put(cctv.getOnvifUrl(), cctv) == null) discoveredCctvCount++;
    }

    public static int getDiscoveredCctvCount() {
        return discoveredCctvCount;
    }

    public static void addScannedCctv(Cctv cctv) {
        if (identifiedDevices.put(cctv.getOnvifUrl(), cctv) == null) scannedCctvCount++;
    }

    public static int getScannedCctvCount() {
        return scannedCctvCount;
    }

    public static List<Cctv> getIdentifiedDevices() {
        return new ArrayList<>(identifiedDevices.values());
    }

    public static void printIdentifiedCctvs() {
        if (identifiedDevices.isEmpty()) {
            logger.info("No CCTVs discovered.");
        } else {
            logger.info("Discovered CCTVs: ");
            for (Cctv cctv : identifiedDevices.values()) {
                logger.info("-- {}", cctv);
            }
        }
    }

    public static void setOnvifCredential(String username, String password) {
        for (Cctv cctv : identifiedDevices.values()) {
            cctv.setOnvifUsername(username);
            cctv.setOnvifPassword(password);
        }
    }

}
