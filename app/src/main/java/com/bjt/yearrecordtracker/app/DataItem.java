package com.bjt.yearrecordtracker.app;

/**
 * Created by ben on 11/01/15.
 */
public class DataItem {
    private String key;
    private String url;
    private final HtmlStringResultParser parser;

    public DataItem(String key, String url, HtmlStringResultParser parser) {
        this.key = key;
        this.url = url;
        this.parser = parser;
    }

    public DataItem(String key) {
        this.key = key;
        parser = null;
        url = null;
    }

    public final String getUrl() {
        return url;
    }

    public final HtmlStringResultParser getParser() {
        return parser;
    }

    public final String getKey() {
        return key;
    }
}
