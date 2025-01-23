package com.cs.on.icamera.cctv.model;

public class Profile {

    private String name;
    private String token;
    private String streamUri;
    private String encoding;
    private int resolutionWidth;
    private int resolutionHeight;
    private float quality;
    private double frameRate;
    private int encodingInterval;
    private int bitrate;

    public String name() {
        return name;
    }

    public Profile setName(String name) {
        this.name = name;
        return this;
    }

    public String token() {
        return token;
    }

    public Profile setToken(String token) {
        this.token = token;
        return this;
    }

    public String streamUri() {
        return streamUri;
    }

    public Profile setStreamUri(String streamUri) {
        this.streamUri = streamUri;
        return this;
    }

    public String encoding() {
        return encoding;
    }

    public Profile setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public int resolutionWidth() {
        return resolutionWidth;
    }

    public Profile setResolutionWidth(int resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
        return this;
    }

    public int resolutionHeight() {
        return resolutionHeight;
    }

    public Profile setResolutionHeight(int resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
        return this;
    }

    public float quality() {
        return quality;
    }

    public Profile setQuality(float quality) {
        this.quality = quality;
        return this;
    }

    public double frameRate() {
        return frameRate;
    }

    public Profile setFrameRate(double frameRate) {
        this.frameRate = frameRate;
        return this;
    }

    public int encodingInterval() {
        return encodingInterval;
    }

    public Profile setEncodingInterval(int encodingInterval) {
        this.encodingInterval = encodingInterval;
        return this;
    }

    public int bitrate() {
        return bitrate;
    }

    public Profile setBitrate(int bitrate) {
        this.bitrate = bitrate;
        return this;
    }
}
