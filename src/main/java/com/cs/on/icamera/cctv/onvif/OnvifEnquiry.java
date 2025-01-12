package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.util.Counter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class OnvifEnquiry {
	private static final Logger logger = LogManager.getLogger(OnvifEnquiry.class);
	private static Counter counter;

	private OnvifEnquiry() {
	}

	/**
	 * This method retrieves and processes ONVIF details for each discovered CCTV.
	 */
	public static void enquire() {
		List<Cctv> devices = DataStore.getIdentifiedDevices();

		// Start the progress logger
		counter = new Counter(devices.size());
		// Iterate over all discovered CCTVs and get their ONVIF details
		devices.forEach(OnvifEnquiry::getOnvifDetails);
	}

	public static int progress() {
		return counter == null ? 0 : counter.getPercentage();
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
			// Set the exception in the Cctv object
			cctv.setError(e);

			// Log an error if there was a problem getting the ONVIF details
			logger.error("ERROR GETTING ONVIF DETAILS FOR {}", cctv, e);
		} finally {
			counter.increment();
		}
	}
}
