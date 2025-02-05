package com.tcs.ion.icamera.cctv.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcs.ion.icamera.cctv.error.OnvifException;
import com.tcs.ion.icamera.cctv.error.ThrowableTypeAdapter;
import com.tcs.ion.icamera.cctv.util.UrlParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.tcs.ion.icamera.cctv.util.UrlParser.addCredentialsToRtspUrl;

/**
 * The Cctv class represents a CCTV object with associated metadata,
 * configuration, and utility methods for managing various aspects
 * such as errors, profiles, and streaming information.
 * <p>
 * This class provides methods to set, retrieve, and manipulate
 * data related to a CCTV, including ONVIF configurations, RTSP ports,
 * profiles, and more.
 */
public class Cctv {
    // Create a Gson instance with a custom date format and a ThrowableTypeAdapter
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .registerTypeAdapter(Throwable.class, new ThrowableTypeAdapter())
            .create();

    // List to store error messages
    private final List<String> error;

    // Unique identifier for the CCTV device
    private Long id;

    // Flag to indicate if the CCTV device is inside a room
    private Boolean insideRoom;

    // IP address of the CCTV device
    private String ip;

    // Make and model of the CCTV device
    private String make;

    private String model;

    // Name of the CCTV device
    private String name;

    // OnvifInfo object to store Onvif-related information
    private final OnvifInfo onvifInfo;
    // Onvif URL for the CCTV device
    private String onvifUrl;

    // List to store profiles (RTSP Port, Main Stream URL, Sub Stream URL)
    private final List<Profile> profiles;

    // RTSP port for the CCTV device, not used in Excel export but to store scanned port
    private Integer rtspPort;

    // Serial number of the CCTV device
    private String serialNumber;

    /**
     * Default constructor to initialize the CCTV object.
     */
    public Cctv() {
        this.profiles = new ArrayList<>();
        this.onvifInfo = new OnvifInfo();
        this.error = new ArrayList<>();
    }

    /**
     * Adds an error to the list of errors associated with the CCTV object.
     * The error is added as a string that is the cause of the exception if it exists, otherwise it is the message of the exception.
     *
     * @param e the exception to add as an error
     */
    public void addError(Exception e) {
        this.error.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
    }


    /**
     * Adds an error message to the list of errors associated with the CCTV object.
     *
     * @param errorMessage the error message to add
     */
    public void addError(String errorMessage) {
        // Add the error message to the list of errors
        this.error.add(errorMessage);
    }

    /**
     * Adds a profile to the CCTV object.
     *
     * @param profile the profile to add
     * @return the CCTV object with the added profile
     */
    public Cctv addProfile(Profile profile) {
        // Add the profile to the list of profiles
        this.profiles.add(profile);
        return this;
    }

    /**
     * Helper method to check for null or blank values and add an error message
     * to the error list if the condition is true.
     *
     * @param value     the value to check
     * @param fieldName the name of the field to add to the error message
     * @param <T>       the type of the value
     * @return the value if it is not null or blank, otherwise null
     */
    private <T> T checkNull(T value, String fieldName) {
        if (value == null || String.valueOf(value).isBlank()) {
            this.error.add(fieldName + " cannot be blank, provide " + fieldName + ".");
        }
        return value;
    }

    /**
     * Clears the stored credentials for the ONVIF connection by setting the username
     * and password to null. This ensures that no sensitive information remains stored
     * in the current instance of the object.
     */
    public void clearCredential() {
        this.onvifInfo.setUsername(null);
        this.onvifInfo.setPassword(null);
    }

    /**
     * Clears all the errors stored in the internal error collection.
     * This method empties the error data structure, removing all
     * logged or tracked error entries, effectively resetting it.
     */
    public void clearErrors() {
        this.error.clear();
    }

    /**
     * Gets the error message associated with the CCTV object.
     *
     * @return the error message as a string
     */
    public String getError() {
        StringBuilder sb = new StringBuilder();
        List<String> e = new HashSet<>(error).stream().toList();
        for (int i = 0; i < e.size(); i++) {
            // Append the error number and message to the string builder
            sb.append(i + 1).append(". ").append(e.get(i)).append(" \r");
        }
        return sb.toString();
    }

