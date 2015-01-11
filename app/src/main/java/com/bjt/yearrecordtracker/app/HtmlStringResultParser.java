package com.bjt.yearrecordtracker.app;

import org.jsoup.nodes.Document;

/**
 * Created by ben on 11/01/15.
 */
public interface HtmlStringResultParser {
    String getResult(Document htmlDoc);
}
