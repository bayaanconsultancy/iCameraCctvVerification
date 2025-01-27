package com.tcs.ion.icamera.cctv.data;

import com.tcs.ion.icamera.cctv.model.Cctv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {
    private static final Logger logger = LogManager.getLogger(DataStore.class);
    private static final Map<String, Cctv> identifiedCctvs = new HashMap<>();
    private static final List<String> notAuthorizedOnvifUrls = new ArrayList<>();
    private static final List<Cctv> cctvsToVerify = new ArrayList<>();
    private static int discoveredCctvCount;
    private static int scannedCctvCount;

    private DataStore() {
    }

    public static void addDiscoveredCctv(Cctv cctv) {
        if (identifiedCctvs.put(cctv.getOnvifUrl(), cctv) == null) discoveredCctvCount++;
    }

    public static int getDiscoveredCctvCount() {
        return discoveredCctvCount;
    }

    public static void addScannedCctv(Cctv cctv) {
        if (identifiedCctvs.put(cctv.getOnvifUrl(), cctv) == null) scannedCctvCount++;
    }

    public static int getScannedCctvCount() {
        return scannedCctvCount;
    }

    public static List<Cctv> getIdentifiedCctvs() {
        return new ArrayList<>(identifiedCctvs.values());
    }

    public static int getIdentifiedCctvCount() {
        return identifiedCctvs.size();
    }

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

    public static void printVerifiedCctvs() {
        if (cctvsToVerify.isEmpty()) {
            logger.info("No CCTVs verified.");
        } else {
            logger.info("Verified CCTVs: ");
            for (Cctv cctv : cctvsToVerify) {
                logger.info("-- {}", cctv);
            }
        }
    }

    public static void setOnvifCredential(String username, String password) {
        for (Cctv cctv : identifiedCctvs.values()) {
            cctv.setOnvifUsername(username);
            cctv.setOnvifPassword(password);
        }
    }

    public static void setOnvifCredential(List<Cctv> devices, String username, String password) {
        for (Cctv cctv : devices) {
            cctv.setOnvifUsername(username);
            cctv.setOnvifPassword(password);
        }
    }

    public static int getOnvifErrorCount() {
        return identifiedCctvs.values().stream().filter(cctv -> !cctv.success()).toList().size();
    }

    public static List<Cctv> getCctvsToVerify() {
        return cctvsToVerify;
    }

    public static void setCctvsToVerify(List<Cctv> cctvs) {
        cctvsToVerify.clear();
        cctvsToVerify.addAll(cctvs);
    }

    public static List<Cctv> getResources() {
        return cctvsToVerify.stream().filter(Cctv::success).toList();
    }

    public static int getUnauthorizedCctvCount() {
        return notAuthorizedOnvifUrls.size();
    }

    public static List<Cctv> getUnauthorizedCctvs() {
        return notAuthorizedOnvifUrls.stream().map(identifiedCctvs::get).toList();
    }

    public static void addUnauthorizedCctv(Cctv cctv) {
        logger.info("Unauthorized ONVIF URL: {}", cctv.getOnvifUrl());
        notAuthorizedOnvifUrls.add(cctv.getOnvifUrl());
    }

    public static void removeUnauthorizedCctv(Cctv cctv) {
        notAuthorizedOnvifUrls.remove(cctv.getOnvifUrl());
    }
}