    /**
     * Gets the unique identifier for the CCTV object.
     *
     * @return the ID of the CCTV object
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the IP address of the CCTV object.
     *
     * @return the IP address as a string
     */
    public String getIp() {
        if (ip == null && onvifUrl != null) {
            // The IP address is null, try to get it from the ONVIF URL
            try {
                // Extract the hostname from the ONVIF URL
                return UrlParser.getHostname(onvifUrl);
            } catch (OnvifException e) {
                // Handle or log the exception if needed
            }
        }
        // The IP address is not null, return it
        return ip;
    }

    /**
     * Retrieves the main stream profile associated with the CCTV object.
     * This method specifically searches for a profile identified as "Main" within the object's profiles.
     *
     * @return the Profile object corresponding to the main stream, or null if no such profile is found
     */
    public Profile getMainStreamProfile() {
        return getStreamProfile("Main");
    }

    /**
     * Retrieves the URI for the main stream.
     *
     * @return the URI of the main stream as a String
     */
    public String getMainStreamUri() {
        return getStreamUri("Main");
    }

    /**
     * Gets the main stream URL of the CCTV object.
     *
     * @return the main stream URL as a string
     */
    public String getMainStreamUrl() {
        // Get the main stream URL by calling the getStreamUrl method with "Main" as the stream type
        return getStreamUrl("Main");
    }

    /**
     * Gets the make of the CCTV object.
     *
     * @return the make of the CCTV object as a string
     */
    public String getMake() {
        return make;
    }


    /**
     * Gets the make and model of the CCTV object as a single string with a format of "Make - Model".
     *
     * @return the make and model of the CCTV object as a single string
     */
    public String getMakeModel() {
        String trimmedMake = (make == null ? "" : make.trim());
        String trimmedModel = (model == null ? "" : model.trim());

        // If the make or model is empty, return the non-empty one. Otherwise, return both with a dash in between.
        return trimmedMake.isEmpty() || trimmedModel.isEmpty() ? trimmedMake + trimmedModel : trimmedMake + " - " + trimmedModel;
    }

    /**
     * Gets the model of the CCTV object.
     *
     * @return the model as a string
     */
    public String getModel() {
        return model;
    }

    /**
     * Gets the name of the CCTV object.
     *
     * @return the name as a string
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the Onvif URL associated with the CCTV object.
     *
     * @return the Onvif URL as a string
     */
    public String getOnvifUrl() {
        return onvifUrl;
    }

    /**
     * Retrieves the password associated with the Onvif configuration of the CCTV object.
     *
     * @return the password as a string, or an empty string if the password is null
     */
    public String getPassword() {
        return onvifInfo.password();
    }

    /**
     * Retrieves the port associated with the CCTV object. If an exception occurs during retrieval,
     * a default port value of 554 is returned.
     *
     * @return the port number as an integer, or 554 if an exception occurs
     */
    public int getPort() {
        try {
            return UrlParser.getPort(profiles.getFirst().streamUri());
        } catch (Exception e) {
            return 554;
        }
    }

    /**
     * Retrieves the list of profiles associated with the CCTV object.
     *
     * @return a list of Profile objects representing the profiles associated with the CCTV object
     */
    public List<Profile> getProfiles() {
        return profiles;
    }

    /**
     * Retrieves the RTSP (Real Time Streaming Protocol) port.
     *
     * @return the RTSP port number as an integer.
     */
    public int getRtspPort() {
        return rtspPort;
    }

    /**
     * Retrieves the serial number of the CCTV object.
     *
     * @return the serial number as a string.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Retrieves the stream profile that matches the specified stream type by iterating
     * through the list of profiles associated with the CCTV object.
     *
     * @param streamType the type of the stream to search for (e.g., "Main", "Sub")
     * @return the Profile object matching the specified stream type,
     * or null if no matching profile is found
     */
    private Profile getStreamProfile(String streamType) {
        for (Profile profile : profiles) {
            if (profile.name().equals(streamType)) {
                return profile;
            }
        }
        return null;
    }

    /**
     * Retrieves the stream URI for the specified stream type.
     *
     * @param streamType the name of the stream type for which the URI is requested
     * @return the stream URI corresponding to the provided stream type; returns an empty string if no matching stream type is found
     */
    private String getStreamUri(String streamType) {
        for (Profile profile : profiles) {
            if (profile.name().equals(streamType)) {
                return profile.streamUri();
            }
        }
        return "";
    }

