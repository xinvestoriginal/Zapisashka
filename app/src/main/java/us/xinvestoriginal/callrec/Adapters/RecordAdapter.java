package us.xinvestoriginal.callrec.Adapters;


import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import us.xinvestoriginal.callrec.Helpers.TimeHelper;
import us.xinvestoriginal.callrec.Models.RecordEntity;
import us.xinvestoriginal.callrec.R;


/**
 * Created by x-inv on 02.04.2017.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.CustomViewHolder> implements View.OnTouchListener{

    private View.OnClickListener listener;
    private List<RecordEntity> items;

    private int inColor;
    private int outColor;
    private PorterDuff.Mode mode;

    public RecordAdapter(List<RecordEntity> items, View.OnClickListener listener, Context context){
        this.items = items;
        this.listener = listener;
        this.inColor = ContextCompat.getColor(context, R.color.colorIncoming);
        this.outColor = ContextCompat.getColor(context, R.color.colorOutcoming);
        this.mode = android.graphics.PorterDuff.Mode.MULTIPLY;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item,parent,false);
        return new CustomViewHolder(root,this);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        RecordEntity item = items.get(position);

        holder.tvName.setText(item.recName);
        holder.tvPhone.setText(item.recName.equals(item.phone) ? "" : item.phone);
        holder.tvDate.setText(TimeHelper.dateStr(item.start));
        holder.tvLen.setText(TimeHelper.lengthStr(item.len));

        holder.ivIncoming.setImageResource(item.incomin ? R.drawable.ic_call_received_white_24dp : R.drawable.ic_call_made_white_24dp);
        holder.ivIncoming.setColorFilter(item.incomin ? inColor : outColor, mode);
        holder.ivPlay.setTag(item);
        holder.ivLiked.setImageResource(item.recLike ? R.drawable.ic_star_white_24dp : R.drawable.ic_star_border_white_24dp);
        holder.ivLiked.setTag(item);
        holder.ivRecShare.setTag(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        private TextView tvPhone;
        private TextView tvLen;
        private TextView tvName;
        private TextView tvDate;

        private ImageView ivPlay;
        private ImageView ivIncoming;
        private ImageView ivLiked;
        private ImageView ivRecShare;

        public CustomViewHolder(View itemView, View.OnTouchListener listener) {
            super(itemView);

            ivPlay  = (ImageView) itemView.findViewById(R.id.ivPlay);
            ivPlay.setOnTouchListener(listener);

            ivIncoming  = (ImageView) itemView.findViewById(R.id.ivIncoming);
            ivIncoming.setClickable(false);
            ivIncoming.setFocusable(false);

            ivLiked  = (ImageView) itemView.findViewById(R.id.ivLiked);
            ivLiked.setOnTouchListener(listener);

            ivRecShare  = (ImageView) itemView.findViewById(R.id.ivRecShare);
            ivRecShare.setOnTouchListener(listener);

            tvPhone = (TextView)itemView.findViewById(R.id.tvPhone);
            tvPhone.setClickable(false);
            tvPhone.setFocusable(false);
            tvLen   = (TextView)itemView.findViewById(R.id.tvLen);
            tvLen.setClickable(false);
            tvLen.setFocusable(false);
            tvName  = (TextView)itemView.findViewById(R.id.tvName);
            tvName.setClickable(false);
            tvName.setFocusable(false);
            tvDate  = (TextView)itemView.findViewById(R.id.tvDate);
            tvDate.setClickable(false);
            tvDate.setFocusable(false);
        }
    }
//----------------------------------------------------------------------------------------
    private float x,y;
    private final static float MAX_LENGTH_CLICK = 8;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
        } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
            float d = Math.abs(x - event.getX()) + Math.abs( y - event.getY());
            if (d <= MAX_LENGTH_CLICK) listener.onClick(v);
        }
        return false;
    }
//----------------------------------------------------------------------------------------
}
