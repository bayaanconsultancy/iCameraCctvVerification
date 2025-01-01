package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.model.Cctv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnvifEnquiry {
	private static final Logger logger = LogManager.getLogger(OnvifEnquiry.class);

	private OnvifEnquiry() {
	}

	/**
	 * This method retrieves and processes ONVIF details for each discovered CCTV.
	 */
	public static void enquire() {
		// Iterate over all discovered CCTVs and get their ONVIF details
		DataStore.getDiscoveredCctvs().forEach(OnvifEnquiry::getOnvifDetails);
	}

	/**
	 * This method retrieves and processes ONVIF details for a given CCTV.
	 * 
	 * @param cctv The CCTV to get the ONVIF details for.
	 */
	public static void getOnvifDetails(Cctv cctv) {
		try {
			// Get the ONVIF capabilities for the CCTV
			OnvifCapabilities.get(cctv);

			// Get the system date and time for the CCTV
			OnvifSystemDateAndTime.get(cctv);

			// Get the profiles for the CCTV
			OnvifProfiles.get(cctv);

			// Get the device information for the CCTV
			OnvifDeviceInformation.get(cctv);

			logger.info("Got ONVIF details for {}", cctv);
		} catch (Exception e) {
			// Log an error if there was a problem getting the ONVIF details
			logger.error("Error getting ONVIF details for {}", cctv, e);
		}
	}
}
