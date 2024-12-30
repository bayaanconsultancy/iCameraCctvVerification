package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnvifProfiles {
    private static final Logger logger = LogManager.getLogger(OnvifProfiles.class);
    private static final String ONVIF_GET_PROFILES = """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:wsdl="http://www.onvif.org/ver10/device/wsdl">
               <soap:Header>
                    %s
               </soap:Header>
               <soap:Body>
                  <wsdl:GetProfiles/>
               </soap:Body>
            </soap:Envelope>
            """;
    private OnvifProfiles() {
    }

    public static void get(Cctv cctv) throws OnvifException {
        try {
            String xml = String.format(ONVIF_GET_PROFILES, cctv.onvifDeviceInfo().header());
            logger.info("Getting profiles for {} with {}", cctv.getOnvifDeviceUrl(), xml);

            String response = HttpSoapClient.postXml(cctv.onvifDeviceInfo().mediaUrl(), xml);
            OnvifResponseParser.parseProfiles(response);
        } catch (Exception e) {
            logger.error("Error getting profiles for {} as {}", cctv, e.getMessage());
            throw new OnvifException(e);
        }
    }
}
