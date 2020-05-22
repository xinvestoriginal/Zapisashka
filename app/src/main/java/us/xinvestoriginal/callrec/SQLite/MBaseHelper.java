package us.xinvestoriginal.callrec.SQLite;

import android.content.Context;


import java.util.List;

import us.xinvestoriginal.callrec.Models.RecordEntity;


/**
 * Created by x-invest on 18.02.2016.
 */
public class MBaseHelper extends UniversalBaseHelper {

    public  static final int BASE_VERSION = 7;
    private static final String BASE_NAME = "records2.db";

    public MBaseHelper(Context context) {
        super(context,BASE_NAME,BASE_VERSION);
    }

    @Override
    protected Class[] getClasses() {
        return new Class[]{ RecordEntity.class };
    }


    public List<RecordEntity> getTracks(String searchText, boolean likedOnly){
        List<RecordEntity> items;
        if (searchText != null && searchText.length() > 0){
            String[] cols;
            String[] oprs;
            String[] vals;
            if (likedOnly){
                cols = new String[]{"recLike", "recName"};
                oprs = new String[]{"=", " LIKE "};
                vals = new String[]{"1", "%" + searchText + "%"};
            }else{
                cols = new String[]{"recName"};
                oprs = new String[]{" LIKE "};
                vals = new String[]{"%" + searchText + "%"};
            }
            items = (List<RecordEntity>)(Object)select(RecordEntity.class,cols,oprs,vals,null);
        }else{
            items = !likedOnly ?
                    (List<RecordEntity>)(Object)select(RecordEntity.class,null,null,null,null) :
                    (List<RecordEntity>)(Object)select(RecordEntity.class,new String[]{"recLike"},
                            new String[]{"="},new String[]{"1"},null);
        }

        return items;
    }

    public List<RecordEntity> getEmptyTracks(){
        List<RecordEntity> items =
                (List<RecordEntity>)(Object)select(RecordEntity.class,new String[]{"recName"},
                                                   new String[]{"="},new String[]{""},null);
        return items;
    }


}
