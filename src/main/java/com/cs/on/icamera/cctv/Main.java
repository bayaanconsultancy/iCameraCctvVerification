package com.cs.on.icamera.cctv;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.onvif.OnvifDiscovery;
import com.cs.on.icamera.cctv.onvif.OnvifEnquiry;
import com.cs.on.icamera.cctv.onvif.OnvifNetworkScan;

public class Main {
    public static void main(String[] args) {
        String username = System.getProperty("username", "admin");
        String password = System.getProperty("password", "Aminul@24");

        OnvifDiscovery.discover();
        OnvifNetworkScan.scan();
        DataStore.setOnvifCredential(username, password);
        OnvifEnquiry.enquire();
        DataStore.printIdentifiedCctvs();
    }
}
