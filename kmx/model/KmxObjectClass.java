package com.k2data.kbc.kmxes.kmx.model;

import java.util.List;

public class KmxObjectClass {

    private Long objectClassId;

    private String objectClassName;

    private List<KmxObjectColumn> objectColumnInfoList;

    public Long getObjectClassId() {
        return objectClassId;
    }

    public void setObjectClassId(Long objectClassId) {
        this.objectClassId = objectClassId;
    }

    public String getObjectClassName() {
        return objectClassName;
    }

    public void setObjectClassName(String objectClassName) {
        this.objectClassName = objectClassName;
    }

    public List<KmxObjectColumn> getObjectColumnInfoList() {
        return objectColumnInfoList;
    }

    public void setObjectColumnInfoList(
        List<KmxObjectColumn> objectColumnInfoList) {
        this.objectColumnInfoList = objectColumnInfoList;
    }
}
