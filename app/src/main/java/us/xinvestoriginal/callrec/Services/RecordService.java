package us.xinvestoriginal.callrec.Services;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import java.io.File;
import java.util.List;

import us.xinvestoriginal.callrec.Helpers.NotifiController;
import us.xinvestoriginal.callrec.Helpers.PhoneBookHelper;
import us.xinvestoriginal.callrec.Helpers.SharedHelper;
import us.xinvestoriginal.callrec.Models.NumberEntity;
import us.xinvestoriginal.callrec.Models.RecordEntity;
import us.xinvestoriginal.callrec.Presenters.MainPresenter;
import us.xinvestoriginal.callrec.SQLite.MBaseHelper;

public class RecordService extends Service {

    private static final int FOREGROUND_ID = 1;

    public interface BeginService {
        void onBeginService();
    }
    private static RecordService instance     = null;
    private static BeginService beginListener = null;


    private static boolean isServiceRunning(Context context) {
        Class<?> serviceClass = RecordService.class;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void InitFromReceiver(Context context) {
        if (instance == null) {
            Intent intent = new Intent(context, RecordService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }

    public static void Init(Context context, final BeginService listener) {
        if (instance == null || !isServiceRunning(context)) {
            beginListener = listener;
            Intent intent = new Intent(context, RecordService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }else{
            listener.onBeginService();
        }
    }

    public static boolean PutTrack(RecordEntity track, boolean needUpdateUI){
        if (instance == null) return false;
        instance.mBaseHelper.put(track);
        if (needUpdateUI) MainPresenter.updateRecordsUi(instance);
        return true;
    }

    public static List<RecordEntity> getTracks(String searchText, boolean emptyOnly){
        boolean likedOnly = false;
        return instance == null ?  null : emptyOnly ? instance.mBaseHelper.getEmptyTracks() :
                                                      instance.mBaseHelper.getTracks(searchText,likedOnly);
    }

    public static boolean getNumbEnable(String numb){
        if (instance == null || numb == null) return false;
        Boolean enableRec = SharedHelper.enableRecord(instance);
        if (enableRec != null) return enableRec;
        return SharedHelper.getNumbEnable(instance, PhoneBookHelper.uniTel(numb));
    }

    public static void setNumbEnable(String numb, boolean enable){
        if (instance != null && numb != null){
            SharedHelper.setNumbEnable(instance, PhoneBookHelper.uniTel(numb),enable);
        }
    }

    public static boolean isFirstStart() {
        if (instance == null) return false;
        return SharedHelper.isFirstStart(instance);
    }

    public static boolean needUpdateBase(){
        if (instance == null) return false;
        return SharedHelper.needUpdateBase(instance,MBaseHelper.BASE_VERSION);
    }

    public static boolean demoPeriodIsEnded(){
        if (instance == null) return false; else return SharedHelper.demoPeriodIsEnded(instance);
    }

    public static void delete(RecordEntity e){
        if (instance == null) return;
        File f = new File(e.path);
        if (f.exists()) f.delete();
        instance.mBaseHelper.delete(e);
    }

    public static void delete(boolean withoutLiked){
        if (instance == null) return;
        List<RecordEntity> items = instance.mBaseHelper.getTracks(null,false);
        if (items == null || items.size() == 0) return;
        for (RecordEntity e : items) {
            if (withoutLiked && e.recLike) continue;
            File f = new File(e.path);
            if (f.exists()) f.delete();
            instance.mBaseHelper.delete(e);
        }
    }

    public static Boolean enableRecord(){
        if (instance == null) return true;
        return SharedHelper.enableRecord(instance);
    }

    public static void setEnableRecord(Boolean eMode){
        if (instance != null){
            SharedHelper.setEnableRecord(instance,eMode);
        }
    }

    public static List<NumberEntity> getNumbFilter(){
        if (instance == null) return null;
        List<NumberEntity> res = PhoneBookHelper.ReadContactsFromPhone(instance);
        if (res == null || res.size() == 0) return null;
        for ( int i = 0; i < res.size(); i++){
            String phone = res.get(i).phone;
            res.get(i).enable = SharedHelper.getNumbEnable(instance, PhoneBookHelper.uniTel(phone));
        }
        return res;
    }

    private MBaseHelper mBaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mBaseHelper = new MBaseHelper(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(FOREGROUND_ID,NotifiController.createForegroundNotification(this));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;
        final BeginService l = beginListener;
        if (l != null) l.onBeginService();
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}