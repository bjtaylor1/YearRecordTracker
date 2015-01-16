package com.bjt.yearrecordtracker.app;

import java.io.Serializable;

/**
 * Created by ben on 11/01/15.
 */
public class DataItemResult implements Serializable {
    private String key;
    private String result;

    public DataItemResult(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
