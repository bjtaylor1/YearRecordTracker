package com.bjt.yearrecordtracker.app;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by ben on 11/01/15.
 */
public class Util {
    public static void closeQuietly(Closeable... toClose) {
        for(Closeable closeable : toClose) {
            try {
                if(closeable != null) {
                    closeable.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
