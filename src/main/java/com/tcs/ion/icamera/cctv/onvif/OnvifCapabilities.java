package com.tcs.ion.icamera.cctv.onvif;

import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.model.OnvifAuth;
import com.tcs.ion.icamera.cctv.util.HttpRequest;
import jakarta.xml.soap.SOAPException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.List;

import static com.tcs.ion.icamera.cctv.util.Token.buildSoapRequest;

public class OnvifCapabilities {
    private static final Logger logger = LogManager.getLogger(OnvifCapabilities.class);

    private static String getXmlEnvelope(OnvifAuth auth, String body) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope"
                                  xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
                                  xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"
                                  xmlns:tds="http://www.onvif.org/ver10/device/wsdl">
                    <soapenv:Header>
                        <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
                                       xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
                            <wsu:Timestamp>
                                <wsu:Created>%s</wsu:Created>
                                <wsu:Expires>%s</wsu:Expires>
                            </wsu:Timestamp>
                            <wsse:UsernameToken>
                                <wsse:Username>%s</wsse:Username>
                                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest">
                                    %s
                                </wsse:Password>
                                <wsse:Nonce EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary">
                                    %s
                                </wsse:Nonce>
                                <wsu:Created>%s</wsu:Created>
                            </wsse:UsernameToken>
                        </wsse:Security>
                    </soapenv:Header>
                    <soapenv:Body>
                        %s
                    </soapenv:Body>
                </soapenv:Envelope>""".formatted(auth.getCreated(), auth.getExpires(), auth.getUsername(), auth.getPassword(), auth.getNonce(), auth.getCreated(), body);
    }

    public static void getCapabilitiesUrl(Cctv cctv, OnvifAuth auth) throws OnvifException, DocumentException, SOAPException, IOException {
        String bodyCapabilitiesAll = "<tds:GetCapabilities><tds:Category>All</tds:Category></tds:GetCapabilities>";
        String response = HttpRequest.postXml(cctv.getOnvifAddress(), getXmlEnvelope(auth, bodyCapabilitiesAll));
        String mediaUrl = OnvifResponseParser.parseMediaUrl(response);
        String deviceUrl = OnvifResponseParser.parseDeviceUrl(response);
        logger.info("MediaUrl: {}", mediaUrl);
        logger.info("DeviceUrl: {}", deviceUrl);
        getProfiles(mediaUrl, auth);
    }

    public static void getProfiles(String mediaUrl, OnvifAuth auth) throws OnvifException, SOAPException, IOException {
        String bodyProfiles = "<GetProfiles xmlns=\"http://www.onvif.org/ver10/media/wsdl\"/>";
        String response = HttpRequest.sendSoapRequest(mediaUrl, buildSoapRequest(auth ));
        List<String> profiles = OnvifResponseParser.parseProfiles(response);
        logger.info("Profiles: " + profiles);
    }

}
