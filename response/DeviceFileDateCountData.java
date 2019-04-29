package com.k2data.kbc.kmxes.response;

public class DeviceFileDateCountData {

    /**
     * 数据类型
     */
    private String dataCategory;

    /**
     * 设备id
     */
    private String device;

    /**
     * 日期：yyyy-MM-dd
     */
    private String day;

    /**
     * 总数
     */
    private long statCount;

    public DeviceFileDateCountData() {

    }

    public DeviceFileDateCountData(String dataCategory, String device, String day, long statCount) {
        this.dataCategory = dataCategory;
        this.device = device;
        this.day = day;
        this.statCount = statCount;
    }

    public String getDataCategory() {
        return dataCategory;
    }

    public void setDataCategory(String dataCategory) {
        this.dataCategory = dataCategory;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public long getStatCount() {
        return statCount;
    }

    public void setStatCount(long statCount) {
        this.statCount = statCount;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
