package com.bjt.yearrecordtracker.app;

/**
 * Created by ben on 11/01/15.
 */
public class DataItemDefinitions {
    public static final DataItem[] ALL = {
            new DataItem("Steve 'Teethgrinder' Abraham"),
            new DataItem("YTD (Strava)", "http://www.strava.com/athletes/1419435", new StravaYTDParser()),
            new DataItem("Today (Trackleaders)", "http://trackleaders.com/oneyeartimetrial15i.php?name=Steve_Abraham", new TrackLeadersDailyParser()),

            new DataItem("Kurt 'Tarzan' Searvogel"),
            new DataItem("YTD (Strava)", "http://www.strava.com/athletes/350859", new StravaYTDParser()),
            new DataItem("Today (Trackleaders)", "http://trackleaders.com/oneyeartimetrial15i.php?name=Kurt_Tarzan_Searvogel", new TrackLeadersDailyParser()),

            new DataItem("William 'IronOx' Pruett"),
            new DataItem("YTD (Strava)", "http://www.strava.com/athletes/2527385", new StravaYTDParser()),
            new DataItem("Today (Trackleaders)", "http://trackleaders.com/oneyeartimetrial15i.php?name=William_IronOx_Pruett", new TrackLeadersDailyParser())
    };
}
