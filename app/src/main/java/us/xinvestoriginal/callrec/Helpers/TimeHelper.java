package us.xinvestoriginal.callrec.Helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by x-inv on 22.08.2017.
 */

public class TimeHelper {

    public static String lengthStr(long time){
        long hours = time / (60 * 60);
        long mins = (time - hours) / 60;
        long secs = time - hours * 60 * 60 - mins * 60;
        return String.format("%02d", hours) + ":" +
               String.format("%02d",  mins) + ":" + String.format("%02d",  secs);
    }

    public static String dateStr(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
        String dateString = formatter.format(new Date(time * 1000L));
        return dateString;
    }
}
