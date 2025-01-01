package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.model.Profile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnvifResponseParser {
    private static final Logger logger = LogManager.getLogger(OnvifResponseParser.class);

    private OnvifResponseParser() {
    }

    public static Object[] parseIpPort(String url) {
        try {
            URI uri = new URI(url);
            return new Object[]{uri.getHost(), uri.getPort()};
        } catch (URISyntaxException e) {
            logger.error("Error parsing ONVIF device service address in response {} as: {}", url, e.getMessage());
            return new Object[]{"0.0.0.0", 80};
        }
    }

    public static String parseOnvifAddress(String xml) throws DocumentException {
        return DocumentHelper.parseText(xml.trim()).getRootElement().element("Body").element("ProbeMatches").element("ProbeMatch").element("XAddrs").getTextTrim();
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

    private static Element getBody(String xml) throws DocumentException, OnvifException {
        Element body = DocumentHelper.parseText(xml).getRootElement().element("Body");

        if (body == null) {
            throw new OnvifException("No body in response");
        }

        Element fault = body.element("Fault");
        if (fault != null) {
            throw new OnvifException(fault.element("Reason").element("Text").getTextTrim());
        }

        return body;
    }

    public static List<Profile> parseProfiles(String xml) throws DocumentException, OnvifException {
        logger.info("Parsing profiles: \n{}", xml);
        List<Profile> profiles = new ArrayList<>();
        List<Element> elements = getBody(xml).element("GetProfilesResponse").elements("Profiles");

        for (Element element : elements) {
            Profile profile = new Profile();
            profile.setName(element.element("Name").getTextTrim());
            profile.setToken(element.attributeValue("token"));

            Element vec = element.element("VideoEncoderConfiguration");
            if (vec != null) {
                profile.setEncoding(vec.element("Encoding").getTextTrim());
                profile.setResolutionWidth(Integer.parseInt(vec.element("Resolution").element("Width").getTextTrim()));
                profile.setResolutionHeight(Integer.parseInt(vec.element("Resolution").element("Height").getTextTrim()));
                profile.setQuality(Integer.parseInt(vec.element("Quality").getTextTrim()));
                Element rateControl = vec.element("RateControl");
                profile.setFrameRate(Integer.parseInt(rateControl.element("FrameRateLimit").getTextTrim()));
                profile.setEncodingInterval(Integer.parseInt(rateControl.element("EncodingInterval").getTextTrim()));
                profile.setBitrate(Integer.parseInt(rateControl.element("BitrateLimit").getTextTrim()));
            }

            profiles.add(profile);
        }
        return profiles;
    }



    public static String parseStreamUri(String xml) throws DocumentException, OnvifException {
        logger.info("Parsing stream URI: \n{}", xml);
        String uri = getBody(xml).element("GetStreamUriResponse").element("MediaUri").element("Uri").getTextTrim();
        return URLDecoder.decode(uri, StandardCharsets.UTF_8);

    }

    public static List<String> parseOnvifDeviceInformation(String xml) throws DocumentException, OnvifException {
        logger.info("Parsing device information: \n{}", xml);
        Element device = getBody(xml).element("GetDeviceInformationResponse");

        String manufacturer = device.element("Manufacturer").getTextTrim();
        String model = device.element("Model").getTextTrim();
        String serialNumber = device.element("SerialNumber").getTextTrim();

        return Arrays.asList(manufacturer, model, serialNumber);
    }
}
