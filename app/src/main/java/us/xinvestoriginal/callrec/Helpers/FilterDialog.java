package us.xinvestoriginal.callrec.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.LinearLayout;
import java.util.List;

import us.xinvestoriginal.callrec.Adapters.ContactAdapter;
import us.xinvestoriginal.callrec.Models.NumberEntity;
import us.xinvestoriginal.callrec.R;


/**
 * Created by x-invest on 16.05.2016.
 */

public class FilterDialog {

    public static Dialog show(Context context, List<NumberEntity> items,
                            View.OnClickListener listener,
                            DialogInterface.OnDismissListener disListener) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LayoutInflater lInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = lInflater.inflate(R.layout.filter_screen,null);

        Dialog dialog = new Dialog(context);
        dialog.addContentView(root,params);

        RecyclerView rvList = (RecyclerView) root.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        rvList.setAdapter(new ContactAdapter(items,listener));

        dialog.setOnDismissListener(disListener);

        dialog.show();
        return dialog;
    }


}
