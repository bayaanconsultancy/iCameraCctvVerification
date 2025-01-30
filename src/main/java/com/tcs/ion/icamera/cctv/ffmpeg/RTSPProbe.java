package com.tcs.ion.icamera.cctv.ffmpeg;

import com.tcs.ion.icamera.cctv.error.VerificationException;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.model.Profile;
import com.tcs.ion.icamera.cctv.util.UrlParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;

import java.net.URISyntaxException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class RTSPProbe {
    private static final Logger logger = LogManager.getLogger(RTSPProbe.class);
    private static final int TIMEOUT_IN_SECONDS = 5;

    private RTSPProbe() {
    }

    public static Profile grab(Cctv cctv) throws VerificationException, URISyntaxException {
        String rtspUrl = UrlParser.getRtspUrlForFfmpeg(cctv.getSubStreamUri(), cctv.getUsername(), cctv.getPassword());
        Profile profile = cctv.getSubStreamProfile();

        logger.info("Probing RTSP stream: {}", rtspUrl);
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl);
        ExecutorService startExecutor = Executors.newSingleThreadExecutor(); // For grabber.start()
        ExecutorService cleanupExecutor = Executors.newSingleThreadExecutor(); // For grabber.stop()/release()
        AtomicReference<Boolean> grabberSuccess = new AtomicReference<>(false);

        try {
            Future<Void> future = startExecutor.submit(() -> {
                grabber.start();

                // Get video properties
                if (grabber.getVideoStream() == -1) {
                    logger.error("No video stream found in camera stream.");
                    cctv.addError("No video stream found in camera feed.");
                } else {
                    profile.setResolutionWidth(grabber.getImageWidth());
                    profile.setResolutionHeight(grabber.getImageHeight());
                    profile.setFrameRate(grabber.getVideoFrameRate());
                    profile.setEncoding(grabber.getVideoCodecName());
                    profile.setBitrate(grabber.getVideoBitrate());
                    grabberSuccess.set(true);

                    logger.info("Video properties: {}", profile);
                }

                return null;
            });

            future.get(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS); // Wait with timeout
        } catch (TimeoutException e) {
            logger.error("Timeout occurred while opening video. Attempting to stop and release...");

            if (grabberSuccess.get() != null && grabberSuccess.get()) logger.warn("Grabber success but yet got timeout.");
            else cctv.addError("Unable to get CCTV feed. Check the camera connectivity and video stream URL.");

            // Crucial: Stop and release in a *separate* thread
            cleanupExecutor.submit(() -> {
                try {
                    grabber.stop();
                    grabber.release();
                    logger.info("Grabber stopped and released.");
                } catch (FrameGrabber.Exception ex) {
                    logger.error("Error stopping/releasing grabber: {}", ex.getMessage());
                }
            });

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error probing video stream: {}", e.getMessage());
        } finally {
            startExecutor.shutdownNow(); // Important to shut down the executor
            cleanupExecutor.shutdownNow(); // Shut down the cleanup executor as well
            try {
                // Give some time for the cleanup to complete.  This might not be strictly necessary but a good practice.
                if (cleanupExecutor.awaitTermination(2, TimeUnit.SECONDS)) logger.info("Cleanup executor terminated.");
                else logger.warn("Cleanup executor failed to terminate.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Re-interrupt the thread
                logger.warn("Interrupted while waiting for cleanup.");
            }
        }
        return profile;
    }
}
