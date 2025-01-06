package com.cs.on.icamera.cctv.xssf;

public enum Columns {
    CCTV_NAME(1, "CCTV Name", String.class, "getName", "setName"),
    CCTV_IP(2, "IP Address", String.class, "getIp", "setIp"),
    RTSP_PORT(3, "RTSP Port", Integer.class, "getPort", "setPort"),
    INSIDE_ROOM(4, "Inside Room", Boolean.class, "getInsideRoom", "setInsideRoom"),
    RTSP_MAIN_URL(5, "Main Stream URL", String.class, "getOnvifUrl", "setOnvifUrl"),
    RTSP_SUB_URL(6, "Sub Stream URL", String.class, "getModel", "setModel"),
    MAKE_MODEL(7, "Make - Model", String.class, "getMake", "setMake"),
    SERIAL_N0(8, "Serial No", String.class, "getSerialNumber", "setSerialNumber"),
    ERROR(9, "Error Message", String.class, "getErrorMessage", "setErrorMessage");

    private final int index;
    private final String name;
    private final Class<?> type;
    private final String getter;
    private final String setter;

    Columns(int index, String name, Class<?> type, String getter, String setter) {
        this.index = index;
        this.name = name;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    public int index() {
        return index;
    }

    public String colName() {
        return name;
    }

    public Class<?> type() {
        return type;
    }

    public String getter() {
        return getter;
    }

    public String setter() {
        return setter;
    }
}
