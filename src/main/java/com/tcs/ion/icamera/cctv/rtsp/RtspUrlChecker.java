package com.tcs.ion.icamera.cctv.rtsp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.util.concurrent.*;

/**
 * The RtspUrlChecker class provides a utility method for verifying the availability
 * of an RTSP URL. It utilizes the FFmpegFrameGrabber library to attempt to connect
 * to the given RTSP URL and checks its accessibility.
 * <p>
 * This method uses multi-threading to ensure operations are performed within a specified
 * timeout period to prevent blocking. Logs are generated to indicate successes, errors,
 * or timeout scenarios during the verification process.
 * <p>
 * The class is primarily designed for use cases where verifying the reachability of RTSP
 * streams is required, such as in CCTV systems or media streaming applications.
 */
public class RtspUrlChecker {
    private static final Logger logger = LogManager.getLogger(RtspUrlChecker.class);
    private static final int TIMEOUT_IN_SECONDS = 4;
    RtspUrlChecker() {
    }

    /**
     * Checks if the given RTSP URL is available and reachable.
     * The method uses an asynchronous task to attempt to establish a connection
     * to the RTSP URL using FFmpegFrameGrabber. It also enforces a timeout period
     * to prevent long blocking operations.
     *
     * @param rtspUrl The RTSP URL to check for availability.
     * @return {@code true} if the RTSP URL is available; {@code false} otherwise.
     */
    public static boolean isRtspUrlAvailable(String rtspUrl) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> {
            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl)) {
                grabber.start(); // Attempt to start the grabber
                grabber.stop();  // Cleanly stop the grabber
                return true;     // If successful, the RTSP URL is available
            } catch (Exception e) {
                logger.warn("Exception occurred while checking RTSP URL {} as {}", rtspUrl, e.getMessage());
                return false;    // If any error occurs, the RTSP URL is not available
            }
        });

        try {
            return future.get(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS); // Wait with timeout
        } catch (TimeoutException e) {
            logger.warn("Timeout occurred while checking RTSP URL {} as {}", rtspUrl, e.getMessage());
            future.cancel(true); // Cancel the task if it times out
            return false;
        } catch (InterruptedException | ExecutionException e) {
            logger.warn("Interrupt or execution exception occurred while checking RTSP URL {} as {}", rtspUrl, e.getMessage());
            return false;
        } finally {
            executor.shutdownNow(); // Shut down the executor
        }
    }
}