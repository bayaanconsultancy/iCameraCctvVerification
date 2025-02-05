package com.tcs.ion.icamera.cctv.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkScan {
    private static final Logger logger = LogManager.getLogger(NetworkScan.class);

    // Configuration constants
    private static final int DEFAULT_TIMEOUT = 1000;
    private static final int NUMBER_OF_CONCURRENT_THREADS = 48;

    // Fields
    private final List<IpPort> ipPorts = new ArrayList<>();
    private final Counter counter;
    private final int noOfPorts;

    // Constructor
    public NetworkScan(Set<String> ips, Set<Integer> ports) {
        // Initialize IP and Port combinations
        ips.forEach(ip -> ports.forEach(port -> ipPorts.add(new IpPort(ip, port))));
        this.counter = new Counter(ipPorts.size());
        this.noOfPorts = ports.size();
    }

    // Check if an IP and Port combination is reachable
    private boolean isIpPortReachable(String ip, int port, int timeout) {
        try (Socket socket = new Socket()) {
            SocketAddress socketAddress = new InetSocketAddress(ip, port);
            socket.connect(socketAddress, timeout);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            counter.increment();
        }
    }

    // Refactored method to use ExecutorService for concurrent processing
    private List<IpPort> getReachableIpPorts(List<IpPort> ipPorts, int timeout) {
        ConcurrentLinkedQueue<IpPort> reachableIpPorts = new ConcurrentLinkedQueue<>();

        List<Callable<Void>> tasks = new ArrayList<>();
        for (IpPort ipPort : ipPorts) {
            tasks.add(() -> {
                if (isIpPortReachable(ipPort.ip(), ipPort.port(), timeout)) {
                    reachableIpPorts.add(ipPort);
                }
                return null; // Callable<Void> return type
            });
        }

        try (ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CONCURRENT_THREADS)) {
            // Submit tasks to executor
            logger.info("Scanning {} IP:Port combinations.", ipPorts.size());
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            logger.error("Scan interrupted.");
            Thread.currentThread().interrupt();
        }

        logger.info("Found {} of {}.", reachableIpPorts.size(), ipPorts.size());
        return new ArrayList<>(reachableIpPorts);
    }

    // Start the scan and return results as a map of reachable IP to ports
    public Map<String, List<Integer>> scan() {
        return scan(DEFAULT_TIMEOUT);
    }

    public Map<String, List<Integer>> scan(int timeout) {
        startProgressLogger();
        List<IpPort> reachableIpPorts = getReachableIpPorts(ipPorts, timeout);
        Map<String, List<Integer>> result = new HashMap<>();

        // Group reachable ports by IP
        for (IpPort ipPort : reachableIpPorts) {
            result.computeIfAbsent(ipPort.ip(), k -> new ArrayList<>()).add(ipPort.port());
        }

        return result;
    }

    // Start a progress logger thread
    private void startProgressLogger() {
        Thread.ofVirtual().start(() -> {
            while (counter.count() < counter.total()) {
                try {
                    Thread.sleep(15000);
                    logger.info("Scanned {} of {}.", counter.count(), counter.total());
                } catch (InterruptedException e) {
                    logger.error("Scan logger interrupted.");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    // Methods for progress tracking
    public int getProgress() {
        return counter.getPercentage();
    }

    public boolean isComplete() {
        return counter.isComplete();
    }

    public int getTotalCount() {
        return counter.total() / noOfPorts;
    }

    public int getCount() {
        return counter.count() / noOfPorts;
    }

    // Immutable record for IP and Port combinations
    private record IpPort(String ip, Integer port) {
    }
}