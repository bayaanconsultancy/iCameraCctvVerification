package com.tcs.ion.icamera.cctv;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.onvif.Exploration;
import com.tcs.ion.icamera.cctv.onvif.OnvifDiscovery;
import com.tcs.ion.icamera.cctv.onvif.OnvifEnquiry;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Main {
    public static void main(String[] args) {
        OnvifDiscovery.discover();
        DataStore.printDiscoveredCctvs();
        DataStore.setUsernamePasswordForDiscoveredCctvs("admin", "Aminul@24");
        OnvifEnquiry.enquire();
    }
}
