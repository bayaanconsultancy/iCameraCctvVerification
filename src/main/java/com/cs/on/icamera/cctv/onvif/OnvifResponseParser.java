package com.cs.on.icamera.cctv.onvif;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class OnvifResponseParser {
	private static final Logger logger = LogManager.getLogger(OnvifResponseParser.class);

	public static Object[] parseIpPort(String url) {
		try {
			URI uri = new URI(url);
			return new Object[] { uri.getHost(), uri.getPort() };
		} catch (URISyntaxException e) {
			logger.error("Error parsing ONVIF device service address in response {} as: {}", url, e.getMessage());
			return new Object[] { "0.0.0.0", 0 };
		}
	}

	public static String parseOnvifAddress(String xmlContent) throws DocumentException {
		return DocumentHelper.parseText(xmlContent.trim()).getRootElement().element("Body").element("ProbeMatches")
				.element("ProbeMatch").element("XAddrs").getTextTrim();
	}

	public static String parseMediaUrl(String xml) throws DocumentException {
		return DocumentHelper.parseText(xml).getRootElement().element("Body").element("GetCapabilitiesResponse")
				.element("Capabilities").element("Media").element("XAddr").getTextTrim();
	}

	public static String parseDeviceUrl(String xml) throws DocumentException {
		return DocumentHelper.parseText(xml).getRootElement().element("Body").element("GetCapabilitiesResponse")
				.element("Capabilities").element("Device").element("XAddr").getTextTrim();
	}

	public static List<String> parseProfiles(String response) {
		return null;
	}
}