    /**
     * Retrieves the stream URL of a specified stream type by iterating through the profiles
     * and matching the given stream type. Adds credentials to the RTSP URL if a matching profile is found.
     * If no matching profile is found, returns an empty string.
     *
     * @param streamType the type of the stream to retrieve the URL for (e.g., "Main", "Sub")
     * @return the stream URL with credentials added, or an empty string if no matching profile is found
     */
    private String getStreamUrl(String streamType) {
        for (Profile profile : profiles) {
            if (profile.name().equals(streamType)) {
                try {
                    return addCredentialsToRtspUrl(profile.streamUri(), onvifInfo.username(), onvifInfo.password());
                } catch (Exception e) {
                    addError(e);
                }
            }
        }
        return "";
    }

    /**
     * Retrieves the profile for the sub-stream of the current stream.
     *
     * @return a {@code Profile} object representing the sub-stream profile.
     */
    public Profile getSubStreamProfile() {
        return getStreamProfile("Sub");
    }

    /**
     * Retrieves the URI for the sub-stream.
     *
     * @return A string representing the URI of the sub-stream.
     */
    public String getSubStreamUri() {
        return getStreamUri("Sub");
    }

    /**
     * Generates and retrieves the URL for a sub-stream based on the specified stream type.
     *
     * @return The URL of the sub-stream as a String.
     */
    public String getSubStreamUrl() {
        return getStreamUrl("Sub");
    }

    /**
     * Retrieves the username associated with the ONVIF information.
     *
     * @return the username as a String.
     */
    public String getUsername() {
        return onvifInfo.username();
    }

    /**
     * Checks if the ONVIF URL is set and not blank.
     *
     * @return true if the ONVIF URL is non-null and not blank; false otherwise.
     */
    public boolean hasOnvifUrl() {
        return onvifUrl != null && !onvifUrl.isBlank();
    }

    /**
     * Checks if there are any profiles available.
     *
     * @return true if the profiles list is not empty, false otherwise.
     */
    public boolean hasProfile() {
        return !profiles.isEmpty();
    }

    /**
     * Checks if the RTSP port is defined and valid.
     *
     * @return true if the RTSP port is not null and not equal to 0, false otherwise.
     */
    public boolean hasRtspPort() {
        return rtspPort != null && rtspPort != 0;
    }

    /**
     * Sets the inside room flag after verifying it is not null.
     *
     * @param insideRoom a Boolean value indicating whether the object is inside the room;
     *                   must not be null
     */
    public void insideRoom(Boolean insideRoom) {
        this.insideRoom = checkNull(insideRoom, "Inside Room Flag");
    }

    /**
     * Determines if the entity is currently located inside the room.
     *
     * @return true if the entity is inside the room; false otherwise.
     */
    public Boolean isInsideRoom() {
        return insideRoom;
    }

    /**
     * A dummy method that gets called during an Excel upload process.
     * This method does not perform any actions and is intended as a placeholder.
     *
     * @param error A string parameter that represents an error, but it is not processed by this method.
     */
    public void noNeedToSetError(String error) {
        // No need tp set the error from a string, it is dummy method get called from Excel upload.
    }

    /**
     * Retrieves the OnvifInfo object representing the ONVIF-related information.
     *
     * @return the OnvifInfo object containing details about ONVIF configuration and settings
     */
    public OnvifInfo onvifInfo() {
        return onvifInfo;
    }

    /**
     * Clears the ONVIF URL by setting it to an empty string.
     * This method can be used to reset or remove the ONVIF URL value stored in the instance.
     */
    public void removeOnvifUrl() {
        this.onvifUrl = "";
    }

    /**
     * Removes the RTSP port by resetting its value to 0.
     * <p>
     * This method effectively disables any previously set RTSP port
     * by assigning the value 0 to the rtspPort variable.
     */
    public void removeRtspPort() {
        this.rtspPort = 0;
    }

    /**
     * Sets the unique identifier for the CCTV object.
     *
     * @param id the ID to set for the CCTV object
     */
    public void setId(Long id) {
        // Assign the provided ID to the id field
        this.id = id;
    }

    /**
     * Sets the IP address for the CCTV object.
     *
     * @param ip the IP address to set for the CCTV object
     */
    public void setIp(String ip) {
        // Validate and set the IP address, adding an error if it is null or blank
        this.ip = checkNull(ip, "CCTV IP Address");
    }

