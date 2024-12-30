package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnvifSystemDateAndTime {
	private OnvifSystemDateAndTime() {
	}

	private static final Logger logger = LogManager.getLogger(OnvifSystemDateAndTime.class);

	private static final String ONVIF_GET_DATETIME = """
			<?xml version="1.0" encoding="UTF-8"?>
			<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:wsdl="http://www.onvif.org/ver10/device/wsdl">
			   <soap:Header/>
			   <soap:Body>
			      <wsdl:GetSystemDateAndTime/>
			   </soap:Body>
			</soap:Envelope>""";

	/**
	 * Sends a SOAP request to the ONVIF device to retrieve its current system date
	 * and time. The response is parsed and the system date and time is stored in
	 * the given Cctv object.
	 * 
	 * @param cctv The Cctv object to store the system date and time in
	 * @throws OnvifException if the SOAP request fails or the response could not be
	 *                        parsed
	 */
	public static void getSystemDateAndTime(Cctv cctv) throws OnvifException {
		try {
			// Send the SOAP request and get the response
			String response = HttpSoapClient.postXml(cctv.getOnvifDeviceUrl(), ONVIF_GET_DATETIME);

			// Parse the response and store the system date and time in the Cctv object
			cctv.onvifDeviceInfo().setSystemDateAndTime(OnvifResponseParser.parseSystemDateAndTime(response));
		} catch (Exception e) {
			logger.error("Error getting system date and time for {} as {}", cctv, e.getMessage());
			throw new OnvifException(e);
		}
	}
}
