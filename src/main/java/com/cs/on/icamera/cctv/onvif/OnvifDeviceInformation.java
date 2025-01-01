package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.cs.on.icamera.cctv.onvif.OnvifSoapMessages.ONVIF_GET_DEVICE_INFORMATION;

public class OnvifDeviceInformation {
	private static final Logger logger = LogManager.getLogger(OnvifDeviceInformation.class);

	private OnvifDeviceInformation() {
	}

	/**
	 * Get the device information of the ONVIF device at the given URL, and set the
	 * make, model and serial number of the Cctv object.
	 *
	 * @param cctv the Cctv object to update
	 * @throws OnvifException if there is an error making the request
	 */
	public static void get(Cctv cctv) throws OnvifException {
		try {
			// Make the request to the ONVIF device
			String xml = String.format(ONVIF_GET_DEVICE_INFORMATION, cctv.onvifInfo().header());
			logger.info("Getting device information for {} with {}", cctv.onvifInfo().deviceUrl(), xml);

			// Parse the response and set the make, model and serial number of the Cctv
			// object
			List<String> deviceInfo = OnvifResponseParser
					.parseOnvifDeviceInformation(HttpSoapClient.postXml(cctv.onvifInfo().deviceUrl(), xml));

			cctv.setMake(deviceInfo.get(0));
			cctv.setModel(deviceInfo.get(1));
			cctv.setSerialNumber(deviceInfo.get(2));
		} catch (Exception e) {
			logger.error("Error getting device information for {} as {}", cctv, e.getMessage());
			throw new OnvifException(e);
		}
	}
}
