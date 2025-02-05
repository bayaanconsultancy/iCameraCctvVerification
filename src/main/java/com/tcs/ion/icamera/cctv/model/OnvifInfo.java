package com.tcs.ion.icamera.cctv.model;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

/**
 * The OnvifInfo class represents a data structure that holds information and utilities for
 * interacting with ONVIF-compliant devices. It provides functionalities for generating
 * necessary security headers for ONVIF requests, managing URLs for specific services,
 * and handling user credentials and expiration details.
 * <p>
 * This class includes methods to generate a nonce, compute a password digest for secure
 * authorization, and construct headers required for ONVIF requests. Additionally, the class
 * allows for the storage and retrieval of URLs related to ONVIF services such as analytics,
 * device management, events, imaging, media, and PTZ control.
 * <p>
 * Features such as user credential management, system date-time manipulation, and secure
 * token generation are also covered.
 * <p>
 * The default username is initialized as "admin", and passwords can be set as needed.
 * Created and expiration times for the security tokens are managed internally.
 */
public class OnvifInfo {
    private static final long EXPIRY_TIME_MILLIS = 300000;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

    private final byte[] nonce;
    private String analyticsUrl;
    private String deviceUrl;
    private String eventsUrl;
    private String imagingUrl;
    private String mediaUrl;
    private String ptzUrl;
    private Date created;
    private Date expires;
    private String username;
    private String password;
    private String systemDateAndTime;

    public OnvifInfo() {
        this.nonce = generateNonce();
        this.created = new Date();
        this.expires = new Date(created.getTime() + EXPIRY_TIME_MILLIS);
        this.username = "admin";
    }

    public byte[] generateNonce() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);

        return bytes;
    }

    public String passwordDigest() throws NoSuchAlgorithmException {
        // Password_Digest = Base64 ( SHA-1 ( nonce + created + password ) )

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

        sha1.update(nonce);
        sha1.update(created().getBytes());
        sha1.update(password().getBytes());

        return new String(Base64.getEncoder().encode(sha1.digest()), StandardCharsets.UTF_8);
    }

    public String nonce() {
        return new String(Base64.getEncoder().encode(nonce), StandardCharsets.UTF_8);
    }

    public String created() {
        return DATE_FORMATTER.format(created.toInstant());
    }

    public String header() throws NoSuchAlgorithmException {
        return """
                <Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
                                  xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
                            <wsse:UsernameToken wsu:Id="usernameToken">
                                <wsse:Username>%s</wsse:Username>
                                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest">%s</wsse:Password>
                                <wsse:Nonce EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary">%s</wsse:Nonce>
                                <wsu:Created>%s</wsu:Created>
                            </wsse:UsernameToken>
                        </Security>""".formatted(username(), passwordDigest(), nonce(), created());
    }

    public String analyticsUrl() {
        return analyticsUrl;
    }

    public OnvifInfo setAnalyticsUrl(String analyticsUrl) {
        this.analyticsUrl = analyticsUrl;
        return this;
    }

    public String deviceUrl() {
        return deviceUrl;
    }

    public OnvifInfo setDeviceUrl(String deviceUrl) {
        this.deviceUrl = deviceUrl;
        return this;
    }

    public String eventsUrl() {
        return eventsUrl;
    }

    public OnvifInfo setEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
        return this;
    }

    public String imagingUrl() {
        return imagingUrl;
    }

    public OnvifInfo setImagingUrl(String imagingUrl) {
        this.imagingUrl = imagingUrl;
        return this;
    }

    public String mediaUrl() {
        return mediaUrl;
    }

    public OnvifInfo setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
        return this;
    }

    public String ptzUrl() {
        return ptzUrl;
    }

    public OnvifInfo setPtzUrl(String ptzUrl) {
        this.ptzUrl = ptzUrl;
        return this;
    }

    public OnvifInfo setCreated(Date created) {
        this.created = created;
        setExpires(new Date(created.getTime() + EXPIRY_TIME_MILLIS));
        return this;
    }

    public String expires() {
        return DATE_FORMATTER.format(expires.toInstant());
    }

    public OnvifInfo setExpires(Date expires) {
        this.expires = expires;
        return this;
    }

    public String username() {
        return username;
    }

    public OnvifInfo setUsername(String username) {
        this.username = username;
        return this;
    }

    public String password() {
        return password == null ? "" : password;
    }

    public boolean hasCredential() {
        return password != null && !password.isEmpty();
    }

    public OnvifInfo setPassword(String password) {
        this.password = password;
        return this;
    }

    public String systemDateAndTime() {
        return systemDateAndTime;
    }

    public OnvifInfo setSystemDateAndTime(String systemDateAndTime) {
        this.systemDateAndTime = systemDateAndTime;
        setCreated(Date.from(DATE_FORMATTER.parse(systemDateAndTime, java.time.Instant::from)));
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
