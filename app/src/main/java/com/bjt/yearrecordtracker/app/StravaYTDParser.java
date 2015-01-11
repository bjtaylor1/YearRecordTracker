package com.bjt.yearrecordtracker.app;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by ben on 11/01/15.
 */
public class StravaYTDParser implements HtmlStringResultParser {

    @Override
    public String getResult(Document htmlDoc) {
        final Elements athleteRecords = htmlDoc.select(".row.athlete-records");
        final Elements divs = athleteRecords.select("div");
        for(Element div : divs) {
            String h2Text = div.select("h2").text();
            if(h2Text.toLowerCase().equals("year-to-date")) {
                Elements tds = div.select("td");
                if(!tds.isEmpty()) return tds.first().text();
            }
        }
        return "(no data)";
    }
}
