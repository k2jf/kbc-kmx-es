package com.k2data.kbc.kmx.es.mapper;

public class IndexMapping {

    private String kmxObjectClassName;

    /**
     * es索引名称
     */
    private String index;

    /**
     * es对应的ts字段
     */
    private String tsField;

    /**
     * es中对应的风场字段
     */
    private String wfField;

    /**
     * es中对应的风机字段
     */
    private String wtField;

    /**
     * es中对应的状态字段
     */
    private String statusField = "state";


    public String getStatusField() {
        return statusField;
    }

    public String getWfField() {
        return wfField;
    }

    public void setWfField(String wfField) {
        this.wfField = wfField;
    }

    public String getWtField() {
        return wtField;
    }

    public void setWtField(String wtField) {
        this.wtField = wtField;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTsField() {
        return tsField;
    }

    public void setTsField(String tsField) {
        this.tsField = tsField;
    }

    public String getKmxObjectClassName() {
        return kmxObjectClassName;
    }

    public void setKmxObjectClassName(String kmxObjectClassName) {
        this.kmxObjectClassName = kmxObjectClassName;
    }
}