    /**
     * Sets the IP address for the instance based on the provided stream URI.
     * Extracts the hostname from the URI and assigns it to the IP address.
     *
     * @param streamUri The URI of the stream containing the hostname to set as the IP.
     * @throws OnvifException If there is an error parsing the URI or setting the IP address.
     */
    public void setIpPort(String streamUri) throws OnvifException {
        this.setIp(UrlParser.getHostname(streamUri));
    }

    /**
     * Sets the main stream URL for the CCTV object.
     *
     * @param streamUri the main stream URL to set for the CCTV object
     */
    public void setMainStreamUrl(String streamUri) {
        // Validate the main stream URL and add it to the list of profiles if it is not null or blank
        if (checkNull(streamUri, "Main Stream URL") != null) {
            // Create a new profile with the name "Main" and the provided stream URI
            this.profiles.add(new Profile().setName("Main").setStreamUri(streamUri.trim()));
        }
    }

    /**
     * Sets the make of the CCTV object.
     *
     * @param make the make of the CCTV object to set
     */
    public void setMake(String make) {
        // Set the make of the CCTV object
        this.make = make;
    }

    /**
     * Sets the make and model of the CCTV object from a single string with a format of "Make - Model".
     *
     * @param makeModel the make and model of the CCTV object as a single string
     */
    public void setMakeModel(String makeModel) {
        if (makeModel == null || makeModel.isBlank()) {
            // Clear the make and model of the CCTV object
            this.setMake("");
            this.setModel("");
            return;
        }

        // Split the provided string into two parts, with the first part being the make and the second part being the model
        String[] parts = makeModel.split("-", 2); // Limit the split to 2 parts

        // Set the make and model of the CCTV object
        this.setMake(parts.length > 0 && parts[0] != null ? parts[0].trim() : ""); // Trim the make
        this.setModel(parts.length > 1 && parts[1] != null ? parts[1].trim() : ""); // Trim the model
    }

    /**
     * Sets the model of the CCTV object.
     *
     * @param model the model to set for the CCTV object
     */
    public void setModel(String model) {
        // Assign the provided model to the model field
        this.model = model;
    }

    /**
     * Sets the name of the CCTV object after validating that it is not null or blank.
     *
     * @param name the name to set for the CCTV object, must not be null or blank
     */
    public void setName(String name) {
        this.name = checkNull(name, "CCTV Name");
    }

    /**
     * Sets the ONVIF password for the associated device.
     *
     * @param password the password to set for ONVIF authentication
     */
    public void setOnvifPassword(String password) {
        this.onvifInfo.setPassword(password);
    }

    /**
     * Sets the Onvif URL for the CCTV object.
     *
     * @param onvifUrl the Onvif URL to set for the CCTV object
     */
    public void setOnvifUrl(String onvifUrl) {
        this.onvifUrl = onvifUrl;
    }

    /**
     * Sets the ONVIF username for the device.
     *
     * @param username the username to be used for ONVIF authentication
     */
    public void setOnvifUsername(String username) {
        this.onvifInfo.setUsername(username);
    }

    /**
     * Sets the password for the ONVIF configuration of the CCTV object.
     * The password value is validated to ensure it is not null or blank.
     *
     * @param password the password to set for the ONVIF configuration, must not be null or blank
     */
    public void setPassword(String password) {
        this.onvifInfo.setPassword(checkNull(password, "Password"));
    }

    /**
     * Sets the profiles for the CCTV object. The provided list of Profile objects is added
     * to the existing profiles, and each profile is assigned a name based on its index
     * in the list ("Main" for even indexes and "Sub" for odd indexes).
     *
     * @param profiles the list of Profile objects to set and modify for the CCTV object
     */
    public void setProfiles(List<Profile> profiles) {
        this.profiles.addAll(profiles);
        for (int i = 0; i < profiles.size(); i++) {
            Profile profile = profiles.get(i);
            if (i % 2 == 0) {
                profile.setName("Main");
            } else {
                profile.setName("Sub");
            }
        }
    }

    /**
     * Sets the RTSP (Real Time Streaming Protocol) port number.
     *
     * @param rtspPort the port number to use for RTSP communication
     */
    public void setRtspPort(int rtspPort) {
        this.rtspPort = rtspPort;
    }

