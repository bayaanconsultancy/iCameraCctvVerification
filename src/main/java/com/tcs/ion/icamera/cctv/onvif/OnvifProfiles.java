package com.tcs.ion.icamera.cctv.onvif;

import com.tcs.ion.icamera.cctv.error.OnvifException;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.model.Profile;
import com.tcs.ion.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.tcs.ion.icamera.cctv.onvif.OnvifSoapMessages.ONVIF_GET_PROFILES;
import static com.tcs.ion.icamera.cctv.onvif.OnvifSoapMessages.ONVIF_GET_STREAM_URI;

/**
 * The OnvifProfiles class provides functionality to retrieve and process
 * ONVIF profiles for a given CCTV device. It includes methods to fetch
 * profiles from the media URL of the CCTV's ONVIF information and to parse
 * the stream URI for each profile.
 * <p>
 * This class handles the communication with the device through HTTP SOAP
 * requests, processes the responses, and updates the CCTV object with the
 * retrieved profile and stream URI data.
 * <p>
 * This is a utility class and cannot be instantiated.
 * <p>
 * Methods:
 * - get(Cctv cctv): Fetches ONVIF profiles and their associated stream URIs
 *   for the specified CCTV device. It updates the provided CCTV object with
 *   the retrieved data. Throws OnvifException in case of any errors during
 *   the process.
 * <p>
 * Important Notes:
 * - The method makes use of the CCTV's ONVIF information to construct and
 *   send SOAP-based requests to the media URL.
 * - The responses are parsed to retrieve the profile and stream URI details.
 * - Any encountered exceptions are logged and rethrown as OnvifException.
 */
public class OnvifProfiles {
    private static final Logger logger = LogManager.getLogger(OnvifProfiles.class);

    private OnvifProfiles() {
    }

    /**
     * Fetches ONVIF profiles and their corresponding stream URIs for the specified CCTV device.
     * Updates the provided Cctv object with the retrieved profile and URI data.
     *
     * @param cctv The Cctv object representing the CCTV device to retrieve profiles and stream URIs for.
     * @throws OnvifException if an error occurs while communicating with the CCTV device or processing responses.
     */
    public static void get(Cctv cctv) throws OnvifException {
        try {
            // Get the profiles from the media URL
            String profileXml = String.format(ONVIF_GET_PROFILES, cctv.onvifInfo().header());
            logger.info("Getting profiles from {} with: \n{}", cctv.onvifInfo().mediaUrl(), profileXml);

            // Parse the response and store the profiles in the Cctv object
            cctv.setProfiles(
                    OnvifResponseParser.parseProfiles(HttpSoapClient.postXml(cctv.onvifInfo().mediaUrl(), profileXml)));

            // Get the stream URI for each profile
            for (Profile profile : cctv.getProfiles()) {
                String streamXml = String.format(ONVIF_GET_STREAM_URI, cctv.onvifInfo().header(), profile.token());
                logger.info("Getting stream URI from {} for {} with \n{}", cctv.onvifInfo().mediaUrl(), profile.name(), streamXml);

                // Parse the response and store the stream URI in the profile
                profile.setStreamUri(OnvifResponseParser
                        .parseStreamUri(HttpSoapClient.postXml(cctv.onvifInfo().mediaUrl(), streamXml)));

                cctv.setIpPort(profile.streamUri());
            }
        } catch (Exception e) {
            logger.error("Error getting profiles for {} as {}", cctv, e.getMessage());
            throw new OnvifException(e);
        }
    }
}
