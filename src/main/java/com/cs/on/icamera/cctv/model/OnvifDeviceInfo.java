package com.cs.on.icamera.cctv.model;

import com.google.gson.Gson;

public class OnvifDeviceInfo {
	private String analyticsUrl;
	private String deviceUrl;
	private String eventsUrl;
	private String imagingUrl;
	private String mediaUrl;
	private String ptzUrl;
	private String nonce;
	private String created;
	private String expires;
	private String username;
	private String password;
	private String systemDateAndTime;

	public String analyticsUrl() {
		return analyticsUrl;
	}

	public OnvifDeviceInfo setAnalyticsUrl(String analyticsUrl) {
		this.analyticsUrl = analyticsUrl;
		return this;
	}

	public String deviceUrl() {
		return deviceUrl;
	}

	public OnvifDeviceInfo setDeviceUrl(String deviceUrl) {
		this.deviceUrl = deviceUrl;
		return this;
	}

	public String eventsUrl() {
		return eventsUrl;
	}

	public OnvifDeviceInfo setEventsUrl(String eventsUrl) {
		this.eventsUrl = eventsUrl;
		return this;
	}

	public String imagingUrl() {
		return imagingUrl;
	}

	public OnvifDeviceInfo setImagingUrl(String imagingUrl) {
		this.imagingUrl = imagingUrl;
		return this;
	}

	public String mediaUrl() {
		return mediaUrl;
	}

	public OnvifDeviceInfo setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
		return this;
	}

	public String ptzUrl() {
		return ptzUrl;
	}

	public OnvifDeviceInfo setPtzUrl(String ptzUrl) {
		this.ptzUrl = ptzUrl;
		return this;
	}

	public String nonce() {
		return nonce;
	}

	public OnvifDeviceInfo setNonce(String nonce) {
		this.nonce = nonce;
		return this;
	}

	public String created() {
		return created;
	}

	public OnvifDeviceInfo setCreated(String created) {
		this.created = created;
		return this;
	}

	public String expires() {
		return expires;
	}

	public OnvifDeviceInfo setExpires(String expires) {
		this.expires = expires;
		return this;
	}

	public String username() {
		return username;
	}

	public OnvifDeviceInfo setUsername(String username) {
		this.username = username;
		return this;
	}

	public String password() {
		return password;
	}

	public OnvifDeviceInfo setPassword(String password) {
		this.password = password;
		return this;
	}

	public String systemDateAndTime() {
		return systemDateAndTime;
	}

	public OnvifDeviceInfo setSystemDateAndTime(String systemDateAndTime) {
		this.systemDateAndTime = systemDateAndTime;
		return this;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
