package us.xinvestoriginal.callrec.Fragments;

import java.util.List;

import us.xinvestoriginal.callrec.Models.RecordEntity;

/**
 * Created by x-inv on 25.08.2017.
 */

public interface IRec {
    void onRecords(List<RecordEntity> items);
    void progressVisible(boolean visible);
}
