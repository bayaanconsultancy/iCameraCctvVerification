package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.model.Profile;
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
    private static final String ONVIF_GET_STREAM_URI = """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:wsdl="http://www.onvif.org/ver10/device/wsdl">
               <soap:Header>
                    %s
               </soap:Header>
               <soap:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
                  <wsdl:GetStreamUri xmlns="http://www.onvif.org/ver10/media/wsdl">
                     <StreamSetup xmlns="http://www.onvif.org/ver10/schema">
                        <Stream xmlns="http://www.onvif.org/ver10/schema">RTP-Unicast</Stream>
                        <Transport xmlns="http://www.onvif.org/ver10/schema">
                           <Protocol>UDP</Protocol>
                        </Transport>
                     </StreamSetup>
                     <ProfileToken>%s</ProfileToken>
                  </wsdl:GetStreamUri>
               </soap:Body>
            </soap:Envelope>
            """;

    private OnvifProfiles() {
    }

    public static void get(Cctv cctv) throws OnvifException {
        try {
            String profileXml = String.format(ONVIF_GET_PROFILES, cctv.onvifInfo().header());
            logger.info("Getting profiles for {} with: \n{}", cctv.getOnvifUrl(), profileXml);

            cctv.setProfiles(OnvifResponseParser.parseProfiles(HttpSoapClient.postXml(cctv.onvifInfo().mediaUrl(), profileXml)));

            for (Profile profile : cctv.getProfiles()) {
                String streamXml = String.format(ONVIF_GET_STREAM_URI, cctv.onvifInfo().header(), profile.token());
                logger.info("Getting stream URI for {} with \n{}", profile.name(), streamXml);

                profile.setStreamUri(OnvifResponseParser.parseStreamUri(HttpSoapClient.postXml(cctv.onvifInfo().mediaUrl(), streamXml)));
            }
        } catch (Exception e) {
            logger.error("Error getting profiles for {} as {}", cctv, e.getMessage());
            throw new OnvifException(e);
        }
    }
}
