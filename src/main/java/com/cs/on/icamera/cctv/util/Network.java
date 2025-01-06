package com.cs.on.icamera.cctv.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Network {
	private Network() {
	}

	private static final Logger logger = LogManager.getLogger(Network.class);

	public static List<String> getLocalIps() {
		List<String> ips = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isUp()) {
					Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						InetAddress address = addresses.nextElement();
						if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
							ips.add(address.getHostAddress());
						}
					}
				}
			}
		} catch (SocketException e) {
			logger.error("Error getting local IP address:", e);
		}
		logger.info("Local IP addresses: {}", ips);
		return ips;
	}

	public static List<NetworkInterface> getNetworkInterfaces() {
		List<NetworkInterface> upInterfaces = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isUp() && !networkInterface.isLoopback() && !networkInterface.isPointToPoint()
						&& !networkInterface.isVirtual()) {
					Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						InetAddress address = addresses.nextElement();
						if (address instanceof Inet4Address) {
							upInterfaces.add(networkInterface);
						}
					}
				}
			}
		} catch (SocketException e) {
			logger.error("Error getting network interfaces:", e);
		}
		logger.info("Network interfaces: {}", upInterfaces);
		return upInterfaces;
	}

	public static int getFreeLocalPort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		} catch (IOException e) {
			logger.error("Error getting a free local port number:", e);
			throw new IOException(e);
		}
	}

	public static int getFreeLocalPort(NetworkInterface networkInterface) throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			socket.bind(new InetSocketAddress(networkInterface.getInetAddresses().nextElement(), 0));
			return socket.getLocalPort();
		} catch (IOException e) {
			logger.error("Error getting a free local port number:", e);
			throw new IOException(e);
		}
	}
}
