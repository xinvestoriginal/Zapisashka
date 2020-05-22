package us.xinvestoriginal.callrec.Activities;

import java.util.List;

import us.xinvestoriginal.callrec.Models.NumberEntity;

/**
 * Created by x-inv on 22.08.2017.
 */

public interface IMainView {
    void onNamesUpdated();
    void showFilterDialog(List<NumberEntity> items);
    void close();
    void share();
    void rate();

    void changeSearchContainerVisible();

    void playFileWithNativePlayer(String path);
    void playFileWithSystemPlayer(String path);

    void setSubtitle(int subtitle);

    void shareFile(String path);

    boolean isDemopack();

    String marketLink();
    String searchText();
}
