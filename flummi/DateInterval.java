package com.k2data.kbc.kmx.es.flummi;

public enum DateInterval {
    YEAR("year"),
    MONTH("month"),
    DAY("day");

    private String name;

    DateInterval(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
