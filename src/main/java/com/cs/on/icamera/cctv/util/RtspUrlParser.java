package com.cs.on.icamera.cctv.util;

import com.cs.on.icamera.cctv.error.VerificationException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class RtspUrlParser {
    private RtspUrlParser() {
    }

    public static RtspUrl parseRTSPUrl(String url) throws VerificationException {
        String schemePart = "rtsp://";
        if (url == null || !url.toLowerCase().startsWith(schemePart)) {
            throw new VerificationException("Invalid RTSP URL.");
        }

        RtspUrl rtspUrl = new RtspUrl();
        rtspUrl.setScheme("rtsp");

        // Extract username, password, host, port, path, query, and fragment
        String restUrl = url.substring(schemePart.length()); // Remove "rtsp://"

        String[] urlParts = restUrl.split("/", 2); // Split into parts
        String userPassHostPort = urlParts[0];
        String pathQueryFragment = urlParts.length > 1 ? (urlParts[1]) : "";

        String userPass;
        String hostPort;
        int atIndex = userPassHostPort.lastIndexOf("@");
        if (atIndex == -1) {
            hostPort = userPassHostPort;
        } else {
            userPass = userPassHostPort.substring(0, atIndex);
            hostPort = userPassHostPort.substring(atIndex + 1);

            String[] userPassParts = userPass.split(":", 2);
            if (userPassParts.length > 1) {
                rtspUrl.setUsername(userPassParts[0]);
                rtspUrl.setPassword(userPassParts[1]);
            } else {
                rtspUrl.setUsername("admin");
                rtspUrl.setPassword(userPass);
            }
        }

        String[] hostPortParts = hostPort.split(":", 2);
        if (hostPortParts.length > 1) {
            rtspUrl.setHost(hostPortParts[0]);
            rtspUrl.setPort(Integer.parseInt(hostPortParts[1]));
        } else {
            rtspUrl.setHost(hostPort);
            rtspUrl.setPort(554);
        }

        String pathQuery;
        String[] pathQueryFragmentParts = pathQueryFragment.split("#", 2);
        if (pathQueryFragmentParts.length > 1) {
            pathQuery = pathQueryFragmentParts[0];
            rtspUrl.setFragment(pathQueryFragmentParts[1]);
        } else {
            pathQuery = pathQueryFragment;
        }

        String[] pathQueryParts = pathQuery.split("\\?", 2);
        if (pathQueryParts.length > 1) {
            rtspUrl.setPath(pathQueryParts[0]);
            rtspUrl.setQuery(pathQueryParts[1]);
        } else {
            rtspUrl.setPath(pathQuery);
        }

        return rtspUrl;
    }

    public static class RtspUrl {
        private String scheme;
        private String username;
        private String password;
        private String host;
        private int port;
        private String path;
        private String query;
        private String fragment;

        // Constructor, getters, and setters (omitted for brevity)
        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
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

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path == null ? "/" : (path.startsWith("/") ? path : ("/" + path));
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getFragment() {
            return fragment;
        }

        public void setFragment(String fragment) {
            this.fragment = fragment;
        }

        public boolean hasUsername() {
            return username != null && !username.isEmpty();
        }

        public boolean hasPassword() {
            return password != null && !password.isEmpty();
        }

        public boolean hasCredential() {
            return hasUsername() && hasPassword();
        }

        @Override
        public String toString() {
            return "RtspUrl{" +
                   "scheme='" + scheme + '\'' +
                   ", username='" + username + '\'' +
                   ", password='" + password + '\'' +
                   ", host='" + host + '\'' +
                   ", port=" + port +
                   ", path='" + path + '\'' +
                   ", query='" + query + '\'' +
                   ", fragment='" + fragment + '\'' +
                   '}';
        }

        public String getRtspUrl() throws URISyntaxException {
            String userInfo = (hasUsername() ? username + ":" : "") + (hasPassword() ? password : "");
            return URLDecoder.decode(new URI(scheme, userInfo, host, port, path, query, fragment).toString(), StandardCharsets.UTF_8);
        }
    }
}