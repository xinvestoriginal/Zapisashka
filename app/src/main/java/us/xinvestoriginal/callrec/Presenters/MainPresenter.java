package us.xinvestoriginal.callrec.Presenters;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import us.xinvestoriginal.callrec.Activities.IMainView;
import us.xinvestoriginal.callrec.Fragments.IRec;
import us.xinvestoriginal.callrec.Helpers.FileHelper;
import us.xinvestoriginal.callrec.Helpers.PhoneBookHelper;
import us.xinvestoriginal.callrec.Models.NumberEntity;
import us.xinvestoriginal.callrec.Models.RecordEntity;
import us.xinvestoriginal.callrec.R;
import us.xinvestoriginal.callrec.Services.RecordService;

/**
 * Created by x-inv on 22.08.2017.
 */

public class MainPresenter {

    public interface RecordSdLoaded{
        void onLoad();
    }

    private static MainPresenter instance = null;
    public static synchronized MainPresenter getInstance(){
        if (instance == null) instance = new MainPresenter();
        return instance;
    }

    private MainPresenter(){
        likedFragmentView = null;
        allFragmentView   = null;
    }

    public boolean isFirstStart(){
        return RecordService.isFirstStart();
    }

    //public boolean needBaseUpdate(){
    //    return RecordService.needUpdateBase();
    //}


    public boolean dirNotEmpty(){
        return FileHelper.dirNotEmpty();
    }

    public static boolean updateRecordsUi(Context context){
        if (instance != null){
            instance.asyncUpdateRecords(context);
            return true;
        }else{
            return false;
        }
    }

    private IMainView         view;
    private IRec likedFragmentView;
    private IRec   allFragmentView;

    public void onPlayClick(String path){
        if (view != null){
            view.playFileWithNativePlayer(path);
            //view.playFileWithSystemPlayer(path);
        }
    }

    public void takeView(IMainView v, final Context context){
        view = v;
        //if (needBaseUpdate()){
            //LogHelper.print(this,"need update");
            //addAllTracksToBase(new RecordSdLoaded() {
                //@Override
                //public void onLoad() {
                   // asyncUpdateRecords(context);
                    //updateSubtitle();
                //}
            //});
        //}else{
            asyncUpdateRecords(context);
            updateSubtitle();
        //}
    }

    public void takeFragmentView(boolean likedOnly, IRec view){
        if (likedOnly) likedFragmentView = view; else allFragmentView = view;
        if (likedFragmentView != null && allFragmentView != null) loadRecords();
    }

    public void drop(){
        view = null;
        instance = null;
    }

    public void loadRecords(){
        final String searchText = view != null ? view.searchText() : "";
        if (likedFragmentView != null) likedFragmentView.progressVisible(true);
        if (allFragmentView != null) allFragmentView.progressVisible(true);
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                return RecordService.getTracks(searchText, false);
            }
            @Override
            protected void onPostExecute(Object params) {
                final IRec lView = likedFragmentView;
                final IRec aView = allFragmentView;
                if (lView != null && aView != null) {
                    aView.onRecords((List<RecordEntity>) params);
                    lView.onRecords(likeOnly((List<RecordEntity>) params));
                    lView.progressVisible(false);
                    aView.progressVisible(false);
                }
            }
        }.execute();
    }

    public void asyncUpdateRecords(final Context context){
        if (likedFragmentView != null) likedFragmentView.progressVisible(true);
        if (allFragmentView != null) allFragmentView.progressVisible(true);
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                UpdateEmptyNumbers(context);
                return null;
            }
            @Override
            protected void onPostExecute(Object params) {
                final IMainView v = view;
                if (v != null) {
                    v.onNamesUpdated();
                }
                final IRec lView = likedFragmentView;
                final IRec aView = allFragmentView;
                if (lView != null && aView != null) {
                    lView.progressVisible(false);
                    aView.progressVisible(false);
                }
            }
        }.execute();
    }

    private void UpdateEmptyNumbers(Context context){
        List<RecordEntity> emptyRecords = RecordService.getTracks(null, true);
        if (emptyRecords == null || emptyRecords.size() == 0) return;
        for (RecordEntity e : emptyRecords){
            String name = PhoneBookHelper.getContactName(context,e.phone);
            e.recName = name != null ? name : e.phone;
            RecordService.PutTrack(e,false);
        }
    }

    private List<RecordEntity> likeOnly(List<RecordEntity> source){
        if (source == null) return null;
        List<RecordEntity> res = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            RecordEntity e = source.get(i);
            if (e.recLike) res.add(e);
        }
        return res.size() > 0 ? res : null;
    }

    public void onLikeClick(RecordEntity entity){
        RecordService.PutTrack(entity,false);
        loadRecords();
    }

    public void deleteRecord(RecordEntity e){
        RecordService.delete(e);
        loadRecords();
    }

    public void deleteRecords(final boolean withoutLiked){
        if (likedFragmentView != null) likedFragmentView.progressVisible(true);
        if (allFragmentView != null) allFragmentView.progressVisible(true);
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                RecordService.delete(withoutLiked);
                return null;
            }
            @Override
            protected void onPostExecute(Object params) {
                loadRecords();
            }
        }.execute();
    }

    private void updateSubtitle(){
        if (view != null){
            Boolean enableRecords = RecordService.enableRecord();
            int msgId = enableRecords == null ? R.string.record_selected :
                    enableRecords ? R.string.record : R.string.not_record;
            view.setSubtitle(msgId);
        }
    }

    public void setEnableRecord(Boolean eMode){
        RecordService.setEnableRecord(eMode);
        updateSubtitle();
    }

    public void onNewPhoneEnableState(String phone, boolean state){
        RecordService.setNumbEnable(phone,state);
    }

    public void addAllTracksToBase(final RecordSdLoaded listener){
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                List<RecordEntity> list = FileHelper.fileFromList();
                if (list != null && list.size() > 0){
                    for (RecordEntity e : list) RecordService.PutTrack(e,false);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object params) {
                listener.onLoad();
            }
        }.execute();
    }

    public void showFilter(){
        if (likedFragmentView != null) likedFragmentView.progressVisible(true);
        if (allFragmentView != null) allFragmentView.progressVisible(true);
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                return RecordService.getNumbFilter();
            }

            @Override
            protected void onPostExecute(Object params) {
                List<NumberEntity> items = (List<NumberEntity>) params;
                final IMainView v = view;
                if (v != null && items != null) {
                    v.showFilterDialog(items);
                }
                final IRec lView = likedFragmentView;
                final IRec aView = allFragmentView;
                if (lView != null && aView != null) {
                    lView.progressVisible(false);
                    aView.progressVisible(false);
                }
            }
        }.execute();
    }

    public boolean demoPeriodIsEnded(){
        return RecordService.demoPeriodIsEnded();
    }

    public void onShareClick(String filePath){
        if (view != null) view.shareFile(filePath);
    }
}
