package us.xinvestoriginal.callrec.Activities;

/**
 * Created by x-inv on 09.09.2017.
 */

public interface IPlayerView {

    void setSeekBar(int pos);
    void setSeekBarMax(int seekBarMax);

    void setFileNameHeader();

    void close();
    void setActionImage(boolean isPause);

    String getPlayerPath();
}
