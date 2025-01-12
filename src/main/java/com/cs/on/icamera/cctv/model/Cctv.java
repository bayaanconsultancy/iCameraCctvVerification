package com.cs.on.icamera.cctv.model;

import com.cs.on.icamera.cctv.error.OnvifException;
import com.cs.on.icamera.cctv.error.ThrowableTypeAdapter;
import com.cs.on.icamera.cctv.util.UrlParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

import static com.cs.on.icamera.cctv.util.UrlParser.addCredentialsToRtspUrl;

public class Cctv {
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").registerTypeAdapter(Throwable.class, new ThrowableTypeAdapter()).create();

    private Long id;
    private String ip; // IP Address
    private int port; // RTSP Port
    private String name; // CCTV Name
    private String make; // Make - Model*
    private String model; // Make - Model*
    private String serialNumber; // Serial No
    private Boolean insideRoom; // Inside Room
    private String onvifUrl;
    private final List<String> error; // Error Message
    private final List<Profile> profiles; // RTSP Port, Main Stream URL, Sub Stream URL
    private final OnvifInfo onvifInfo;

    public Cctv() {
        this.profiles = new ArrayList<>();
        this.onvifInfo = new OnvifInfo();
        this.error = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMakeModel() {
        return (make == null ? "" : make) + ((make != null && model != null) ? " - " : "") + (model == null ? "" : model);
    }
    public void setMakeModel(String makeModel) {
        String[] parts = makeModel.split(" - ");
        this.setMake(parts[0]);
        this.setModel(parts[1]);
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getOnvifUrl() {
        return onvifUrl;
    }

    public void setOnvifUrl(String onvifUrl) {
        this.onvifUrl = onvifUrl;
    }

    public Boolean isInsideRoom() {
        return insideRoom;
    }

    public void insideRoom(Boolean insideRoom) {
        this.insideRoom = insideRoom;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles.addAll(profiles);
    }

    public void setIpPort(String streamUri) throws OnvifException {
            this.setIp(UrlParser.getHostname(streamUri));
            this.setPort(UrlParser.getPort(streamUri));
    }

    public Cctv withId(Long id) {
        this.setId(id);
        return this;
    }

    public Cctv withIp(String ip) {
        this.setIp(ip);
        return this;
    }

    public Cctv withName(String name) {
        this.setName(name);
        return this;
    }

    public Cctv withMake(String make) {
        this.setMake(make);
        return this;
    }

    public Cctv withModel(String model) {
        this.setModel(model);
        return this;
    }

    public Cctv withSerialNumber(String serialNumber) {
        this.setSerialNumber(serialNumber);
        return this;
    }

    public Cctv withOnvifDeviceUrl(String onvifAddress) {
        this.setOnvifUrl(onvifAddress);
        return this;
    }

    public Cctv withInsideRoom(Boolean insideRoom) {
        this.insideRoom(insideRoom);
        return this;
    }

    public Cctv withProfiles(List<Profile> profiles) {
        this.setProfiles(profiles);
        return this;
    }

    public Cctv addProfile(Profile profile) {
        this.profiles.add(profile);
        return this;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public OnvifInfo onvifInfo() {
        return onvifInfo;
    }

    public void setOnvifUsername(String username) {
        this.onvifInfo.setUsername(username);
    }
    public String getUsername() {
        return onvifInfo.username();
    }


    public void setOnvifPassword(String password) {
        this.onvifInfo.setPassword(password);
    }
    public String getPassword() {
        return onvifInfo.password();
    }

    public String getError() {
        StringBuilder sb = new StringBuilder();
        List<String> e = new HashSet<>(error).stream().toList();
        for (int i = 0; i < e.size(); i++) {
            sb.append(i+1).append(". ").append(e.get(i)).append(" \n");
        }
        return sb.toString();
    }

    public void setError(Exception e) {
        this.error.add(e.getCause().getMessage());
    }

    public boolean success() {
        return error.isEmpty();
    }

    public String getMainStreamUrl() {
        return getStreamUrl("Main");
    }

    public String getSubStreamUrl() {
        return getStreamUrl("Sub");
    }

    private String getStreamUrl(String streamType) {
        for (Profile profile : profiles) {
            if (profile.name().equals(streamType)) {
                try {
                    return addCredentialsToRtspUrl(profile.streamUri(), onvifInfo.username(), onvifInfo.password());
                } catch (OnvifException e) {
                    setError(e);
                }
            }
        }
        return "";
    }

    public void setMainStreamUrl() {

    }
    public void setSubStreamUrl() {

    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
