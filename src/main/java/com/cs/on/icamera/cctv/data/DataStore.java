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

	private static final Map<String, Cctv> discoveredCctvs = new HashMap<>();

	public static void addDiscoveredCctv(Cctv cctv) {
		discoveredCctvs.put(cctv.getOnvifDeviceUrl(), cctv);
	}

	public static int getDiscoveredCctvCount() {
		return discoveredCctvs.size();
	}

	public static List<Cctv> getDiscoveredCctvs() {
		return new ArrayList<>(discoveredCctvs.values());
	}

	public static void printDiscoveredCctvs() {
		if (discoveredCctvs.isEmpty()) {
			logger.info("No CCTVs discovered.");
		} else {
			logger.info("Discovered CCTVs: ");
			for (Cctv cctv : discoveredCctvs.values()) {
				logger.info(cctv.toString());
			}
		}
	}

	public static void setUsernamePasswordForDiscoveredCctvs(String username, String password) {
		for (Cctv cctv : discoveredCctvs.values()) {
			cctv.setUsername(username);
			cctv.setPassword(password);
		}
	}
}
