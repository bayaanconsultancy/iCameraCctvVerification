package com.cs.on.icamera.cctv.onvif;
/**
 * This package contains classes and interfaces related to ONVIF (Open Network
 * Video Interface Forum) communication for CCTV (Closed-Circuit Television)
 * devices. It provides functionality for discovering, querying, and interacting
 * with ONVIF-compliant devices, including retrieving device information,
 * capabilities, profiles, and media streams.
 * <p>
 * Key classes include: - {@link OnvifDiscovery}: Handles the discovery of ONVIF
 * devices on the network. - {@link OnvifEnquiry}: Provides methods for querying
 * ONVIF device details. - {@link OnvifSystemDateAndTime}: Retrieves system date
 * and time from ONVIF devices. - {@link OnvifDeviceInformation}: Retrieves
 * device-specific information from ONVIF devices. - {@link OnvifProfiles}:
 * Manages the profiles available on ONVIF devices.
 * <p>
 * Exceptions related to ONVIF operations are defined in: -
 * {@link OnvifException} - {@link OnvifDiscoveryException}
 * <p>
 * Utility classes, such as {@link HttpSoapClient}, support the HTTP SOAP
 * communication required for ONVIF operations.
 */
