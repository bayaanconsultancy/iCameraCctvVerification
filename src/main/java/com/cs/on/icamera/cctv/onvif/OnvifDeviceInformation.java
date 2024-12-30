package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnvifDeviceInformation {
    private static final Logger logger = LogManager.getLogger(OnvifDeviceInformation.class);
    private static final String ONVIF_GET_DEVICE_INFORMATION = """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:tds="http://www.onvif.org/ver10/device/wsdl">
               <soap:Header>
                    %s
               </soap:Header>
               <soap:Body>
                  <tds:GetDeviceInformation/>
               </soap:Body>
            </soap:Envelope>

            """;
    private OnvifDeviceInformation() {}
    public static void get(Cctv cctv) throws OnvifException {
        try {
            String xml = String.format(ONVIF_GET_DEVICE_INFORMATION, cctv.onvifDeviceInfo().header());
            logger.info("Getting device information for {} with {}", cctv.onvifDeviceInfo().deviceUrl(), xml);
            String response = HttpSoapClient.postXml(cctv.onvifDeviceInfo().deviceUrl(), xml);
            OnvifResponseParser.parseOnvifDeviceInformation(response);
        } catch (Exception e) {
            logger.error("Error getting device information for {} as {}", cctv, e.getMessage());
            throw new OnvifException(e);
        }
    }
}
