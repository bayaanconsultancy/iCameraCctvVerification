package com.tcs.ion.icamera.cctv.onvif;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.error.OnvifDiscoveryException;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.util.Network;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static com.tcs.ion.icamera.cctv.data.DataStore.addDiscoveredCctv;
import static com.tcs.ion.icamera.cctv.onvif.OnvifSoapMessages.WS_DISCOVERY_PROBE;

public class OnvifDiscovery {
    private static final Logger logger = LogManager.getLogger(OnvifDiscovery.class);
    private static final int WS_DISCOVERY_TIMEOUT = 8000;
    private static final int WS_DISCOVERY_SOCKET_TIMEOUT = 4000;
    private static final int WS_DISCOVERY_MULTICAST_PORT = 3702;
    private static final String WS_DISCOVERY_MULTICAST_IP_ADDRESS = "239.255.255.250";
    private static final InetAddress WS_DISCOVERY_MULTICAST_INET_ADDRESS;
    private static String currentInterfaceName;

    static {
        try {
            WS_DISCOVERY_MULTICAST_INET_ADDRESS = InetAddress.getByName(WS_DISCOVERY_MULTICAST_IP_ADDRESS);
        } catch (UnknownHostException e) {
            throw new OnvifDiscoveryException("Failed to resolve multicast IP address", e);
        }
    }

    private OnvifDiscovery() {
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
     * an error
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

        for (NetworkInterface networkInterface : Network.getNetworkInterfaces()) {
            currentInterfaceName = networkInterface.getName();
            logger.info("Discovering ONVIF devices on interface {}", networkInterface);

            try {
                discover(networkInterface, Network.getFreeLocalPort());
            } catch (IOException e) {
                logger.error("Error discovering ONVIF devices on interface {}", networkInterface, e);
            }
        }
        logger.debug("ONVIF device discovery completed.");
    }

    /**
     * Performs ONVIF device discovery on a single network interface.
     * <p>
     * This method creates a MulticastSocket, sends the discovery data to the
     * multicast group, waits for a while to receive all responses, and then
     * processes the responses.
     * <p>
     * If an error occurs while creating the MulticastSocket, it logs an error
     * message and returns.
     * <p>
     * If no ONVIF devices are found, it logs a warning message.
     *
     * @param networkInterface The network interface to discover ONVIF devices on.
     * @param localPort        The free local port to bind the MulticastSocket to.
     */
    private static void discover(NetworkInterface networkInterface, int localPort) {
        try (MulticastSocket socket = new MulticastSocket(localPort)) {
            socket.setNetworkInterface(networkInterface);
            socket.setSoTimeout(WS_DISCOVERY_SOCKET_TIMEOUT);
            logger.debug("Created multicast socket on interface {}", currentInterfaceName);

            // Send the discovery data to the multicast group
            sendData(socket);
            logger.debug("Sent discovery packets to multicast group on interface {}", currentInterfaceName);

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
    }

    /**
     * Parses the responses received from ONVIF devices, extracts their service
     * addresses, and adds them to the discovered CCTV list.
     *
     * @param packets The list of DatagramPacket objects containing the responses
     *                from ONVIF devices.
     */
    private static void parseResponses(List<DatagramPacket> packets) {
        int discoveredCount = 0;

        for (DatagramPacket packet : packets) {
            // Skip empty or null packets
            if (packet == null || packet.getLength() == 0)
                continue;

            // Convert packet data to a string for processing
            String response = new String(packet.getData(), packet.getOffset(), packet.getLength());
            logger.info("Processing ONVIF device response: {}", response);

            try {
                // Parse the ONVIF address from the response
                String address = OnvifResponseParser.parseOnvifAddress(response);
                if (address != null) {
                    // Add the discovered CCTV device using the parsed address
                    addDiscoveredCctv(new Cctv().withOnvifDeviceUrl(address));
                    discoveredCount++;
                } else {
                    logger.error("Empty ONVIF device service address in response: {}", response);
                }
            } catch (DocumentException e) {
                // Log an error if there is an issue parsing the response
                logger.error("Error parsing ONVIF device service address in response: {} as: {}", response,
                        e.getMessage());
            }
        }

        // Log the total number of discovered ONVIF devices
        logger.info("Total discovered ONVIF devices count: {}/{}", DataStore.getDiscoveredCctvCount(), discoveredCount);
    }
}
