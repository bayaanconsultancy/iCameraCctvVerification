package com.tcs.ion.icamera.cctv.ffmpeg;

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

    public static int progress() {
        return counter == null ? 0 : counter.getPercentage();
    }

    public static boolean isComplete() {
        return counter != null && counter.isComplete();
    }

    public static int getCount() {
        return counter == null ? 0 : counter.count();
    }

    public static int getTotalCount() {
        return counter == null ? 0 : counter.total();
    }

    public static void verify() {
        cctvsToVerify.addAll(DataStore.getCctvsToVerify());
        counter = new Counter(cctvsToVerify.size());

        cctvsToVerify.forEach(CctvVerification::verify);
        DataStore.printVerifiedCctvs();
    }

    private static void verify(Cctv cctv) {
        try {
            // TODO: Implement verification logic, no need to check for null, check for duplicate enries like name ip and stream url, and thrrowly verify feed.

            Profile profile = RTSPProbe.grab(cctv);

        } catch (Exception e) {
            cctv.addError(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            logger.error("ERROR VERIFYING CCTV: {}", e.getMessage());
        } finally {
            counter.increment();
        }
    }

}
