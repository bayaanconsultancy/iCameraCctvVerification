package com.tcs.ion.icamera.cctv.xssf;

public enum TemplateColumns implements Columns {
    CCTV_NAME(1, "CCTV Name", String.class, "getName", "setName"),
    INSIDE_ROOM(2, "Inside Room", Boolean.class, "isInsideRoom", "insideRoom"),
    CCTV_IP(3, "IP Address", String.class, "getIp", "setIp"),
    USERNAME(4, "Username", String.class, "getUsername", "setUsername"),
    PASSWORD(5, "Password", String.class, "getPassword", "setPassword"),
    RTSP_MAIN_URL(6, "Main Stream URL", String.class, "getMainStreamUrl", "setMainStreamUrl"),
    RTSP_SUB_URL(7, "Sub Stream URL", String.class, "getSubStreamUrl", "setSubStreamUrl"),
    MAKE_MODEL(8, "Make - Model", String.class, "getMakeModel", "setMakeModel"),
    SERIAL_N0(9, "Serial No", String.class, "getSerialNumber", "setSerialNumber"),
    ERROR(10, "Error", String.class, "getError", "noNeedToSetError");

    private final int index;
    private final String name;
    private final Class<?> type;
    private final String getter;
    private final String setter;

    TemplateColumns(int index, String name, Class<?> type, String getter, String setter) {
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
