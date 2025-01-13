package com.cs.on.icamera.cctv.model;

import com.cs.on.icamera.cctv.error.OnvifException;
import com.cs.on.icamera.cctv.error.ThrowableTypeAdapter;
import com.cs.on.icamera.cctv.util.UrlParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.cs.on.icamera.cctv.util.UrlParser.addCredentialsToRtspUrl;

public class Cctv {
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
			.registerTypeAdapter(Throwable.class, new ThrowableTypeAdapter()).create();
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

	public Long getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public String getMainStreamUrl() {
		return getStreamUrl("Main");
	}

	public String getMake() {
		return make;
	}

	public String getMakeModel() {
		return (make == null ? "" : make) + ((make != null && model != null) ? " - " : "")
				+ (model == null ? "" : model);
	}

	public String getModel() {
		return model;
	}

	public String getName() {
		return name;
	}

	public String getOnvifUrl() {
		return onvifUrl;
	}

	public String getPassword() {
		return onvifInfo.password();
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

	public String getSerialNumber() {
		return serialNumber;
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

	public String getSubStreamUrl() {
		return getStreamUrl("Sub");
	}

	public String getUsername() {
		return onvifInfo.username();
	}

	public void insideRoom(Boolean insideRoom) {
		this.insideRoom = insideRoom;
	}

	public void insideRoom(Object insideRoom) {
		this.insideRoom = insideRoom != null && String.valueOf(insideRoom).toLowerCase().matches("y|yes|true|t");
	}

	public Boolean isInsideRoom() {
		return insideRoom;
	}

	public OnvifInfo onvifInfo() {
		return onvifInfo;
	}

	public void setError(Exception e) {
		this.error.add(e.getCause().getMessage());
	}

	public void setError(String e) {
		// No need tp set the error from a string, it is dummy method get called from
		// Excel upload.
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setIpPort(String streamUri) throws OnvifException {
		this.setIp(UrlParser.getHostname(streamUri));
	}

	public void setMainStreamUrl(String streamUri) {
		this.profiles.add(new Profile().setName("Main").setStreamUri(streamUri));
	}

	public void setMake(String make) {
		this.make = make;
	}

	public void setMakeModel(String makeModel) {
		String[] parts = makeModel.split(" - ");
		this.setMake(parts[0]);
		this.setModel(parts[1]);
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOnvifPassword(String password) {
		this.onvifInfo.setPassword(password);
	}

	public void setOnvifUrl(String onvifUrl) {
		this.onvifUrl = onvifUrl;
	}

	public void setOnvifUsername(String username) {
		this.onvifInfo.setUsername(username);
	}

	public void setPassword(String password) {
		this.onvifInfo.setPassword(password);
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

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setSubStreamUrl(String streamUri) {
		this.profiles.add(new Profile().setName("Sub").setStreamUri(streamUri));
	}

	public void setUsername(String username) {
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
}
