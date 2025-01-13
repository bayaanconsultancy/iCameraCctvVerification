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
	private static final Map<String, Cctv> identifiedCctvs = new HashMap<>();
	private static List<Cctv> cctvsToVerify;
	private static int discoveredCctvCount;
	private static int scannedCctvCount;

	private DataStore() {
	}

	public static void addDiscoveredCctv(Cctv cctv) {
		if (identifiedCctvs.put(cctv.getOnvifUrl(), cctv) == null)
			discoveredCctvCount++;
	}

	public static int getDiscoveredCctvCount() {
		return discoveredCctvCount;
	}

	public static void addScannedCctv(Cctv cctv) {
		if (identifiedCctvs.put(cctv.getOnvifUrl(), cctv) == null)
			scannedCctvCount++;
	}

	public static int getScannedCctvCount() {
		return scannedCctvCount;
	}

	public static List<Cctv> getIdentifiedCctvs() {
		return new ArrayList<>(identifiedCctvs.values());
	}

	public static void printIdentifiedCctvs() {
		if (identifiedCctvs.isEmpty()) {
			logger.info("No CCTVs discovered.");
		} else {
			logger.info("Discovered CCTVs: ");
			for (Cctv cctv : identifiedCctvs.values()) {
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

	public static int getOnvifErrorCount() {
		return identifiedCctvs.values().stream().filter(cctv -> !cctv.success()).toList().size();
	}

	public static void setCctvsToVerify(List<Cctv> cctvs) {
		cctvsToVerify = cctvs;
	}

	public static List<Cctv> getCctvsToVerify() {
		return cctvsToVerify;
	}

	public static List<Cctv> getCorrectCctvs() {
		return cctvsToVerify.stream().filter(Cctv::success).toList();
	}
}
