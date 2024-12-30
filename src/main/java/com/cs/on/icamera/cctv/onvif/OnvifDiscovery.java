package com.cs.on.icamera.cctv.onvif;

import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.util.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cs.on.icamera.cctv.data.DataStore.addDiscoveredCctv;
import static com.cs.on.icamera.cctv.data.DataStore.getDiscoveredCctvCount;

public class OnvifDiscovery {
	private OnvifDiscovery() {
	}

	private static final Logger logger = LogManager.getLogger(OnvifDiscovery.class);

	private static final int WS_DISCOVERY_TIMEOUT = 4000;
	private static final int WS_DISCOVERY_SOCKET_TIMEOUT = 1000;
	private static final int WS_DISCOVERY_MULTICAST_PORT = 3702;
	private static final String WS_DISCOVERY_MULTICAST_IP_ADDRESS = "239.255.255.250";

	private static final byte[] WS_DISCOVERY_PROBE = """
			<?xml version="1.0" encoding="UTF-8"?>
			<soap:Envelope
			        xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
			        xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing"
			        xmlns:tns="http://schemas.xmlsoap.org/ws/2005/04/discovery">
			    <soap:Header>
			        <wsa:Action>
			            http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe
			        </wsa:Action>
			        <wsa:MessageID>
			            uuid:%s
			        </wsa:MessageID>
			        <wsa:To>
			            urn:schemas-xmlsoap-org:ws:2005:04:discovery
			        </wsa:To>
			    </soap:Header>
			    <soap:Body>
			        <tns:Probe>
			        </tns:Probe>
			    </soap:Body>
			</soap:Envelope>""".formatted(UUID.randomUUID().toString()).getBytes();

	private static final InetAddress WS_DISCOVERY_MULTICAST_INET_ADDRESS;
	static {
		try {
			WS_DISCOVERY_MULTICAST_INET_ADDRESS = InetAddress.getByName(WS_DISCOVERY_MULTICAST_IP_ADDRESS);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	private static String currentInterfaceName;

	/**
	 * Creates a new MulticastSocket object that listens for incoming datagram
	 * packets on the specified port and network interface.
	 *
	 * @param port             the port number to listen on
	 * @param networkInterface the network interface to listen on
	 * @return the newly created MulticastSocket object
	 * @throws IOException if an I/O error occurs
	 */
	private static MulticastSocket createMulticastGroupSocket(int port, NetworkInterface networkInterface)
			throws IOException {
		MulticastSocket socket = new MulticastSocket(port);
		socket.setNetworkInterface(networkInterface);
		socket.setSoTimeout(WS_DISCOVERY_SOCKET_TIMEOUT);
		// The following line is commented out because joining the
		// multicast group is not necessary for this use case.
		// socket.joinGroup(WS_DISCOVERY_MULTICAST_INET_ADDRESS);
		return socket;
	}

	/**
	 * Sends a datagram packet containing the specified data to the multicast group.
	 *
	 * @param socket the MulticastSocket through which the data will be sent
	 */
	private static void sendData(MulticastSocket socket) {
		try {
			// Create a DatagramPacket to send to the multicast group
			DatagramPacket packet = new DatagramPacket(WS_DISCOVERY_PROBE, WS_DISCOVERY_PROBE.length,
					WS_DISCOVERY_MULTICAST_INET_ADDRESS, WS_DISCOVERY_MULTICAST_PORT);
			// Send the packet through the specified socket
			socket.send(packet);
		} catch (IOException e) {
			// Log an error message if there is an issue sending the data
			logger.error("Error sending data to multicast group {}", currentInterfaceName, e);
		}
	}

	/**
	 * Receives a datagram packet from the multicast group.
	 *
	 * @param socket the MulticastSocket object to receive the data through
	 * @return the received DatagramPacket object if successful, or null if there is
	 *         an error
	 */
	private static DatagramPacket receiveData(MulticastSocket socket) {
		try {
			// Create a byte array to store the received data
			byte[] data = new byte[4096];
			// Create a DatagramPacket to receive the data
			DatagramPacket packet = new DatagramPacket(data, data.length, WS_DISCOVERY_MULTICAST_INET_ADDRESS,
					WS_DISCOVERY_MULTICAST_PORT);
			// Receive the data through the specified socket
			socket.receive(packet);
			// Return the received packet
			return packet;
		} catch (SocketTimeoutException e) {
			// Log a warning message if the socket times out
			logger.warn("Socket receive timed out from multicast group {}", currentInterfaceName);
		} catch (IOException e) {
			// Log an error message if there is an error receiving the data
			logger.error("Error receiving data from multicast group {}", currentInterfaceName, e);
		}
		// Return null if there is an error
		return null;
	}

	/**
	 * Performs ONVIF device discovery on all available network interfaces.
	 */
	public static void discover() {
		logger.debug("Starting ONVIF device discovery.");
		try {
			int localPort = Network.getFreeLocalPort();
			logger.debug("Obtained free local port: {}", localPort);

			Network.getNetworkInterfaces().forEach(networkInterface -> {
				currentInterfaceName = networkInterface.getName();
				logger.info("Checking network interface: {}", networkInterface);

				try (MulticastSocket socket = createMulticastGroupSocket(localPort, networkInterface)) {
					// Join the multicast group on the network interface
					logger.debug("Joined multicast group on interface: {}", currentInterfaceName);

					// Send the discovery data to the multicast group
					sendData(socket);
					logger.debug("Sent discovery data.");

					// Receive the responses from the ONVIF devices
					List<DatagramPacket> packets = new ArrayList<>();
					long startTime = System.currentTimeMillis();

					// Wait for a while to receive all responses
					while (System.currentTimeMillis() - startTime < WS_DISCOVERY_TIMEOUT)
						packets.add(receiveData(socket));

					// Process the responses
					if (packets.isEmpty())
						logger.warn("No ONVIF devices found on interface {}.", currentInterfaceName);
					else
						parseResponses(packets);
				} catch (IOException e) {
					logger.error("Error with multicast socket on interface {}:", currentInterfaceName, e);
				}
			});
		} catch (IOException e) {
			logger.error("Error during device discovery:", e);
		}
		logger.debug("ONVIF device discovery completed.");
	}

	/**
	 * Parses the responses received from ONVIF devices and extracts their service
	 * addresses.
	 *
	 * @param packets the list of DatagramPackets containing responses from ONVIF
	 *                devices
	 */
	private static void parseResponses(List<DatagramPacket> packets) {
		// Iterate over each received packet
		for (DatagramPacket packet : packets) {
			if (packet == null)
				continue; // Skip null packets

			// Convert packet data to a string
			String response = new String(packet.getData(), packet.getOffset(), packet.getLength());
			if (response.isEmpty())
				continue; // Skip empty responses

			logger.info("Processing ONVIF device response: {}", response);

			String address = null;
			try {
				// Parse the service address from the response
				address = OnvifResponseParser.parseOnvifAddress(response);
			} catch (DocumentException e) {
				logger.error("Error parsing ONVIF device service address in response: {} as: {}", response,
						e.getMessage());
			}

			if (address == null)
				logger.error("Empty ONVIF device service address in response: {}", response);
			else
				addDiscoveredCctv(new Cctv().withOnvifDeviceUrl(address));
		}

		logger.info("Total discovered ONVIF devices count: {}", getDiscoveredCctvCount());
	}
}
