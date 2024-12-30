package com.cs.on.icamera.cctv.onvif;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class OnvifResponseParser {
    private OnvifResponseParser() {}

    private static final Logger logger = LogManager.getLogger(OnvifResponseParser.class);

    public static Object[] parseIpPort(String url) {
        try {
            URI uri = new URI(url);
            return new Object[]{uri.getHost(), uri.getPort()};
        } catch (URISyntaxException e) {
            logger.error("Error parsing ONVIF device service address in response {} as: {}", url, e.getMessage());
            return new Object[]{"0.0.0.0", 80};
        }
    }

    public static String parseOnvifAddress(String xmlContent) throws DocumentException {
        return DocumentHelper.parseText(xmlContent.trim()).getRootElement().element("Body").element("ProbeMatches").element("ProbeMatch").element("XAddrs").getTextTrim();
    }

    public static String parseMediaUrl(String xml) throws DocumentException {
        return DocumentHelper.parseText(xml).getRootElement().element("Body").element("GetCapabilitiesResponse").element("Capabilities").element("Media").element("XAddr").getTextTrim();
    }

    public static String parseDeviceUrl(String xml) throws DocumentException {
        return DocumentHelper.parseText(xml).getRootElement().element("Body").element("GetCapabilitiesResponse").element("Capabilities").element("Device").element("XAddr").getTextTrim();
    }

    public static String parseSystemDateAndTime(String xml) throws DocumentException {
        Element dateTime = DocumentHelper.parseText(xml).getRootElement().element("Body").element("GetSystemDateAndTimeResponse").element("SystemDateAndTime").element("UTCDateTime");

        Element date = dateTime.element("Date");
        int year = Integer.parseInt(date.element("Year").getTextTrim());
        int month = Integer.parseInt(date.element("Month").getTextTrim());
        int day = Integer.parseInt(date.element("Day").getTextTrim());

        Element time = dateTime.element("Time");
        int hour = Integer.parseInt(time.element("Hour").getTextTrim());
        int minute = Integer.parseInt(time.element("Minute").getTextTrim());
        int second = Integer.parseInt(time.element("Second").getTextTrim());

        return String.format("%04d-%02d-%02dT%02d:%02d:%02dZ", year, month, day, hour, minute, second);
    }

    public static List<String> parseProfiles(String xml) throws DocumentException, OnvifException {
        logger.info("Parsing profiles: {}", xml);
        Element body = DocumentHelper.parseText(xml).getRootElement().element("Body");
        checkForFault(body);
        return new ArrayList<>();
    }

    private static void checkForFault(Element body) throws OnvifException {
        if (body == null) {
            throw new OnvifException("No body in response");
        }
        Element fault = body.element("Fault");
        if (fault != null) {
            throw new OnvifException(fault.element("Reason").element("Text").getTextTrim());
        }
    }

    public static void parseOnvifDeviceInformation(String xml) {
        logger.info("Parsing device information: {}", xml);
    }
}
