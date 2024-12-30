package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnvifCapabilities {
	private OnvifCapabilities() {
	}

	private static final Logger logger = LogManager.getLogger(OnvifCapabilities.class);
	private static final String ONVIF_GET_CAPABILITIES = """
			<?xml version="1.0" encoding="UTF-8"?>
			<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:wsdl="http://www.onvif.org/ver10/device/wsdl">
			   <soap:Header/>
			   <soap:Body>
			      <wsdl:GetCapabilities>
			         <wsdl:Category>All</wsdl:Category>
			      </wsdl:GetCapabilities>
			   </soap:Body>
			</soap:Envelope>""";

	/**
	 * Retrieve the capabilities of the ONVIF device at the given URL, and add the
	 * media and device URLs to the device info.
	 * 
	 * @param cctv the ONVIF device to retrieve the capabilities for
	 * @throws OnvifException if there is an error making the request
	 */
	public static void get(Cctv cctv) throws OnvifException {
		try {
			// Make the request to the ONVIF device
			String response = HttpSoapClient.postXml(cctv.getOnvifDeviceUrl(), ONVIF_GET_CAPABILITIES);

			// Parse the response and set the media and device URLs in the device info
			String mediaUrl = OnvifResponseParser.parseMediaUrl(response);
			String deviceUrl = OnvifResponseParser.parseDeviceUrl(response);
			cctv.onvifDeviceInfo().setDeviceUrl(deviceUrl).setMediaUrl(mediaUrl);
		} catch (Exception e) {
			logger.error("Error getting capabilities for {} as {}", cctv, e.getMessage());
			throw new OnvifException(e);
		}
	}
}
