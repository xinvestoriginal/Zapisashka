package us.xinvestoriginal.callrec.Presenters;

import android.media.MediaPlayer;
import android.os.Handler;

import java.io.IOException;

import us.xinvestoriginal.callrec.Activities.IPlayerView;
import us.xinvestoriginal.callrec.Helpers.LogHelper;

/**
 * Created by x-inv on 09.09.2017.
 */

public class PlayerPresenter implements IPresenter, Runnable {

    private static final int INTERVAL_MILLIS = 500;
    private static PlayerPresenter instance = null;
    public static PlayerPresenter getInstance(){
        if (instance == null) instance = new PlayerPresenter();
        return instance;
    }

    private IPlayerView view;
    private MediaPlayer mPlayer;
    private boolean isPause;
    private int mCurrentPosition;
    private Handler mHandler;

    private PlayerPresenter(){
        view = null;
        mPlayer = null;
        isPause = false;
        mHandler = new Handler();
        this.run();
    }

    public void seekTo(int pos){
        if (mPlayer != null){
            mPlayer.seekTo(pos * 1000);
        }
    }

    @Override
    public void takeView(Object v) {
        view = (IPlayerView) v;
        if (mPlayer == null){
            String path = view.getPlayerPath();
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(path);
            } catch (IOException e) {
                LogHelper.print(this,e.toString());
                view.close();
                return;
            }
            try {
                mPlayer.prepare();
            } catch (IOException e) {
                LogHelper.print(this,e.toString());
                view.close();
                return;
            }
            isPause = false;
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    final IPlayerView v = view;
                    if (v != null) v.close();
                }
            });
            int maxVolume = 50;
            int curVolume = 49;
            float log=(float)(Math.log(maxVolume-curVolume)/Math.log(maxVolume));
            mPlayer.setVolume(1-log,1-log);
            mPlayer.start();
        }
        view.setSeekBarMax(mPlayer.getDuration() / 1000);
        mCurrentPosition = mPlayer.getCurrentPosition() / 1000;

        view.setSeekBar(mCurrentPosition);
        view.setActionImage(isPause);
        view.setFileNameHeader();
        //int totalDuration = mPlayer.getDuration();
        //LogHelper.print(this,totalDuration);
        //LogHelper.print(this,mCurrentPosition);
    }

    @Override
    public void drop() {
        mHandler.removeCallbacks(this);
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        view = null;
        instance = null;
    }

    public void onActionClick(){

        isPause = !isPause;
        if (mPlayer != null){
            if (isPause) {
                mPlayer.pause();
                mCurrentPosition = mPlayer.getCurrentPosition() / 1000;
            } else {
                mPlayer.seekTo(mCurrentPosition * 1000);
                mPlayer.start();
            }
        }
        view.setActionImage(isPause);
    }


    @Override
    public void run() {
        //LogHelper.print(this,"run");
        if (mPlayer != null){
            final IPlayerView v = view;
            mCurrentPosition = mPlayer.getCurrentPosition() / 1000;
            if (v != null) v.setSeekBar(mCurrentPosition);
        }
        mHandler.postDelayed(this, INTERVAL_MILLIS);
    }
}
