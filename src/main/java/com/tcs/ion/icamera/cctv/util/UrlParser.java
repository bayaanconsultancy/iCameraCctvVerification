package com.tcs.ion.icamera.cctv.util;

import com.tcs.ion.icamera.cctv.error.OnvifException;
import com.tcs.ion.icamera.cctv.error.VerificationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlParser {
    private static final Logger logger = LogManager.getLogger(UrlParser.class);

    private UrlParser() {
    }

    /**
     * Given a URL, return the hostname component of the URL.
     *
     * @param url the URL to extract the hostname from
     * @return the hostname component of the URL
     * @throws OnvifException if the URL can not be parsed
     */
    public static String getHostname(String url) throws OnvifException {
        try {
            return new URI(url).getHost();
        } catch (URISyntaxException e) {
            logger.error("Error extracting hostname from URL {} as: {}", url, e.getMessage());
            throw new OnvifException(e);
        }
    }

    /**
     * Given a URL, extract the port number from the URL.
     *
     * @param url the URL to extract the port from
     * @return the port number, or 443 if the scheme is HTTPS, 554 if the scheme is
     * RTSP, or 80 if the scheme is anything else
     * @throws OnvifException if the URL can not be parsed
     */
    public static int getPort(String url) throws OnvifException {
        try {
            URI uri = new URI(url);
            int port = uri.getPort();

            // If no port is specified in the URL, use a default port based on the
            // scheme
            return port == -1 ? switch (uri.getScheme()) {
                case "https" -> 443; // Default HTTPS port
                case "rtsp" -> 554; // Default RTSP port
                default -> 80; // Default HTTP port
            } : port;

        } catch (URISyntaxException e) {
            logger.error("Error extracting port from URL {} as: {}", url, e.getMessage());
            throw new OnvifException(e);
        }
    }

    public static String addCredentialsToRtspUrl(String rtspUrl, String username, String password) throws URISyntaxException, VerificationException {
        RtspUrlParser.RtspUrl url = RtspUrlParser.parseRTSPUrl(rtspUrl);

        url.setUsername(username);
        url.setPassword(password);

        return url.getRtspUrl();
    }

    public static String getRtspUrlForFfmpeg(String rtspUrl, String username, String password) throws URISyntaxException, VerificationException {
        RtspUrlParser.RtspUrl url = RtspUrlParser.parseRTSPUrl(rtspUrl);
        logger.info("Parts of URI is: Scheme {} Username {} Password {} Host {} Port {} Path {} Query {} Fragment {}", url.getScheme(), url.getUsername(), url.getPassword(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getFragment());

        if (url.hasCredential() && (!url.getUsername().equals(username) || !url.getPassword().equals(password))) {
            throw new VerificationException("Mismatched username or password in RTSP URL.");
        }

        url.setUsername(username);
        url.setPassword(password);
        return url.getRtspUrl();
    }


    public static String getOnvifDeviceServiceUrl(String ip, int port) {
        return "http://" + ip + ":" + port + "/onvif/device_service";
    }
}
