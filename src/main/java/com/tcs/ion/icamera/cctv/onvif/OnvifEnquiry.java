package com.tcs.ion.icamera.cctv.onvif;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.util.Credential;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The OnvifEnquiry class provides utilities to interact with ONVIF-capable CCTV devices.
 * It includes methods to authenticate with devices using credentials and retrieve their details.
 * The main purpose of this class is to identify, authenticate, and collect information about
 * ONVIF-compliant CCTV devices.
 * <p>
 * This class operates as a utility class and cannot be instantiated.
 */
public class OnvifEnquiry {
    private static final Logger logger = LogManager.getLogger(OnvifEnquiry.class);

    private OnvifEnquiry() {
    }

    /**
     * Attempts to authenticate and retrieve ONVIF details for identified CCTV devices
     * using the provided credentials. The method iterates through all given credentials
     * to match with devices from the datastore and processes only devices that are
     * not yet identified as ONVIF-compliant.
     *
     * @param credentials an array of {@code Credential} objects that contain
     *                    username and password combinations to be used
     *                    for authenticating the CCTV devices.
     */
    public static void enquire(Credential... credentials) {
        for (Credential credential : credentials) {
            List<Cctv> cctvs = new ArrayList<>(DataStore.getRefuteOnvifCctvs());
            cctvs.removeIf(cctv -> getOnvifDetails(cctv, credential));
        }
        DataStore.printIdentifiedCctvs();
    }

    /**
     * Retrieves the ONVIF details of a CCTV device using the provided credentials. This method
     * communicates with the ONVIF-compliant CCTV device to gather its capabilities, system
     * date and time, profiles, and device information. If successful, it logs the success
     * and returns true; otherwise, it logs the error, clears the credentials, and returns false.
     *
     * @param cctv the {@code Cctv} object representing the CCTV device whose ONVIF details
     *             are to be retrieved.
     * @param credential the {@code Credential} object containing the username and password
     *                   required to authenticate the CCTV device.
     * @return {@code true} if the ONVIF details were successfully retrieved, or {@code false}
     *         if an error occurred during the process.
     */
    public static boolean getOnvifDetails(Cctv cctv, Credential credential) {
        try {
            cctv.clearErrors();
            cctv.setUsername(credential.getUsername());
            cctv.setPassword(credential.getPassword());

            OnvifCapabilities.get(cctv);

            OnvifSystemDateAndTime.get(cctv);

            OnvifProfiles.get(cctv);

            OnvifDeviceInformation.get(cctv);

            cctv.removeOnvifUrl();

            logger.info("SUCCESS GETTING ONVIF DETAILS FOR {}", cctv);

            return true;
        } catch (Exception e) {
            cctv.addError(e);

            cctv.clearCredential();

            logger.error("ERROR GETTING ONVIF DETAILS FOR {}", cctv, e);

            return false;
        }
    }
}
