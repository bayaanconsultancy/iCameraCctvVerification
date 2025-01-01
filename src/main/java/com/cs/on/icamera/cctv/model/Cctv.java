package com.cs.on.icamera.cctv.model;

import static com.cs.on.icamera.cctv.onvif.OnvifResponseParser.parseIpPort;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class Cctv {
	private Long id;
	private int port;
	private String ip;
	private String name;
	private String make;
	private String model;
	private String username;
	private String password;
	private String serialNumber;
	private String onvifUrl;
	private Boolean insideRoom;
	private final List<Profile> profiles;
	private final OnvifInfo onvifInfo;

	public Cctv() {
		this.profiles = new ArrayList<>();
		this.onvifInfo = new OnvifInfo();
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
		Object[] ipPort = parseIpPort(onvifUrl);
		this.ip = (String) ipPort[0];
		this.port = (int) ipPort[1];
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

	public Cctv withUsername(String username) {
		this.setUsername(username);
		return this;
	}

	public Cctv withPassword(String password) {
		this.setPassword(password);
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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Cctv withPort(int port) {
		this.setPort(port);
		return this;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public OnvifInfo onvifInfo() {
		return onvifInfo;
	}

	public void setOnvifUsername(String username) {
		this.onvifInfo.setUsername(username);
	}

	public void setOnvifPassword(String password) {
		this.onvifInfo.setPassword(password);
	}
}
