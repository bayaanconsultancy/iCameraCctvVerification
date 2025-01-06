package com.cs.on.icamera.cctv;

import java.io.IOException;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.ffmpeg.Ffprobe;
import com.cs.on.icamera.cctv.onvif.OnvifDiscovery;
import com.cs.on.icamera.cctv.onvif.OnvifEnquiry;
import com.cs.on.icamera.cctv.swing.MainWindow;

public class Main {
    public static void main(String[] args) {
        

        //new MainWindow();
        OnvifDiscovery.discover();
        DataStore.setOnvifUsernamePassword("admin", "Aminul@24");
        OnvifEnquiry.enquire();
        DataStore.printDiscoveredCctvs();
        try {
            Ffprobe.get();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
