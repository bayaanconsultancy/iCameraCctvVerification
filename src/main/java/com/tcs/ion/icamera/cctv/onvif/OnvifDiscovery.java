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

/**
 * Performs ONVIF device discovery to locate ONVIF-compatible devices
 * on the network using WS-Discovery protocol. This class leverages
 * multicast to communicate with devices and parse their responses
 * to extract service URLs.
 */
public class OnvifDiscovery {
    private static final Logger logger = LogManager.getLogger(OnvifDiscovery.class);
    private static final int WS_DISCOVERY_TIMEOUT = 4000;
    private static final int WS_DISCOVERY_SOCKET_TIMEOUT = 2000;
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
     * Sends a WS-Discovery probe packet through the specified multicast socket.
     * <p>
     * This method is used to transmit a WS-Discovery probe message to a multicast
     * group in order to discover ONVIF-compliant devices on the network. It
     * constructs a `DatagramPacket` containing the probe message and sends it
     * through the provided `MulticastSocket`.
     *
     * @param socket the `MulticastSocket` used for sending the probe packet
     * @throws IOException if an I/O error occurs while sending the packet
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
     * Receives data from a multicast group through the specified multicast socket.
     * <p>
     * This method creates a `DatagramPacket` to hold the incoming data and
     * listens for data on the provided `MulticastSocket` instance. If an error
     * occurs, such as a timeout or I/O problem, it logs an appropriate message
     * and returns null.
     *
     * @param socket the `MulticastSocket` used to receive data from the multicast group
     * @return the received `DatagramPacket`, or null if an error occurs during the receiving process
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
     * Discovers ONVIF-compliant devices on all available network interfaces.
     * <p>
     * This method iterates through all available network interfaces that are up and
     * capable of multicast communication. For each network interface, it attempts
     * to discover ONVIF devices by invoking the `discover(NetworkInterface, int)` method
     * with a free local port. If an error occurs during discovery on an interface,
     * it is logged without interrupting the discovery process for other interfaces.
     * <p>
     * The method logs the start and end of the discovery process, and logs relevant
     * information for each network interface being used in discovery.
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
     * Performs WS-Discovery to find ONVIF-compliant devices on a specific network interface
     * and port using multicast communication.
     * <p>
     * This method establishes a multicast socket on the given network interface and local port,
     * sends discovery packets to a defined multicast group, and waits for responses from
     * ONVIF devices. The responses are processed and logged, and any errors during the
     * discovery process are also logged.
     *
     * @param networkInterface the network interface to perform the discovery on
     * @param localPort        the local port to bind the multicast socket to
     */
    private static void discover(NetworkInterface networkInterface, int localPort) {
        try (MulticastSocket socket = new MulticastSocket(new InetSocketAddress(Network.getIPv4InetAddress(networkInterface), localPort))) {
            socket.joinGroup(new InetSocketAddress(WS_DISCOVERY_MULTICAST_INET_ADDRESS, WS_DISCOVERY_MULTICAST_PORT), networkInterface);
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
     * Parses a list of DatagramPacket objects to extract and process ONVIF device responses.
     * This method processes each packet to identify ONVIF-compliant device addresses, adds
     * the discovered devices to the data store, and logs the results.
     *
     * @param packets the list of DatagramPacket objects containing responses from ONVIF devices
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
