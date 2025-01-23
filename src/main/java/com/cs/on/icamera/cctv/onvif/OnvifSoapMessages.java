package com.cs.on.icamera.cctv.onvif;

import java.util.UUID;

public class OnvifSoapMessages {
    public static final byte[] WS_DISCOVERY_PROBE = """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope
                    xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
                    xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing"
                    xmlns:tns="http://schemas.xmlsoap.org/ws/2005/04/discovery">
                <soap:Header>
                    <wsa:Action>
                        http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe
                    </wsa:Action>
                    <wsa:MessageID>
                        uuid:%s
                    </wsa:MessageID>
                    <wsa:To>
                        urn:schemas-xmlsoap-org:ws:2005:04:discovery
                    </wsa:To>
                </soap:Header>
                <soap:Body>
                    <tns:Probe>
                    </tns:Probe>
                </soap:Body>
            </soap:Envelope>""".formatted(UUID.randomUUID().toString()).getBytes();

    public static final String ONVIF_GET_CAPABILITIES = """
            <?xml version="1.0" encoding="UTF-8"?>
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope" xmlns:tds="http://www.onvif.org/ver10/device/wsdl">
               <SOAP-ENV:Header/>
               <SOAP-ENV:Body>
                  <tds:GetCapabilities>
                     <tds:Category>All</tds:Category>
                  </tds:GetCapabilities>
               </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>""";

    public static final String ONVIF_GET_DATETIME = """
            <?xml version="1.0" encoding="UTF-8"?>
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope" xmlns:tds="http://www.onvif.org/ver10/device/wsdl">
               <SOAP-ENV:Header/>
               <SOAP-ENV:Body>
                  <tds:GetSystemDateAndTime/>
               </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>""";

    public static final String ONVIF_GET_DEVICE_INFORMATION = """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:tds="http://www.onvif.org/ver10/device/wsdl">
               <soap:Header>
                    %s
               </soap:Header>
               <soap:Body>
                  <tds:GetDeviceInformation/>
               </soap:Body>
            </soap:Envelope>""";

    public static final String ONVIF_GET_PROFILES = """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:trt="http://www.onvif.org/ver10/media/wsdl">
               <soap:Header>
                    %s
               </soap:Header>
               <soap:Body>
                  <trt:GetProfiles/>
               </soap:Body>
            </soap:Envelope>""";

    public static final String ONVIF_GET_STREAM_URI = """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:trt="http://www.onvif.org/ver10/media/wsdl" xmlns:tt="http://www.onvif.org/ver10/schema">
               <soap:Header>
                    %s
               </soap:Header>
               <soap:Body>
                  <trt:GetStreamUri>
                     <trt:StreamSetup>
                        <tt:Stream>RTP-Unicast</tt:Stream>
                        <tt:Transport>
                           <tt:Protocol>UDP</tt:Protocol>
                        </tt:Transport>
                     </trt:StreamSetup>
                     <trt:ProfileToken>%s</trt:ProfileToken>
                  </trt:GetStreamUri>
               </soap:Body>
            </soap:Envelope>""";

    public static final String ONVIF_COMPATIBILITY = """
            <?xml version="1.0" encoding="utf-8"?>
            <Envelope xmlns="http://www.w3.org/2003/05/soap-envelope">
            	<Body>
            		<GetCapabilities xmlns="http://www.onvif.org/ver10/device/wsdl"/>
            	</Body>
            </Envelope>""";

    private OnvifSoapMessages() {
    }
}
