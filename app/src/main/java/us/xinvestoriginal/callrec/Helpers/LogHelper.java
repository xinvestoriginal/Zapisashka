package us.xinvestoriginal.callrec.Helpers;

import android.util.Log;

/**
 * Created by x-inv on 27.03.2017.
 */

public class LogHelper {
    private static final boolean ENABLE_DEBUG = true;

    public static void print(Class c,String text){
        if (ENABLE_DEBUG) Log.e(c.getName(),text);
    }

    public static void print(Object tag,Object data){
        print(tag.getClass(),String.valueOf(data));
    }
}
