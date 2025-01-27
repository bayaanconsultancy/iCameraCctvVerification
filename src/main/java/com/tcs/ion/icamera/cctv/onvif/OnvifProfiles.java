package com.tcs.ion.icamera.cctv.onvif;

import com.tcs.ion.icamera.cctv.error.OnvifException;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.model.Profile;
import com.tcs.ion.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.tcs.ion.icamera.cctv.onvif.OnvifSoapMessages.ONVIF_GET_PROFILES;
import static com.tcs.ion.icamera.cctv.onvif.OnvifSoapMessages.ONVIF_GET_STREAM_URI;

public class OnvifProfiles {
    private static final Logger logger = LogManager.getLogger(OnvifProfiles.class);

    private OnvifProfiles() {
    }

    /**
     * Get the profiles for a given Cctv object. The profiles are retrieved using
     * the media URL from the Cctv object. The response is parsed and the profiles
     * are stored in the Cctv object.
     *
     * @param cctv the Cctv object to store the profiles in
     * @throws OnvifException if there is an error retrieving the profiles
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
                logger.info("Getting stream URI from {} for {} with \n{}", cctv.onvifInfo().mediaUrl(), profile.name(),
                        streamXml);

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
