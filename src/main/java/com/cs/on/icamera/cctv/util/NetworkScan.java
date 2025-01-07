package com.cs.on.icamera.cctv.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkScan {
    private NetworkScan() {}

    public static boolean isPortReachable(String ipAddress, int port, int timeout) {
        try (Socket socket = new Socket()) {
            SocketAddress socketAddress = new InetSocketAddress(ipAddress, port);
            socket.connect(socketAddress, timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public static List<Integer> getReachablePorts(String ipAddress, List<Integer> ports, int timeout) {
        List<Integer> reachablePorts = new ArrayList<>();
        for (int port : ports) {
            if (isPortReachable(ipAddress, port, timeout)) {
                reachablePorts.add(port);
            }
        }
        return reachablePorts;
    }


    public static Map<String, List<Integer>> getReachablePortsMap(List<String> ipAddresses, List<Integer> ports, int timeout) {
        Map<String, List<Integer>> reachablePortsMap = new HashMap<>();
        for (String ipAddress : ipAddresses) {
            List<Integer> reachablePorts = getReachablePorts(ipAddress, ports, timeout);
            if (!reachablePorts.isEmpty()) {
                reachablePortsMap.put(ipAddress, reachablePorts);
            }
        }
        return reachablePortsMap;
    }


}
