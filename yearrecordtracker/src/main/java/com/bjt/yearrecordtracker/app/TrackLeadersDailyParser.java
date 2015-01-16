package com.bjt.yearrecordtracker.app;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by ben on 11/01/15.
 */
public class TrackLeadersDailyParser implements HtmlStringResultParser {
    @Override
    public String getResult(Document htmlDoc) {
        final Elements scrollBodyRows = htmlDoc.select("tr");
        for(Element row : scrollBodyRows) {
            Elements tds = row.select("td");
            for(int i = 0; i < tds.size(); i++) {
                if(tds.get(i).text().trim().toLowerCase().equals("today's estimated distance covered") &&
                        i < tds.size() - 1) {
                    return tds.get(i+1).text();
                }
            }
        }
        return "(no data)";
    }
}
