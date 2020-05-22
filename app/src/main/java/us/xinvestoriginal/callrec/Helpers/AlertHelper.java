package us.xinvestoriginal.callrec.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import us.xinvestoriginal.callrec.R;


/**
 * Created by x-invest on 29.07.2016.
 */
public class AlertHelper {


    public static void ShowAlert(final Activity activity, String text){ShowAlert(activity,text,true,null,null,false);}
    public static void ShowCancelAlert(final Activity activity, String text,  DialogInterface.OnClickListener okCallBack){
        ShowAlert(activity,text,false,okCallBack,null,true);
    }

    public static void ShowAlert(final Activity activity, String text,
                                 final boolean exit,
                                 DialogInterface.OnClickListener okCallBack, DialogInterface.OnClickListener noCallBack, boolean hasCancel){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(text);
        builder.setCancelable(false);
        if (okCallBack == null){
            builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (exit) activity.finish();
                }
            });
        }else{
            builder.setPositiveButton(activity.getString(R.string.ok), okCallBack);
        }
        if (hasCancel){
            builder.setNegativeButton(activity.getString(R.string.no), noCallBack);
        }

        AlertDialog alertMenu = builder.create();
        alertMenu.show();
    }

    public static void Toast(final Activity activity, int resource){
        Toast(activity,activity.getString(resource));
    }



    public static void Toast(final Activity activity, String text){
        Toast.makeText(activity,text,Toast.LENGTH_SHORT).show();
    }


}
