package com.tcs.ion.icamera.cctv.onvif;

import com.tcs.ion.icamera.cctv.error.OnvifException;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.tcs.ion.icamera.cctv.onvif.OnvifSoapMessages.ONVIF_GET_DEVICE_INFORMATION;

/**
 * A utility class to retrieve and set device information of an ONVIF-enabled CCTV.
 * This includes fetching details such as make, model, and serial number of the device
 * and updating the corresponding {@code Cctv} object.
 * <p>
 * This class is intended for use with ONVIF-compliant devices and relies on SOAP-based communication.
 */
public class OnvifDeviceInformation {
    private static final Logger logger = LogManager.getLogger(OnvifDeviceInformation.class);

    private OnvifDeviceInformation() {
    }

    /**
     * Retrieves device information from an ONVIF-enabled CCTV device, including its make,
     * model, and serial number, and updates the corresponding {@code Cctv} object.
     *
     * @param cctv the {@code Cctv} object representing the ONVIF device for which device
     *             information needs to be fetched and updated.
     * @throws OnvifException if an error occurs while retrieving or processing the device information.
     */
    public static void get(Cctv cctv) throws OnvifException {
        try {
            // Make the request to the ONVIF device
            String xml = String.format(ONVIF_GET_DEVICE_INFORMATION, cctv.onvifInfo().header());
            logger.info("Getting device information for {} with \n{}", cctv.onvifInfo().deviceUrl(), xml);

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
