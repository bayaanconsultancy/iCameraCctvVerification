/**
 * This package contains classes and interfaces related to ONVIF (Open Network
 * Video Interface Forum) communication for CCTV (Closed-Circuit Television)
 * devices. It provides functionality for discovering, querying, and interacting
 * with ONVIF-compliant devices, including retrieving device information,
 * capabilities, profiles, and media streams.
 * <p>
 * Key classes include: - {@link com.tcs.ion.icamera.cctv.onvif.OnvifDiscovery}: Handles the discovery of ONVIF
 * devices on the network. - {@link com.tcs.ion.icamera.cctv.onvif.OnvifEnquiry}: Provides methods for querying
 * ONVIF device details. - {@link com.tcs.ion.icamera.cctv.onvif.OnvifSystemDateAndTime}: Retrieves system date
 * and time from ONVIF devices. - {@link com.tcs.ion.icamera.cctv.onvif.OnvifDeviceInformation}: Retrieves
 * device-specific information from ONVIF devices. - {@link com.tcs.ion.icamera.cctv.onvif.OnvifProfiles}:
 * Manages the profiles available on ONVIF devices.
 * <p>
 * Exceptions related to ONVIF operations are defined in: -
 * {@link com.tcs.ion.icamera.cctv.error.OnvifException} - {@link com.tcs.ion.icamera.cctv.error.OnvifDiscoveryException}
 * <p>
 * Utility classes, such as {@link com.tcs.ion.icamera.cctv.util.HttpSoapClient}, support the HTTP SOAP
 * communication required for ONVIF operations.
 */
package com.tcs.ion.icamera.cctv.onvif;
