package com.cs.on.icamera.cctv.xssf;

public enum ExportColumns implements Columns {
    CCTV_NAME(1, "CCTV Name", String.class, "getName", "setName"),
    CCTV_IP(2, "IP Address", String.class, "getIp", "setIp"),
    INSIDE_ROOM(3, "Inside Room", Boolean.class, "isInsideRoom", "insideRoom"),
    RTSP_PORT(4, "RTSP Port", Integer.class, "getPort", "setPort"),
    RTSP_MAIN_URL(5, "Main Stream URL", String.class, "getMainStreamUrl", "setMainStreamUrl"),
    RTSP_SUB_URL(6, "Sub Stream URL", String.class, "getSubStreamUrl", "setSubStreamUrl"),
    MAKE_MODEL(7, "Make - Model", String.class, "getMakeModel", "setMakeModel"),
    SERIAL_N0(8, "Serial No", String.class, "getSerialNumber", "setSerialNumber");

    private final int index;
    private final String name;
    private final Class<?> type;
    private final String getter;
    private final String setter;

    ExportColumns(int index, String name, Class<?> type, String getter, String setter) {
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
