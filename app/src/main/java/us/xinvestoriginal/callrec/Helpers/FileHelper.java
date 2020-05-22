package us.xinvestoriginal.callrec.Helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import us.xinvestoriginal.callrec.Models.RecordEntity;

/**
 * Created by x-inv on 26.08.2017.
 */

public class FileHelper {

    public static boolean dirNotEmpty(){
        String mainPath = RecordHelper.mainDirectory();
        File mDir = new File(mainPath);
        if (!mDir.exists() || mDir.isFile()) return false;
        File[] files = mDir.listFiles();
        return files.length > 0;
    }

    private static String getExtension(String path){
        String res = ".";
        if (path == null) return res;
        String filenameArray[] = path.split("\\.");
        String extension = filenameArray[filenameArray.length-1];
        res += extension;
        return res;
    }

    public static List<RecordEntity> fileFromList(){
        String mainPath = RecordHelper.mainDirectory();
        File mDir = new File(mainPath);
        if (!mDir.exists() || mDir.isFile()) return null;
        File[] files = mDir.listFiles();
        if (files == null || files.length == 0) return null;
        List<RecordEntity> res = new ArrayList<>();
        for (int i = 0; i < files.length; i++){
             if (RecordHelper.AMR_EXTANSION.equals(getExtension(files[i].getAbsolutePath()))){
                 String fileName = files[i].getName().replace(RecordHelper.AMR_EXTANSION,"");
                 String[] parts = fileName.split("_");
                 if (parts.length == 4){
                     RecordEntity e = new RecordEntity();
                     e.path = files[i].getAbsolutePath();
                     e.recName = "";
                     e.phone = parts[0];
                     e.incomin = "1".equals(parts[3]);
                     try {
                         e.start = Long.parseLong(parts[1]);
                         e.id = e.start;
                         e.len = Long.parseLong(parts[2]);
                         //Log.e(">>>1",e.path);
                         res.add(e);
                     }catch (NumberFormatException ex){
                         LogHelper.print(e,ex.toString());
                     }
                 }
                 //Log.e("^^^",fileName);
            }
        }
        return res.size() > 0 ? res : null;
    }

}
