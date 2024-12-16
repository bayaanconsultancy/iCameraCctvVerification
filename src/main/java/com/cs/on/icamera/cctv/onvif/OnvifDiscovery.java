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
	private static final Logger logger = LogManager.getLogger(OnvifDiscovery.class);

	private static final String MULTICAST_IP = "239.255.255.250";
	private static final Integer MULTICAST_PORT = 3702;
	private static final int SOCKET_TIMEOUT_MILL_SECONDS = 1000;
	private static final int DISCOVERY_TIMEOUT_MILL_SECONDS = 3000;

	private static final InetAddress MULTICAST_ADDRESS;
	private static final String SOAP_CONTENT = """
			<?xml version="1.0" encoding="UTF-8"?>
			<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
			               xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing"
			               xmlns:tds="http://www.onvif.org/ver10/device/wsdl">
			  <soap:Header>
			    <wsa:To>urn:uuid:uuid:%s</wsa:To>
			    <wsa:Action>http://schemas.xmlsoap.org/ws/2005/04/Discovery</wsa:Action>
			  </soap:Header>
			  <soap:Body>
			    <tds:Probe/>
			  </soap:Body>
			</soap:Envelope>""".formatted(UUID.randomUUID().toString());

	private static String currentInterfaceName;

	static {
		try {
			MULTICAST_ADDRESS = InetAddress.getByName(MULTICAST_IP);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

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
		socket.setSoTimeout(SOCKET_TIMEOUT_MILL_SECONDS);
		// The following line is commented out because joining the
		// multicast group is not necessary for this use case.
		// socket.joinGroup(MULTICAST_ADDRESS);
		return socket;
	}

	/**
	 * Sends a datagram packet containing the specified data to the multicast group.
	 *
	 * @param socket the MulticastSocket through which the data will be sent
	 * @param data   the byte array containing the data to be sent
	 */
	private static void sendData(MulticastSocket socket, byte[] data) {
		try {
			// Create a DatagramPacket to send to the multicast group
			DatagramPacket packet = new DatagramPacket(data, data.length, MULTICAST_ADDRESS, MULTICAST_PORT);
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
			byte[] data = new byte[1024 * 2];
			// Create a DatagramPacket to receive the data
			DatagramPacket packet = new DatagramPacket(data, data.length, MULTICAST_ADDRESS, MULTICAST_PORT);
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
			int port = Network.getFreeLocalPort();
			logger.debug("Obtained free local port: {}", port);

			Network.getNetworkInterfaces().forEach(networkInterface -> {
				currentInterfaceName = networkInterface.getName();
				logger.info("Checking network interface: {}", networkInterface);

				try (MulticastSocket socket = createMulticastGroupSocket(port, networkInterface)) {
					// Join the multicast group on the network interface
					logger.debug("Joined multicast group on interface: {}", currentInterfaceName);

					// Send the discovery data to the multicast group
					sendData(socket, SOAP_CONTENT.getBytes());
					logger.debug("Sent discovery data.");

					// Receive the responses from the ONVIF devices
					List<DatagramPacket> packets = new ArrayList<>();
					long startTime = System.currentTimeMillis();

					// Wait for a while to receive all responses
					while (System.currentTimeMillis() - startTime < DISCOVERY_TIMEOUT_MILL_SECONDS) {
						DatagramPacket packet = receiveData(socket);
						if (packet != null) {
							packets.add(packet);
						}
					}

					// Process the responses
					if (packets.isEmpty()) {
						logger.warn("No ONVIF devices found on interface {}.", currentInterfaceName);
					} else {
						parseResponses(packets);
					}
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
				addDiscoveredCctv(new Cctv().withOnvifAddress(address));
		}

		logger.info("Total discovered ONVIF devices count: {}", getDiscoveredCctvCount());
	}
}
