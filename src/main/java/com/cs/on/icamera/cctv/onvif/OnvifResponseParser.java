package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.error.OnvifException;
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

	/**
	 * Extracts the first "normal" hostname-based URL from the response.
	 * <p>
	 * Some devices may respond with multiple URLs, such as a hostname and a
	 * link-local address. This method will extract the first URL that has a valid
	 * hostname and not a IPv6 address.
	 *
	 * @param xml the response from the ONVIF device
	 * @return the first hostname-based URL from the response
	 * @throws DocumentException if the XML cannot be parsed
	 */
	public static String parseOnvifAddress(String xml) throws DocumentException {
		/*
		 * Example: 1. <d:XAddrs>http://172.16.0.27/onvif/device_service
		 * http://[fe80::26b1:5ff:fe39:8d91]/onvif/device_service</d:XAddrs> 2.
		 * <d:XAddrs>http://192.168.0.113:8000/onvif/device_service</d:XAddrs>
		 */
		String[] urls = DocumentHelper.parseText(xml).getRootElement().element("Body").element("ProbeMatches")
				.element("ProbeMatch").element("XAddrs").getTextTrim().split("\\s+");

		for (String url : urls) {
			try {
				URI uri = new URI(url);
				if (uri.getHost() != null && !uri.getHost().contains(":")) {
					return url;
				}
			} catch (URISyntaxException e) {
				logger.warn("Error extracting hostname from URL {} as: {}", url, e.getMessage());
			}
		}
		return urls[0];
	}

	/**
	 * Parses the given XML string to extract the device and media URLs from the
	 * GetCapabilitiesResponse.
	 *
	 * @param xml the XML response from the ONVIF device
	 * @return an array containing the media and device URLs
	 * @throws DocumentException if the XML cannot be parsed
	 */
	public static String[] parseDeviceAndMediaUrl(String xml) throws DocumentException {
		logger.info("Parsing device and media URL from response: \n{}", xml);

		// Parse the XML to get the Capabilities element
		Element capabilities = DocumentHelper.parseText(xml).getRootElement().element("Body")
				.element("GetCapabilitiesResponse").element("Capabilities");

		// Extract the Media and Device URLs from the Capabilities element
		String deviceUrl = capabilities.element("Device").element("XAddr").getTextTrim();
		String mediaUrl = capabilities.element("Media").element("XAddr").getTextTrim();

		// Return the extracted URLs as an array
		return new String[] { deviceUrl, mediaUrl };
	}

	/**
	 * Given a GetSystemDateAndTimeResponse ONVIF response, extracts the
	 * SystemDateAndTime element text content. The SystemDateAndTime element is in
	 * the format
	 * 
	 * <pre>
	 *     &lt;SystemDateAndTime&gt;
	 *         &lt;UTCDateTime&gt;
	 *             &lt;Date&gt;
	 *                 &lt;Year&gt;2019&lt;/Year&gt;
	 *                 &lt;Month&gt;11&lt;/Month&gt;
	 *                 &lt;Day&gt;26&lt;/Day&gt;
	 *             &lt;/Date&gt;
	 *             &lt;Time&gt;
	 *                 &lt;Hour&gt;11&lt;/Hour&gt;
	 *                 &lt;Minute&gt;11&lt;/Minute&gt;
	 *                 &lt;Second&gt;11&lt;/Second&gt;
	 *             &lt;/Time&gt;
	 *         &lt;/UTCDateTime&gt;
	 *     &lt;/SystemDateAndTime&gt;
	 * </pre>
	 * 
	 * This method parses this XML and returns the date and time in the ISO 8601
	 * format "yyyy-MM-dd'T'HH:mm:ss'Z'".
	 *
	 * @param xml the ONVIF response to parse
	 * @return the SystemDateAndTime element text content
	 * @throws DocumentException if the XML can not be parsed
	 */
	public static String parseSystemDateAndTime(String xml) throws DocumentException {
		logger.info("Parsing system date and time from response: \n{}", xml);
		// Parse the XML document and navigate to the UTCDateTime element
		Element dateTime = DocumentHelper.parseText(xml).getRootElement().element("Body")
				.element("GetSystemDateAndTimeResponse").element("SystemDateAndTime").element("UTCDateTime");

		// Extract the date and time from the UTCDateTime element
		Element date = dateTime.element("Date");
		int year = Integer.parseInt(date.element("Year").getTextTrim());
		int month = Integer.parseInt(date.element("Month").getTextTrim());
		int day = Integer.parseInt(date.element("Day").getTextTrim());

		Element time = dateTime.element("Time");
		int hour = Integer.parseInt(time.element("Hour").getTextTrim());
		int minute = Integer.parseInt(time.element("Minute").getTextTrim());
		int second = Integer.parseInt(time.element("Second").getTextTrim());

		// Format the date and time in the ISO 8601 format
		return String.format("%04d-%02d-%02dT%02d:%02d:%02dZ", year, month, day, hour, minute, second);
	}

	/**
	 * Parses the given XML string to extract the SOAP body element.
	 *
	 * @param xml the XML string to parse
	 * @return the SOAP body element
	 * @throws DocumentException if the XML cannot be parsed
	 * @throws OnvifException    if the response does not contain a body, or if the
	 *                           body contains a Fault element
	 */
	private static Element getBody(String xml) throws DocumentException, OnvifException {
		Element body = DocumentHelper.parseText(xml).getRootElement().element("Body");

		if (body == null) {
			throw new OnvifException("No body in response");
		}

		Element fault = body.element("Fault");
		if (fault != null) {
			// If there is a Fault element, throw an OnvifException with the error text
			throw new OnvifException(fault.element("Reason").element("Text").getTextTrim());
		}

		return body;
	}

	/**
	 * Parses the given XML string to extract the list of profiles in the
	 * GetProfilesResponse.
	 *
	 * @param xml the XML string to parse
	 * @return the list of profiles
	 * @throws DocumentException if the XML cannot be parsed
	 * @throws OnvifException    if the response does not contain a body, or if the
	 *                           body contains a Fault element
	 */
	public static List<Profile> parseProfiles(String xml) throws DocumentException, OnvifException {
		logger.info("Parsing profiles from response: \n{}", xml);
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
				profile.setResolutionHeight(
						Integer.parseInt(vec.element("Resolution").element("Height").getTextTrim()));
				profile.setQuality(Float.parseFloat(vec.element("Quality").getTextTrim()));
				Element rateControl = vec.element("RateControl");
				profile.setFrameRate(Integer.parseInt(rateControl.element("FrameRateLimit").getTextTrim()));
				profile.setEncodingInterval(Integer.parseInt(rateControl.element("EncodingInterval").getTextTrim()));
				profile.setBitrate(Integer.parseInt(rateControl.element("BitrateLimit").getTextTrim()));
			}

			profiles.add(profile);
		}
		return profiles;
	}

	/**
	 * Given a GetStreamUriResponse ONVIF response, extracts the stream URI from the
	 * MediaUri element.
	 * 
	 * @param xml the ONVIF response to parse
	 * @return the stream URI
	 * @throws DocumentException if the XML cannot be parsed
	 * @throws OnvifException    if the response does not contain a body, or if the
	 *                           body contains a Fault element
	 */
	public static String parseStreamUri(String xml) throws DocumentException, OnvifException {
		logger.info("Parsing stream URI from response: \n{}", xml);
		return URLDecoder.decode(
				getBody(xml).element("GetStreamUriResponse").element("MediaUri").element("Uri").getTextTrim(),
				StandardCharsets.UTF_8);
	}

	/**
	 * Given a GetDeviceInformationResponse ONVIF response, extracts the device
	 * information.
	 * 
	 * @param xml the ONVIF response to parse
	 * @return a list of strings containing the device information: manufacturer,
	 *         model, and serial number
	 * @throws DocumentException if the XML cannot be parsed
	 * @throws OnvifException    if the response does not contain a body, or if the
	 *                           body contains a Fault element
	 */
	public static List<String> parseOnvifDeviceInformation(String xml) throws DocumentException, OnvifException {
		logger.info("Parsing device information from response: \n{}", xml);
		Element device = getBody(xml).element("GetDeviceInformationResponse");

		String manufacturer = device.element("Manufacturer").getTextTrim();
		String model = device.element("Model").getTextTrim();
		String serialNumber = device.element("SerialNumber").getTextTrim();

		return Arrays.asList(manufacturer, model, serialNumber);
	}

	public static boolean isOnvifDevice(String response) {
		logger.info("Confirming ONVIF device: \n{}", response);
		return response != null && response.contains("GetCapabilitiesResponse");
	}
}
