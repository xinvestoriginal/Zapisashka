package us.xinvestoriginal.callrec.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import us.xinvestoriginal.callrec.Models.NumberEntity;
import us.xinvestoriginal.callrec.R;

/**
 * Created by x-inv on 02.04.2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.CustomViewHolder> implements View.OnTouchListener{

    private View.OnClickListener listener;
    private List<NumberEntity> items;



    public ContactAdapter(List<NumberEntity> items, View.OnClickListener listener){
        this.items = items;
        this.listener = listener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.number_item,parent,false);
        return new CustomViewHolder(root,this);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        NumberEntity item = items.get(position);
        holder.tvContactName.setText(item.numName);
        holder.tvContactPhone.setText(item.numName.equals(item.phone) ? "" : item.phone);
        int enableId = item.enable ? R.drawable.ic_check_box_white_24dp :
                       R.drawable.ic_check_box_outline_blank_white_24dp;
        holder.ivContactEnable.setImageResource(enableId);
        holder.mainView.setTag(new Object[]{this,item});
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        private TextView  tvContactPhone;
        private TextView  tvContactName;
        private ImageView ivContactEnable;
        private View      mainView;


        public CustomViewHolder(View itemView, View.OnTouchListener listener) {
            super(itemView);
            mainView = itemView;
            itemView.setOnTouchListener(listener);
            ivContactEnable = (ImageView)itemView.findViewById(R.id.ivContactEnable);
            tvContactPhone = (TextView)itemView.findViewById(R.id.tvContactPhone);
            tvContactName  = (TextView)itemView.findViewById(R.id.tvContactName);
        }
    }
//----------------------------------------------------------------------------------------
    private float x,y;
    private final static float MAX_LENGTH_CLICK = 8;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            float d = Math.abs(x - event.getX()) + Math.abs( y - event.getY());
            if (d <= MAX_LENGTH_CLICK) listener.onClick(v);
        }
        return false;
    }
//----------------------------------------------------------------------------------------
}
