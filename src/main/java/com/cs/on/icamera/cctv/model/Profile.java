package com.cs.on.icamera.cctv.model;

public class Profile {
	private String streamUri;
	private String videoEncoding;
	private Integer videoWidth;
	private Integer videoHeight;
	private Integer frameRateLimit;
	private Integer bitRateLimit;

	public String streamUri() {
		return streamUri;
	}

	public Profile setStreamUri(String streamUri) {
		this.streamUri = streamUri;
		return this;
	}

	public String videoEncoding() {
		return videoEncoding;
	}

	public Profile setVideoEncoding(String videoEncoding) {
		this.videoEncoding = videoEncoding;
		return this;
	}

	public Integer videoWidth() {
		return videoWidth;
	}

	public Profile setVideoWidth(Integer videoWidth) {
		this.videoWidth = videoWidth;
		return this;
	}

	public Integer videoHeight() {
		return videoHeight;
	}

	public Profile setVideoHeight(Integer videoHeight) {
		this.videoHeight = videoHeight;
		return this;
	}

	public Integer frameRateLimit() {
		return frameRateLimit;
	}

	public Profile setFrameRateLimit(Integer frameRateLimit) {
		this.frameRateLimit = frameRateLimit;
		return this;
	}

	public Integer bitRateLimit() {
		return bitRateLimit;
	}

	public Profile setBitRateLimit(Integer bitRateLimit) {
		this.bitRateLimit = bitRateLimit;
		return this;
	}
}
