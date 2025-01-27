package com.tcs.ion.icamera.cctv.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcs.ion.icamera.cctv.error.OnvifException;
import com.tcs.ion.icamera.cctv.error.ThrowableTypeAdapter;
import com.tcs.ion.icamera.cctv.util.UrlParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.tcs.ion.icamera.cctv.util.UrlParser.addCredentialsToRtspUrl;

public class Cctv {
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").registerTypeAdapter(Throwable.class, new ThrowableTypeAdapter()).create();
    private final List<String> error; // Error Message
    private final List<Profile> profiles; // RTSP Port, Main Stream URL, Sub Stream URL
    private final OnvifInfo onvifInfo;
    private Long id;
    private String ip; // IP Address
    private String name; // CCTV Name
    private String make; // Make - Model*
    private String model; // Make - Model*
    private String serialNumber; // Serial No
    private Boolean insideRoom; // Inside Room
    private String onvifUrl;

    public Cctv() {
        this.profiles = new ArrayList<>();
        this.onvifInfo = new OnvifInfo();
        this.error = new ArrayList<>();
    }

    private <T> T checkNull(T value, String fieldName) {
        if (value == null || String.valueOf(value).isBlank()) {
            this.error.add(fieldName + " cannot be blank. Please provide " + fieldName + ".");
        }
        return value;
    }


    public Cctv addProfile(Profile profile) {
        this.profiles.add(profile);
        return this;
    }

    public String getError() {
        StringBuilder sb = new StringBuilder();
        List<String> e = new HashSet<>(error).stream().toList();
        for (int i = 0; i < e.size(); i++) {
            sb.append(i + 1).append(". ").append(e.get(i)).append(" \n");
        }
        return sb.toString();
    }

    public void addError(Exception e) {
        this.error.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
    }

    public void addError(String e) {
        this.error.add(e);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        if (ip == null) {
            try {
                return UrlParser.getHostname(onvifUrl);
            } catch (OnvifException e) {
                // Handle or log the exception if needed
            }
        }
        return ip;
    }

    public void setIp(String ip) {
        this.ip = checkNull(ip, "CCTV IP Address");
    }

    public String getMainStreamUrl() {
        return getStreamUrl("Main");
    }

    public void setMainStreamUrl(String streamUri) {
        if (checkNull(streamUri, "Main Stream URL") != null)
            this.profiles.add(new Profile().setName("Main").setStreamUri(streamUri.trim()));
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getMakeModel() {
        String trimmedMake = (make == null ? "" : make.trim());
        String trimmedModel = (model == null ? "" : model.trim());

        return trimmedMake.isEmpty() || trimmedModel.isEmpty() ? trimmedMake + trimmedModel : trimmedMake + " - " + trimmedModel;
    }

    public void setMakeModel(String makeModel) {
        if (makeModel == null || makeModel.isBlank()) {
            this.setMake("");
            this.setModel("");
            return;
        }

        String[] parts = makeModel.split("-", 2); // Limit the split to 2 parts
        this.setMake(parts.length > 0 && parts[0] != null ? parts[0].trim() : "");
        this.setModel(parts.length > 1 && parts[1] != null ? parts[1].trim() : "");
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = checkNull(name, "CCTV Name");
    }

    public String getOnvifUrl() {
        return onvifUrl;
    }

    public void setOnvifUrl(String onvifUrl) {
        this.onvifUrl = onvifUrl;
    }

    public String getPassword() {
        return onvifInfo.password();
    }

    public void setPassword(String password) {
        this.onvifInfo.setPassword(checkNull(password, "Password"));
    }

    public int getPort() {
        try {
            return UrlParser.getPort(profiles.getFirst().streamUri());
        } catch (Exception e) {
            return 554;
        }
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles.addAll(profiles);
        for (int i = 0; i < profiles.size(); i++) {
            Profile profile = profiles.get(i);
            if (i % 2 == 0) {
                profile.setName("Main");
            } else {
                profile.setName("Sub");
            }
        }
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = checkNull(serialNumber, "Serial Number");
    }

    private String getStreamUrl(String streamType) {
        for (Profile profile : profiles) {
            if (profile.name().equals(streamType)) {
                try {
                    return addCredentialsToRtspUrl(profile.streamUri(), onvifInfo.username(), onvifInfo.password());
                } catch (Exception e) {
                    addError(e);
                }
            }
        }
        return "";
    }

    private Profile getStreamProfile(String streamType) {
        for (Profile profile : profiles) {
            if (profile.name().equals(streamType)) {
                return profile;
            }
        }
        return null;
    }

    public Profile getMainStreamProfile() {
        return getStreamProfile("Main");
    }

    public Profile getSubStreamProfile() {
        return getStreamProfile("Sub");
    }

    public String getSubStreamUrl() {
        return getStreamUrl("Sub");
    }

    public void setSubStreamUrl(String streamUri) {
        if (checkNull(streamUri, "Sub Stream URL") != null)
            this.profiles.add(new Profile().setName("Sub").setStreamUri(streamUri.trim()));
    }

    public String getUsername() {
        return onvifInfo.username();
    }

    public void setUsername(String username) {
        this.onvifInfo.setUsername(checkNull(username, "Username"));
    }

    public void insideRoom(Boolean insideRoom) {
        this.insideRoom = checkNull(insideRoom, "Inside Room Flag");
    }

    public Boolean isInsideRoom() {
        return insideRoom;
    }

    public OnvifInfo onvifInfo() {
        return onvifInfo;
    }

    public void noNeedToSetError(String error) {
        // No need tp set the error from a string, it is dummy method get called from Excel upload.
    }

    public void setIpPort(String streamUri) throws OnvifException {
        this.setIp(UrlParser.getHostname(streamUri));
    }

    public void setOnvifPassword(String password) {
        this.onvifInfo.setPassword(password);
    }

    public void setOnvifUsername(String username) {
        this.onvifInfo.setUsername(username);
    }

    public boolean success() {
        return error.isEmpty();
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public Cctv withId(Long id) {
        this.setId(id);
        return this;
    }

    public Cctv withInsideRoom(Boolean insideRoom) {
        this.insideRoom(insideRoom);
        return this;
    }

    public Cctv withIp(String ip) {
        this.setIp(ip);
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

    public Cctv withName(String name) {
        this.setName(name);
        return this;
    }

    public Cctv withOnvifDeviceUrl(String onvifAddress) {
        this.setOnvifUrl(onvifAddress);
        return this;
    }

    public Cctv withProfiles(List<Profile> profiles) {
        this.setProfiles(profiles);
        return this;
    }

    public Cctv withSerialNumber(String serialNumber) {
        this.setSerialNumber(serialNumber);
        return this;
    }

    public void clearErrors() {
        this.error.clear();
    }

    public void clearCredential() {
        this.onvifInfo.setUsername(null);
        this.onvifInfo.setPassword(null);
    }

    public String getSubStreamUri() {
        return getStreamUri("Sub");
    }

    public String getMainStreamUri() {
        return getStreamUri("Main");
    }

    private String getStreamUri(String streamType) {
        for (Profile profile : profiles) {
            if (profile.name().equals(streamType)) {
                return profile.streamUri();
            }
        }
        return "";
    }
}
