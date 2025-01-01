package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.cs.on.icamera.cctv.onvif.OnvifSoapMessages.ONVIF_GET_DATETIME;

public class OnvifSystemDateAndTime {
	private OnvifSystemDateAndTime() {
	}

	private static final Logger logger = LogManager.getLogger(OnvifSystemDateAndTime.class);

	/**
	 * Sends a SOAP request to the ONVIF device to retrieve its current system date
	 * and time. The response is parsed and the system date and time is stored in
	 * the given Cctv object.
	 * 
	 * @param cctv The Cctv object to store the system date and time in
	 * @throws OnvifException if the SOAP request fails or the response could not be
	 *                        parsed
	 */
	public static void get(Cctv cctv) throws OnvifException {
		try {
			// Send the SOAP request and get the response
			String response = HttpSoapClient.postXml(cctv.getOnvifUrl(), ONVIF_GET_DATETIME);

			// Parse the response and store the system date and time in the Cctv object
			cctv.onvifInfo().setSystemDateAndTime(OnvifResponseParser.parseSystemDateAndTime(response));
		} catch (Exception e) {
			logger.error("Error getting system date and time for {} as {}", cctv, e.getMessage());
			throw new OnvifException(e);
		}
	}
}
