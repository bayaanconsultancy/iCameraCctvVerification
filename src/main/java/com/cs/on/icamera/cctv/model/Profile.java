package com.cs.on.icamera.cctv.model;

public class Profile {

	private String name;
	private String token;
	private String streamUri;
	private String encoding;
	private int resolutionWidth;
	private int resolutionHeight;
	private int quality;
	private int frameRate;
	private int encodingInterval;
	private int bitrate;

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String token() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String streamUri() {
		return streamUri;
	}

	public void setStreamUri(String streamUri) {
		this.streamUri = streamUri;
	}

	public String encoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int resolutionWidth() {
		return resolutionWidth;
	}

	public void setResolutionWidth(int resolutionWidth) {
		this.resolutionWidth = resolutionWidth;
	}

	public int resolutionHeight() {
		return resolutionHeight;
	}

	public void setResolutionHeight(int resolutionHeight) {
		this.resolutionHeight = resolutionHeight;
	}

	public int quality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int frameRate() {
		return frameRate;
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	public int encodingInterval() {
		return encodingInterval;
	}

	public void setEncodingInterval(int encodingInterval) {
		this.encodingInterval = encodingInterval;
	}

	public int bitrate() {
		return bitrate;
	}

	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}
}
