package com.tcs.ion.icamera.cctv.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;

public class NetworkScan {
    private static final Logger logger = LogManager.getLogger(NetworkScan.class);
    private static final int DEFAULT_TIMEOUT = 1000;
    private static final int NUMBER_OF_CONCURRENT_THREADS = 100;

    private final List<IpPort> ipPorts = new ArrayList<>();
    private final Counter counter;
    private final int noOfPorts;

    public NetworkScan(Set<String> ips, Set<Integer> ports) {
        ips.forEach(ip -> ports.forEach(port -> ipPorts.add(new IpPort(ip, port))));
        this.counter = new Counter(ipPorts.size());
        this.noOfPorts = ports.size();
    }

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

    private List<IpPort> getReachableIpPorts(List<IpPort> ipPorts, int timeout) {
        List<IpPort> reachableIpPorts = new ArrayList<>();

        Thread[] threads = new Thread[NUMBER_OF_CONCURRENT_THREADS];
        int step = ipPorts.size() / NUMBER_OF_CONCURRENT_THREADS;

        for (int i = 0; i < NUMBER_OF_CONCURRENT_THREADS; i++) {
            int start = i * step;
            int end = (i == NUMBER_OF_CONCURRENT_THREADS - 1) ? ipPorts.size() : start + step;

            threads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    IpPort ipPort = ipPorts.get(j);
                    if (isIpPortReachable(ipPort.ip(), ipPort.port(), timeout)) {
                        synchronized (reachableIpPorts) {
                            reachableIpPorts.add(ipPort);
                        }
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        logger.info("Found {} of {}.", reachableIpPorts.size(), ipPorts.size());
        return reachableIpPorts;
    }

    public Map<String, List<Integer>> scan() {
        return scan(DEFAULT_TIMEOUT);
    }

    public Map<String, List<Integer>> scan(int timeout) {
        startProgressLogger();
        List<IpPort> reachableIpPorts = getReachableIpPorts(ipPorts, timeout);

        Map<String, List<Integer>> result = new HashMap<>();
        reachableIpPorts
                .forEach(ipPort -> result.computeIfAbsent(ipPort.ip(), k -> new ArrayList<>()).add(ipPort.port()));
        return result;
    }

    private void startProgressLogger() {
        Thread.ofVirtual().start(() -> {
            while (counter.count() < counter.total()) {
                try {
                    Thread.sleep(15000);
                    logger.info("Scanned {} of {}.", counter.count(), counter.total());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

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

    private record IpPort(String ip, Integer port) {
    }
}
