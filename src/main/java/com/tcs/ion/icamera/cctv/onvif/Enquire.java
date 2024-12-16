package com.tcs.ion.icamera.cctv.onvif;

//import com.github.chengtengfei.onvif.discovery.SingleIPCDiscovery;
//import com.github.chengtengfei.onvif.model.OnvifDeviceInfo;
//import com.github.chengtengfei.onvif.model.ProfileInfo;
//import com.github.chengtengfei.onvif.service.OnvifService;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.util.ArrayList;
//import java.util.List;

public class Enquire {
//    private static final Logger logger = LogManager.getLogger(Enquire.class);
//
//    public static OnvifDeviceInfo getOnvifDevice(String ip, String username, String password) {
//        OnvifDeviceInfo onvifDevice = new OnvifDeviceInfo();
//        try {
//            onvifDevice.setIp(ip);
//            onvifDevice.setUsername(username);
//            onvifDevice.setPassword(password);
//            SingleIPCDiscovery.fillOnvifAddress(onvifDevice);
//        } catch (Exception e) {
//            logger.error("Error getting ONVIF device service address for IP {}:", ip, e);
//        }
//        return onvifDevice;
//    }
//
//    public static List<ProfileInfo> getProfiles(OnvifDeviceInfo onvifDevice) {
//        List<ProfileInfo> profileInfoList = new ArrayList<>();
//        try {
//            profileInfoList.addAll(OnvifService.getVideoInfo(onvifDevice));
//        } catch (Exception e) {
//            logger.error("Error getting ONVIF device profile information for device {}:", onvifDevice.getIp(), e);
//        }
//        return profileInfoList;
//    }
}
