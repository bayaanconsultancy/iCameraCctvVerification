package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.model.Profile;
import com.cs.on.icamera.cctv.util.HttpSoapClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.cs.on.icamera.cctv.onvif.OnvifSoapMessages.ONVIF_GET_PROFILES;
import static com.cs.on.icamera.cctv.onvif.OnvifSoapMessages.ONVIF_GET_STREAM_URI;

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
			logger.info("Getting profiles for {} with: \n{}", cctv.getOnvifUrl(), profileXml);

			// Parse the response and store the profiles in the Cctv object
			cctv.setProfiles(
					OnvifResponseParser.parseProfiles(HttpSoapClient.postXml(cctv.onvifInfo().mediaUrl(), profileXml)));

			// Get the stream URI for each profile
			for (Profile profile : cctv.getProfiles()) {
				String streamXml = String.format(ONVIF_GET_STREAM_URI, cctv.onvifInfo().header(), profile.token());
				logger.info("Getting stream URI for {} with \n{}", profile.name(), streamXml);

				// Parse the response and store the stream URI in the profile
				profile.setStreamUri(OnvifResponseParser
						.parseStreamUri(HttpSoapClient.postXml(cctv.onvifInfo().mediaUrl(), streamXml)));
			}
		} catch (Exception e) {
			logger.error("Error getting profiles for {} as {}", cctv, e.getMessage());
			throw new OnvifException(e);
		}
	}
}
