package com.tcs.ion.icamera.cctv.rtsp;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.model.Profile;
import com.tcs.ion.icamera.cctv.util.Counter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CctvVerification {
    private static final Logger logger = LogManager.getLogger(CctvVerification.class);
    private static final List<Cctv> cctvsToVerify = new ArrayList<>();
    private static Counter counter;

    private CctvVerification() {
    }

    /**
     * Returns the progress percentage of the verification process.
     *
     * @return the progress percentage as an integer, or 0 if the counter is null.
     */
    public static int progress() {
        // Check if the counter is null and return 0 if it is
        return counter == null ? 0 : counter.getPercentage();
    }

    /**
     * Checks if the verification process is complete.
     *
     * @return true if the process is complete, false otherwise.
     */
    public static boolean isComplete() {
        // Check if the counter is null and return false if it is
        return counter != null && counter.isComplete();
    }

    /**
     * Returns the number of cctvs verified so far.
     *
     * @return the number of cctvs verified, or 0 if the counter is null.
     */
    public static int getCount() {
        // Check if the counter is null and return 0 if it is
        return counter == null ? 0 : counter.count();
    }

    /**
     * Returns the total number of CCTVs to verify.
     *
     * @return the total count of CCTVs, or 0 if the counter is null.
     */
    public static int getTotalCount() {
        // Check if the counter is null and return 0 if it is
        return counter == null ? 0 : counter.total();
    }

    /**
     * Starts the verification process on the list of CCTVs to verify.
     * The utility will check for various aspects, including any missing or duplicate information, stream availability, and compatibility factors such as username, password, accepted video encoding, resolution, and bitrate. If any errors are detected, the utility will prompt the user to download the Excel template again for corrections. After making the necessary updates, the user can re-upload the file. Finally, the utility will create a read-only sheet within the same Excel file, which will be used to onboard the CCTV into the iCamera Live Streaming solution.
     * The verification process is a blocking call.
     */
    public static void verify() {
        // Add all CCTVs to verify to the list
        cctvsToVerify.addAll(DataStore.getExcelCctvs());

        // Initialize the counter
        counter = new Counter(cctvsToVerify.size());

        // Start the verification process
        cctvsToVerify.forEach(CctvVerification::verify);

        // Print the verified CCTVs
        DataStore.printVerifiedCctvs();
    }

    /**
     * Verify the given CCTV.
     * <p>
     * This method implements the verification logic. It is a blocking call and
     * should be called from the main thread. The method checks for various
     * aspects, including any missing or duplicate information, stream availability,
     * and compatibility factors such as username, password, accepted video encoding,
     * resolution, and bitrate. If any errors are detected, the utility will prompt
     * the user to download the Excel template again for corrections. After making
     * the necessary updates, the user can re-upload the file. Finally, the utility
     * will create a read-only sheet within the same Excel file, which will be used
     * to onboard the CCTV into the iCamera Live Streaming solution.
     * </p>
     *
     * @param cctv the CCTV to verify
     */
    private static void verify(Cctv cctv) {
        try {
            // TODO: Implement verification logic

            // Check for duplicate entries like name, ip and stream url
            // Verify feed
            Profile profile = RTSPProbe.grab(cctv);

        } catch (Exception e) {
            cctv.addError(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            logger.error("ERROR VERIFYING CCTV: {}", e.getMessage());
        } finally {
            counter.increment();
        }
    }

}
