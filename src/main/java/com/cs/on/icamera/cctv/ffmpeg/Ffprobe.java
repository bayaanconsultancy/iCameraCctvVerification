package com.cs.on.icamera.cctv.ffmpeg;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.probe.FFmpegFormat;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ffprobe {

    private static final Logger logger = LogManager.getLogger(Ffprobe.class);

    public static void get() throws IOException {
        String url = "rtsp://admin:Aminul@24@192.168.0.113:5543/live/channel0";
        FFprobe ffprobe = new FFprobe("/opt/local/bin/ffprobe");

        FFmpegProbeResult probeResult = ffprobe.probe(url);
        List<FFmpegStream> streams = probeResult.getStreams();
        for (FFmpegStream stream : streams) {
            logger.info("encoding: {}, resolutionWidth: {}, resolutionHeight: {}, quality: {}, frameRate: {}, encodingInterval/is_avc: {}, bitrate: {}",
                    stream.codec_name, stream.width, stream.height, stream.bits_per_sample, stream.avg_frame_rate, stream.is_avc, stream.bit_rate);
        }
        FFmpegFormat format = probeResult.getFormat();
        logger.info(format.toString());
    }
}
