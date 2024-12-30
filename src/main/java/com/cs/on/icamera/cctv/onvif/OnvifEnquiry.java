package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.model.Cctv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnvifEnquiry {

	private static final Logger logger = LogManager.getLogger(OnvifEnquiry.class);

	public static void enquire() {
		DataStore.getDiscoveredCctvs().forEach(OnvifEnquiry::getOnvifDetails);
	}

	public static void getOnvifDetails(Cctv cctv) {
		try {
			OnvifCapabilities.getCapabilities(cctv);
			OnvifSystemDateAndTime.getSystemDateAndTime(cctv);
			logger.info("Got ONVIF details for {}", cctv);
		} catch (Exception e) {
			logger.error("Error getting ONVIF details for {}", cctv, e);
		}
	}
}
