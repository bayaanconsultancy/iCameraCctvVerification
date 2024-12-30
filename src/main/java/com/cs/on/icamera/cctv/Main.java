package com.cs.on.icamera.cctv;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.onvif.OnvifDiscovery;
import com.cs.on.icamera.cctv.onvif.OnvifEnquiry;

public class Main {
    public static void main(String[] args) {
        OnvifDiscovery.discover();
        DataStore.printDiscoveredCctvs();
        DataStore.setOnvifUsernamePassword("admin", "Aminul@24");
        OnvifEnquiry.enquire();
    }
}