    /**
     * Sets the serial number for the CCTV object.
     * The provided serial number is validated to ensure it is not null or blank.
     *
     * @param serialNumber the serial number to set for the CCTV object, must not be null or blank
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = checkNull(serialNumber, "Serial Number");
    }

    /**
     * Sets the URL for the sub-stream and adds it to the profiles list.
     *
     * @param streamUri the URI of the sub-stream to be set. It should not be null or empty.
     *                  The method trims any leading or trailing whitespace before processing.
     */
    public void setSubStreamUrl(String streamUri) {
        if (checkNull(streamUri, "Sub Stream URL") != null)
            this.profiles.add(new Profile().setName("Sub").setStreamUri(streamUri.trim()));
    }

    /**
     * Sets the username for the ONVIF information.
     *
     * @param username the username to be set; must not be null
     */
    public void setUsername(String username) {
        this.onvifInfo.setUsername(checkNull(username, "Username"));
    }

    /**
     * Checks if the current operation or state signifies success.
     *
     * @return true if there are no errors (error is empty), false otherwise
     */
    public boolean success() {
        return error.isEmpty();
    }

    /**
     * Converts the current object into its JSON representation as a String.
     *
     * @return A JSON-formatted String representation of the object.
     */
    @Override
    public String toString() {
        return gson.toJson(this);
    }

    /**
     * Adds an error message to the list of errors associated with the CCTV object and
     * returns the current instance of the Cctv object to allow method chaining.
     *
     * @param errorMessage the error message to add to the list of errors
     * @return the current instance of the Cctv object
     */
    public Cctv withError(String errorMessage) {
        this.error.add(errorMessage);
        return this;
    }

    /**
     * Sets the ID for this Cctv instance and returns the current instance.
     *
     * @param id the ID to set for the Cctv instance
     * @return the current Cctv instance with the updated ID
     */
    public Cctv withId(Long id) {
        this.setId(id);
        return this;
    }

    /**
     * Sets whether the CCTV is located inside a room.
     *
     * @param insideRoom a Boolean indicating if the CCTV is inside a room
     * @return the current instance of Cctv with the updated insideRoom property
     */
    public Cctv withInsideRoom(Boolean insideRoom) {
        this.insideRoom(insideRoom);
        return this;
    }

    /**
     * Sets the IP address for the current Cctv instance and returns the instance.
     *
     * @param ip the IP address to set for the Cctv
     * @return the modified Cctv instance with the updated IP address
     */
    public Cctv withIp(String ip) {
        this.setIp(ip);
        return this;
    }

    /**
     * Sets the make of the CCTV and returns the updated instance.
     *
     * @param make the make or brand of the CCTV
     * @return the updated Cctv instance with the specified make
     */
    public Cctv withMake(String make) {
        this.setMake(make);
        return this;
    }

    /**
     * Sets the model of the CCTV object and returns the updated object.
     *
     * @param model the model name to set for the CCTV
     * @return the updated Cctv object with the specified model
     */
    public Cctv withModel(String model) {
        this.setModel(model);
        return this;
    }

    /**
     * Sets the name of the Cctv instance and returns the updated instance.
     *
     * @param name the name to be assigned to the Cctv instance
     * @return the updated Cctv instance with the specified name
     */
    public Cctv withName(String name) {
        this.setName(name);
        return this;
    }

    /**
     * Sets the ONVIF device URL for the CCTV instance and returns the updated instance.
     *
     * @param onvifAddress the URL string of the ONVIF-compatible device
     * @return the updated instance of the {@code Cctv} class with the ONVIF URL set
     */
    public Cctv withOnvifDeviceUrl(String onvifAddress) {
        this.setOnvifUrl(onvifAddress);
        return this;
    }

    /**
     * Sets the list of profiles for the CCTV instance and returns the updated instance.
     *
     * @param profiles the list of Profile objects to associate with the CCTV instance
     * @return the updated CCTV instance with the provided profiles set
     */
    public Cctv withProfiles(List<Profile> profiles) {
        this.setProfiles(profiles);
        return this;
    }

    /**
     * Sets the RTSP port for the CCTV object and returns the updated instance.
     *
     * @param rtspPort the port number to be used for RTSP communication
     * @return the updated instance of the CCTV object
     */
    public Cctv withRtspPort(int rtspPort) {
        this.rtspPort = rtspPort;
        return this;
    }

    /**
     * Sets the serial number for the CCTV object and returns the updated object.
     *
     * @param serialNumber the serial number to be assigned to the CCTV object
     * @return the updated CCTV object with the specified serial number
     */
    public Cctv withSerialNumber(String serialNumber) {
        this.setSerialNumber(serialNumber);
        return this;
    }
}
