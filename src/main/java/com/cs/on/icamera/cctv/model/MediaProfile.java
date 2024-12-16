package com.cs.on.icamera.cctv.model;

public class MediaProfile {
	private String streamUri;
	private String videoEncoding;
	private Integer videoWidth;
	private Integer videoHeight;
	private Integer frameRateLimit;
	private Integer bitRateLimit;

	public String getStreamUri() {
		return streamUri;
	}

	public void setStreamUri(String streamUri) {
		this.streamUri = streamUri;
	}

	public String getVideoEncoding() {
		return videoEncoding;
	}

	public void setVideoEncoding(String videoEncoding) {
		this.videoEncoding = videoEncoding;
	}

	public Integer getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(Integer videoWidth) {
		this.videoWidth = videoWidth;
	}

	public Integer getVideoHeight() {
		return videoHeight;
	}

	public void setVideoHeight(Integer videoHeight) {
		this.videoHeight = videoHeight;
	}

	public Integer getFrameRateLimit() {
		return frameRateLimit;
	}

	public void setFrameRateLimit(Integer frameRateLimit) {
		this.frameRateLimit = frameRateLimit;
	}

	public Integer getBitRateLimit() {
		return bitRateLimit;
	}

	public void setBitRateLimit(Integer bitRateLimit) {
		this.bitRateLimit = bitRateLimit;
	}

	public MediaProfile withStreamUri(String streamUri) {
		this.setStreamUri(streamUri);
		return this;
	}

	public MediaProfile withVideoEncoding(String videoEncoding) {
		this.setVideoEncoding(videoEncoding);
		return this;
	}

	public MediaProfile withVideoWidth(Integer videoWidth) {
		this.setVideoWidth(videoWidth);
		return this;
	}

	public MediaProfile withVideoHeight(Integer videoHeight) {
		this.setVideoHeight(videoHeight);
		return this;
	}

	public MediaProfile withFrameRateLimit(Integer frameRateLimit) {
		this.setFrameRateLimit(frameRateLimit);
		return this;
	}

	public MediaProfile withBitRateLimit(Integer bitRateLimit) {
		this.setBitRateLimit(bitRateLimit);
		return this;
	}
}
