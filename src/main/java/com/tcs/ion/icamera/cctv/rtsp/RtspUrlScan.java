package com.tcs.ion.icamera.cctv.rtsp;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.util.Credential;
import com.tcs.ion.icamera.cctv.util.RtspUrlParser;
import com.tcs.ion.icamera.cctv.util.RtspUrlParser.RtspUrl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The RtspUrlScan class is responsible for scanning and validating
 * RTSP (Real-Time Streaming Protocol) URLs for a list of CCTV devices.
 * It attempts to establish connections to provided RTSP URLs using
 * various credentials and updates the CCTV information accordingly.
 * <p>
 * This class is designed to identify main and sub-stream paths for
 * CCTV devices and allows reconnection or validation by checking the availability
 * of the RTSP streams.
 */
public class RtspUrlScan {
    private static final Logger logger = LogManager.getLogger(RtspUrlScan.class);
    private static final Set<RtspUrl> mainStreamPaths = new HashSet<>();
    private static final Set<RtspUrl> subStreamPaths = new HashSet<>();

    RtspUrlScan() {
    }

    /**
     * Initializes and populates collections of RTSP URL paths for main and sub-streams.
     * This method adds predefined RTSP paths with associated query parameters and fragments
     * to the respective collections and appends additional RTSP paths using a dynamic retrieval
     * process.
     * <p>
     * The predefined paths are hardcoded as primary examples of RTSP URLs with varying resolutions,
     * bitrates, authentication, and tokens. These paths are then extended with RTSP URLs extracted
     * from external data sources (like {@code Cctv} objects) via the utility method
     * {@code getRtspUrlPaths(Function<Cctv, String> urlExtractor)} with specific extraction logic
     * for main and sub-stream paths.
     * <p>
     * The paths are ultimately stored in collections representing main stream URLs
     * (`mainStreamPaths`) and sub-stream URLs (`subStreamPaths`). These collections could later
     * be used for validation, streaming setup, or other purposes.
     */
    private static void getRtspUrlPaths() {
        mainStreamPaths.clear();
        subStreamPaths.clear();

        mainStreamPaths.addAll(getRtspUrlPaths(Cctv::getMainStreamUrl));
        subStreamPaths.addAll(getRtspUrlPaths(Cctv::getSubStreamUrl));

        mainStreamPaths.add(new RtspUrl("/live/channel0", null, null));
        subStreamPaths.add(new RtspUrl("/live/channel1", null, null));

        mainStreamPaths.add(new RtspUrl("/mainstream", "resolution=high", "auth=true"));
        subStreamPaths.add(new RtspUrl("/substream", "resolution=low", "auth=true"));

        mainStreamPaths.add(new RtspUrl("/live/main", "bitrate=2048", "token=secure"));
        subStreamPaths.add(new RtspUrl("/live/sub", "bitrate=512", "token=secure"));
    }

    /**
     * Extracts RTSP URL paths from a collection of identified CCTV objects using the provided URL extractor function.
     * Filters out null, blank, or invalid URLs and returns a set of parsed RTSP URLs with path, query, and fragment information.
     *
     * @param urlExtractor a function that extracts a string representation of the RTSP URL from a {@code Cctv} object
     * @return a set of {@code RtspUrl} objects containing the valid paths, queries, and fragments from the extracted RTSP URLs
     */
    private static Set<RtspUrl> getRtspUrlPaths(Function<Cctv, String> urlExtractor) {
        return DataStore.getIdentifiedCctvs().stream().map(urlExtractor).filter(Objects::nonNull).filter(url -> !url.isBlank()).map(url -> {
            try {
                logger.info("Trying to parse RTSP URL: {}", url);
                return RtspUrlParser.parseRTSPUrl(url);
            } catch (Exception e) {
                logger.error("Failed to parse RTSP URL: {}", url, e);
                return null;
            }
        }).filter(Objects::nonNull).map(url -> new RtspUrl(url.getPath(), url.getQuery(), url.getFragment())).collect(Collectors.toSet());
    }

