package com.cs.on.icamera.cctv;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.onvif.OnvifDiscovery;
import com.cs.on.icamera.cctv.onvif.OnvifEnquiry;
import com.cs.on.icamera.cctv.onvif.Test;

public class Main {
    public static void main(String[] args) {
        OnvifDiscovery.discover();
        DataStore.printDiscoveredCctvs();
        DataStore.setUsernamePasswordForDiscoveredCctvs("admin", "Aminul@24");
        OnvifEnquiry.enquire();
        //Test.doTry();
    }
}
