package us.xinvestoriginal.callrec.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by x-inv on 06.05.2017.
 */

public class SharedHelper {

    private static final int ENABLE_REC  = 1;
    private static final int DISABLE_REC = 2;
    private static final int CUSTOM_REC  = 3;

    private static final String NUMB_KEY        = "numb";
    private static final String ENABLE_REC_KEY  = "eRec";

    private static final String FIRST_START_KEY   = "first_start";
    private static final String TIME_START_KEY    = "time_start";
    private static final String BASE_VERSION_KEY  = "base_ver";

    public static SharedPreferences shared(Context c){
        return c.getSharedPreferences("settings.ini",Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor editor(Context c){
        return shared(c).edit();
    }

    public static boolean getNumbEnable(Context c, String numb){
        return numb == null ? false : shared(c).getBoolean(NUMB_KEY + numb,true);
    }

    public static void setNumbEnable(Context c, String numb, boolean enable){
        SharedPreferences.Editor e = editor(c);
        e.putBoolean(NUMB_KEY + numb,enable);
        e.commit();
    }

    public static boolean demoPeriodIsEnded(Context c){
        long firstRunTime = shared(c).getLong(TIME_START_KEY,0);
        if (firstRunTime == 0){
            firstRunTime = System.currentTimeMillis() / 1000L;
            SharedPreferences.Editor e = editor(c);
            e.putLong(TIME_START_KEY,firstRunTime);
            e.commit();
        }
        long DEMO_PERIOD = 7 * 24 * 60 * 60;
        long current_time = System.currentTimeMillis() / 1000L;
        return current_time - firstRunTime >= DEMO_PERIOD;
    }

    public static Boolean enableRecord(Context c){
        int eMode = shared(c).getInt(ENABLE_REC_KEY,ENABLE_REC);
        if (eMode == CUSTOM_REC) return null;
        return eMode == ENABLE_REC;
    }

    public static void setEnableRecord(Context c, Boolean eMode){
        SharedPreferences.Editor e = editor(c);
        if (eMode == null) {
            e.putInt(ENABLE_REC_KEY,CUSTOM_REC);
        }else{
            e.putInt(ENABLE_REC_KEY,eMode ? ENABLE_REC : DISABLE_REC);
        }
        e.commit();
    }

    public static boolean isFirstStart(Context c){
        boolean res = shared(c).getBoolean(FIRST_START_KEY,true);
        if (!res) return false;
        SharedPreferences.Editor e = editor(c);
        e.putBoolean(FIRST_START_KEY,false);
        e.commit();
        return true;
    }

    public static boolean needUpdateBase(Context c, int baseVersion){
        int oldVersion = shared(c).getInt(BASE_VERSION_KEY,0);
        if (baseVersion <= oldVersion) return false;
        SharedPreferences.Editor e = editor(c);
        e.putInt(BASE_VERSION_KEY,baseVersion);
        e.commit();
        return true;
    }
}
