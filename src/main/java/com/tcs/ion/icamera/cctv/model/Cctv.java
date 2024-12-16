package com.tcs.ion.icamera.cctv.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.tcs.ion.icamera.cctv.onvif.OnvifResponseParser.parseIpPort;

public class Cctv {
	private final List<MediaProfile> profiles;
	private Long id;
	private String ip;
	private int port;
	private String name;
	private String make;
	private String model;
	private String username;
	private String password;
	private String serialNumber;
	private String onvifAddress;
	private Boolean insideRoom;

	public Cctv() {
		this.profiles = new ArrayList<>();
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

	public String getOnvifAddress() {
		return onvifAddress;
	}

	public void setOnvifAddress(String onvifAddress) {
		this.onvifAddress = onvifAddress;
		Object[] ipPort = parseIpPort(onvifAddress);
		this.ip = (String) ipPort[0];
		this.port = (int) ipPort[1];
	}

	public Boolean getInsideRoom() {
		return insideRoom;
	}

	public void InsideRoom(Boolean insideRoom) {
		this.insideRoom = insideRoom;
	}

	public List<MediaProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<MediaProfile> profiles) {
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

	public Cctv withOnvifAddress(String onvifAddress) {
		this.setOnvifAddress(onvifAddress);
		return this;
	}

	public Cctv withInsideRoom(Boolean insideRoom) {
		this.InsideRoom(insideRoom);
		return this;
	}

	public Cctv withProfiles(List<MediaProfile> profiles) {
		this.setProfiles(profiles);
		return this;
	}

	public Cctv addProfile(MediaProfile profile) {
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
}
