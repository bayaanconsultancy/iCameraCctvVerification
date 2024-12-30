package com.cs.on.icamera.cctv.onvif;

import com.github.chengtengfei.onvif.discovery.SingleIPCDiscovery;
import com.github.chengtengfei.onvif.model.OnvifDeviceInfo;
import com.github.chengtengfei.onvif.model.ProfileInfo;
import com.github.chengtengfei.onvif.service.OnvifService;

import java.util.List;

public class Test {
    public static void doTry() {
        try {
            OnvifDeviceInfo onvifDeviceInfo = new OnvifDeviceInfo();
            onvifDeviceInfo.setIp("192.168.0.113");
            onvifDeviceInfo.setUsername("admin");
            onvifDeviceInfo.setPassword("Aminul@24");
            SingleIPCDiscovery.fillOnvifAddress(onvifDeviceInfo);
            List<ProfileInfo> profileInfoList = OnvifService.getVideoInfo(onvifDeviceInfo);
            System.out.println(profileInfoList);
        } catch (Exception e) {
            e.printStackTrace();
        }

// Output : [ProfileInfo{name='profile0', token='profile0', videoInfo=VideoInfo{videoEncoding='H264', videoWidth=1920, videoHeight=1080, frameRateLimit=25, bitrateLimit=4096, streamUri='rtsp://admin:admin@192.168.101.234:554/av0_0'}}, ProfileInfo{name='profile1', token='profile1', videoInfo=VideoInfo{videoEncoding='H264', videoWidth=704, videoHeight=576, frameRateLimit=25, bitrateLimit=1024, streamUri='rtsp://admin:admin@192.168.101.234:554/av0_1'}}]

    }
}
