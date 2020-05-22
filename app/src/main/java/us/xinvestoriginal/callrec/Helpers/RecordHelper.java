package us.xinvestoriginal.callrec.Helpers;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import us.xinvestoriginal.callrec.Models.RecordEntity;

/**
 * Created by x-inv on 20.08.2017.
 */

public class RecordHelper {

    private static final String REC_DIRECTORY = "AiRecords";
    public  static final String AMR_EXTANSION = ".amr";

    public static String mainDirectory(){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + REC_DIRECTORY;
    }

    private static MediaRecorder recorder = null;
    private static RecordEntity track = null;

    public static boolean start(final String phone, boolean isIncoming){

        if (recorder != null){
            return false;
        }

        recorder = new MediaRecorder();
        String path = mainDirectory();

        File recDir = new File(path);
        if (!recDir.exists() || recDir.isFile()) recDir.mkdir();

        long curTime = System.currentTimeMillis() / 1000L;
        path += "/" + phone + "_" +String.valueOf(curTime) + AMR_EXTANSION;

        File mediaFile = new File(path);
        if (mediaFile.exists()) mediaFile.delete();

        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        recorder.setOutputFile(path);
        track = null;

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("RecordHelper1",e.toString());
            recorder = null;
            return false;
        }
        try {
            recorder.start();
        }catch (IllegalStateException ex){
            Log.e("RecordHelper2",ex.toString());
            recorder = null;
            return false;
        }

        track = new RecordEntity();
        track.recName = "";
        track.recLike = false;
        track.phone     = phone;
        track.incomin   = isIncoming;
        track.path      = path;
        track.start     = curTime;
        track.id        = track.start;

        return true;
    }

    public static RecordEntity stop(){

        if (recorder != null){
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            if (track != null){
                track.len = System.currentTimeMillis() / 1000L - track.start;
                String newName = mainDirectory() + "/" + track.phone + "_" +
                                 String.valueOf(track.start) + "_" + String.valueOf(track.len) +
                                 "_" + String.valueOf(track.incomin ? "1" : "0") + ".amr";
                rename(track.path,newName);
                track.path = newName;
                return track;
            }
        }
        return null;
    }

    private static void rename(String oldName, String newName){
        File from = new File(oldName);
        File to = new File(newName);
        if(from.exists()) from.renameTo(to);
    }
}
