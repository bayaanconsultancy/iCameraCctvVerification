package com.tcs.ion.icamera.cctv.onvif;

import com.tcs.ion.icamera.cctv.error.OnvifException;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.tcs.ion.icamera.cctv.onvif.OnvifSoapMessages.ONVIF_GET_CAPABILITIES;

/**
 * The OnvifCapabilities class provides functionality to retrieve and process
 * ONVIF capabilities from a CCTV device. This includes requesting capabilities
 * from the ONVIF device, parsing the response, and updating the ONVIF information
 * of the CCTV device with the parsed data.
 * <p>
 * This class contains static methods and cannot be instantiated.
 */
public class OnvifCapabilities {
    private static final Logger logger = LogManager.getLogger(OnvifCapabilities.class);

    private OnvifCapabilities() {
    }

    /**
     * Sends a request to the ONVIF device to retrieve its capabilities, parses the response,
     * and updates the provided CCTV device with the parsed device and media URLs.
     *
     * @param cctv the CCTV device for which the ONVIF capabilities are being retrieved
     * @throws OnvifException if an error occurs during the capability retrieval or parsing process
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
