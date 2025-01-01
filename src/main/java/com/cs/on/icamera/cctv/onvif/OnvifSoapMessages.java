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
			<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:wsdl="http://www.onvif.org/ver10/device/wsdl">
			   <soap:Header/>
			   <soap:Body>
			      <wsdl:GetCapabilities>
			         <wsdl:Category>All</wsdl:Category>
			      </wsdl:GetCapabilities>
			   </soap:Body>
			</soap:Envelope>""";

	public static final String ONVIF_GET_DATETIME = """
			<?xml version="1.0" encoding="UTF-8"?>
			<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:wsdl="http://www.onvif.org/ver10/device/wsdl">
			   <soap:Header/>
			   <soap:Body>
			      <wsdl:GetSystemDateAndTime/>
			   </soap:Body>
			</soap:Envelope>""";

	public static final String ONVIF_GET_DEVICE_INFORMATION = """
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

	public static final String ONVIF_GET_PROFILES = """
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

	public static final String ONVIF_GET_STREAM_URI = """
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

	private OnvifSoapMessages() {
	}
}