    /**
     * Scans a list of CCTVs to verify RTSP stream availability using the provided credentials.
     * This method retrieves and initializes RTSP URL paths, processes the specified credentials,
     * and attempts to authenticate and locate RTSP streams for each CCTV. If a stream is successfully
     * found, the corresponding CCTV is removed from the list of pending scans.
     *
     * @param credentials one or more {@code Credential} instances containing the authentication information
     *                    (e.g., username and password) used to verify RTSP stream availability for the CCTVs
     */
    public static void scan(Credential... credentials) {
        getRtspUrlPaths();
        logger.info("Found RTSP paths for {} main streams and {} sub streams.", mainStreamPaths.size(), subStreamPaths.size());

        List<Cctv> cctvs = new ArrayList<>(DataStore.getRefuteRtspCctvs());
        logger.info("Starting RTSP scan for {} cctvs.", cctvs.size());

        int threadCount = Math.min(cctvs.size(), Runtime.getRuntime().availableProcessors()) * 2;
        logger.info("USING {} CONCURRENT THREADS.", threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (Credential credential : credentials) {
            logger.info("Processing CCTVs with credential: {}", credential);
            List<Callable<Void>> tasks = cctvs.stream().map(cctv -> (Callable<Void>) () -> {
                if (check(cctv, credential)) {
                    synchronized (cctvs) {
                        cctvs.remove(cctv);
                    }
                }
                return null;
            }).toList();

            try {
                List<Future<Void>> futures = executorService.invokeAll(tasks);
                for (Future<Void> future : futures) {
                    try {
                        future.get(); // Wait for each task to complete
                    } catch (ExecutionException e) {
                        logger.error("Error occurred while processing CCTVs with credential: {}", credential, e);
                    }
                }
            } catch (InterruptedException e) {
                logger.error("Error occurred while processing CCTVs with credential: {}", credential, e);
                Thread.currentThread().interrupt();
            }
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("ExecutorService was interrupted during shutdown.", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Verifies the availability of RTSP streams for the specified CCTV using the provided credentials.
     * If the streams are successfully located, the CCTV object is updated with the appropriate stream URLs
     * and credentials.
     *
     * @param cctv       the CCTV object to be validated and updated with stream URLs and credentials
     * @param credential the credentials used to authenticate and locate RTSP streams for the CCTV
     * @return true if at least one stream URL is successfully found and updated for the CCTV, false otherwise
     */
    private static boolean check(Cctv cctv, Credential credential) {
        if (cctv.hasProfile()) {
            logger.info("CCTV with IP {} already has profile(s).", cctv.getIp());
            cctv.removeRtspPort();
            return true;
        }

        RtspUrl rtspUrl = new RtspUrl();
        rtspUrl.setHost(cctv.getIp());
        rtspUrl.setPort(cctv.getPort());
        rtspUrl.setUsername(credential.getUsername());
        rtspUrl.setPassword(credential.getPassword());

        boolean nainStreamFound = mainStreamPaths.parallelStream().anyMatch(mainStreamPath -> check(cctv, rtspUrl, mainStreamPath, Cctv::setMainStreamUrl));
        boolean subStreamFound = subStreamPaths.parallelStream().anyMatch(subStreamPath -> check(cctv, rtspUrl, subStreamPath, Cctv::setSubStreamUrl));

        if (nainStreamFound || subStreamFound) {
            cctv.removeRtspPort(); // Reset RTSP port that it does not get re-selected with new credentials
            cctv.setUsername(credential.getUsername());
            cctv.setPassword(credential.getPassword());
            return true;
        } else return false;
    }

    /**
     * Checks the availability of an RTSP URL composed of the given RTSP host and path,
     * and sets the URL on the specified Cctv object if available.
     *
     * @param cctv     the Cctv object to update if the RTSP URL is available
     * @param rtspHost the RTSP host to be used in constructing the URL
     * @param rtspPath the RTSP path to be used in constructing the URL
     * @param setter   a BiConsumer to set the RTSP URL on the given Cctv object
     * @return true if the RTSP URL is available and successfully set, false otherwise
     */
    private static boolean check(Cctv cctv, RtspUrl rtspHost, RtspUrl rtspPath, BiConsumer<Cctv, String> setter) {
        try {
            String rtspUrlStr = new RtspUrl(rtspHost, rtspPath).getRtspUrl();
            logger.info("Is RTSP URL available: {}", rtspUrlStr);
            if (RtspUrlChecker.isRtspUrlAvailable(rtspUrlStr)) {
                logger.info("RTSP URL is available: {}", rtspUrlStr);
                setter.accept(cctv, rtspUrlStr);
                return true;
            } else {
                logger.warn("RTSP URL is not available: {}", rtspUrlStr);
            }
        } catch (Exception e) {
            logger.warn("Failed to check RTSP URL: {}", rtspHost, e);
        }
        return false;
    }
}
