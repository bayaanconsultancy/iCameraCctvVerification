package com.cs.on.icamera.cctv.util;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class Network {
	private Network() {
	}

	private static final Logger logger = LogManager.getLogger(Network.class);
	private static final InetAddressValidator validator = InetAddressValidator.getInstance();

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
	public static List<String> getInetAddressesInSubnet() {
		List<String> allAddresses = new ArrayList<>();
		try {
			for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					java.net.InetAddress inetAddress = interfaceAddress.getAddress();
					if (networkInterface.isUp() && inetAddress.isSiteLocalAddress()) {
						String cidrNotation = inetAddress.getHostAddress() + "/" + interfaceAddress.getNetworkPrefixLength();
						allAddresses.addAll(Arrays.stream(new SubnetUtils(cidrNotation).getInfo().getAllAddresses()).collect(Collectors.toList()));
					}
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return allAddresses;
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

	public static List<String> inetAddressesBetween(String inetAddress1, String inetAddress2) {
		List<String> ipAddresses = new ArrayList<>();
		try {
			if (!validator.isValidInet4Address(inetAddress1)) {
				throw new Exception(String.format("Invalid IPv4 address '%s'.", inetAddress1));
			}
			if (!validator.isValidInet4Address(inetAddress2)) {
				throw new Exception(String.format("Invalid IPv4 address '%s'.", inetAddress2));
			}

			long ip1 = ip2long(inetAddress1);
			long ip2 = ip2long(inetAddress2);
			if (ip1 > ip2) {
				long ip3 = ip1;
				ip1 = ip2;
				ip2 = ip3;
			}

			for (long ip = ip1; ip <= ip2; ip++)
				ipAddresses.add(long2ip(ip));

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return ipAddresses;
	}

	private static long ip2long(String ip) {
		long[] digits = Arrays.stream(ip.split("\\.")).mapToLong(Long::parseLong).toArray();
		return digits[0] << 24 | digits[1] << 16 | digits[2] << 8 | digits[3];
	}

	private static String long2ip(long ip) {
		return String.format("%d.%d.%d.%d", (ip >> 24 & 0xff), (ip >> 16 & 0xff), (ip >> 8 & 0xff), (ip & 0xff));
	}
}
