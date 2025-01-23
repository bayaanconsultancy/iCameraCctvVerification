package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.error.OnvifException;
import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.cs.on.icamera.cctv.onvif.OnvifSoapMessages.ONVIF_GET_CAPABILITIES;

public class OnvifCapabilities {
    private static final Logger logger = LogManager.getLogger(OnvifCapabilities.class);

    private OnvifCapabilities() {
    }

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
            String response = HttpSoapClient.postXml(cctv.getOnvifUrl(), ONVIF_GET_CAPABILITIES);

            // Parse the response and set the media and device URLs in the device info
            String[] urls = OnvifResponseParser.parseDeviceAndMediaUrl(response);
            cctv.onvifInfo().setDeviceUrl(urls[0]).setMediaUrl(urls[1]);
        } catch (Exception e) {
            logger.error("Error getting capabilities for {} as {}", cctv, e.getMessage());
            throw new OnvifException(e);
        }
    }
}
