package com.cs.on.icamera.cctv.ffmpeg;

import com.cs.on.icamera.cctv.error.VerificationException;
import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.model.Profile;
import com.cs.on.icamera.cctv.util.UrlParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.net.URISyntaxException;

public class RTSPProbe {
    private static final Logger logger = LogManager.getLogger(RTSPProbe.class);

    private RTSPProbe() {
    }

    public static void grab(Cctv cctv) throws VerificationException, URISyntaxException {
        String rtspUrl = UrlParser.getRtspUrlForFfmpeg(cctv.getSubStreamUri(), cctv.getUsername(), cctv.getPassword());
        Profile profile = cctv.getSubStreamProfile();

        logger.info("Probing RTSP stream: {}", rtspUrl);
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl)) {

            // Initialize the grabber with the RTSP stream
            grabber.setOption("rtsp_transport", "tcp"); // Use TCP for RTSP (safer against packet loss)
            grabber.setOption("rw_timeout", "5000000"); // 5 seconds timeout in microseconds

            grabber.start(); // Start grabbing the stream

            // Get stream information
            int videoStreamIndex = grabber.getVideoStream();
            if (videoStreamIndex == -1) {
                logger.error("No video stream found in the RTSP feed!");
            } else {
                profile.setBitrate(grabber.getVideoBitrate());
                profile.setEncoding(grabber.getVideoCodecName());
                profile.setFrameRate(grabber.getVideoFrameRate());
                profile.setResolutionHeight(grabber.getImageHeight());
                profile.setResolutionWidth(grabber.getImageWidth());

                logger.info("Video stream found. Codec: {}", grabber.getVideoCodecName());
            }
        } catch (Exception e) {
            logger.error("An error occurred while probing the RTSP stream: {}", e.getMessage());
            throw new VerificationException(e);
        }
    }
}
