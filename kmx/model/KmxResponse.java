package com.k2data.kbc.kmxes.kmx.model;

public class KmxResponse<T> {

    private String message;

    private T data;

    private boolean isFaild;

    public boolean isSuccessed() {
        return !isFaild;
    }

    public void faild() {
        this.isFaild = true;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
