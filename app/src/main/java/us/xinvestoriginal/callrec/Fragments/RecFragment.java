package us.xinvestoriginal.callrec.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.List;

import us.xinvestoriginal.callrec.Adapters.RecordAdapter;
import us.xinvestoriginal.callrec.Helpers.AlertHelper;
import us.xinvestoriginal.callrec.Models.RecordEntity;
import us.xinvestoriginal.callrec.Presenters.MainPresenter;
import us.xinvestoriginal.callrec.R;


/**
 * Created by x-inv on 26.04.2017.
 */

public class RecFragment extends Fragment implements IRec, View.OnClickListener{

    private static final String POS_KEY = "likedOnly";

    public static RecFragment getInstance(boolean likedOnly){
        RecFragment res = new RecFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(POS_KEY,likedOnly);
        res.setArguments(bundle);
        return res;
    }

    private View           rootView;
    private boolean       likedOnly;
    private MainPresenter presenter;
    private RecordAdapter   adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.filter_screen, container, false);
        progressVisible(true);
        likedOnly = getArguments().getBoolean(POS_KEY,false);
        presenter = MainPresenter.getInstance();
        presenter.takeFragmentView(likedOnly,this);
        return rootView;
    }


    @Override
    public void onRecords(List<RecordEntity> tracks) {
        if (tracks != null && tracks.size() > 0){
            rootView.findViewById(R.id.tvNoTracks).setVisibility(View.GONE);
            rootView.findViewById(R.id.rvList).setVisibility(View.VISIBLE);
            RecyclerView rvRecords = (RecyclerView)rootView.findViewById(R.id.rvList);

            int pos;
            int offsetTop;
            LinearLayoutManager layoutManager = (LinearLayoutManager) rvRecords.getLayoutManager();
            if (layoutManager != null){
                pos = layoutManager.findFirstVisibleItemPosition();
                View v = layoutManager.getChildAt(0);
                if (v != null) {
                    offsetTop = v.getTop();
                }else{
                    offsetTop = 0;
                }
            }else{
                pos = 0;
                offsetTop = 0;
            }

            rvRecords.setVisibility(View.VISIBLE);
            layoutManager = new LinearLayoutManager(getContext());
            rvRecords.setLayoutManager(layoutManager);
            adapter = new RecordAdapter(tracks,this,getContext());
            rvRecords.setAdapter(adapter);
            layoutManager.scrollToPositionWithOffset(pos, offsetTop);
        }else{
            rootView.findViewById(R.id.tvNoTracks).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.rvList).setVisibility(View.GONE);
        }
    }

    @Override
    public void progressVisible(boolean visible) {
        rootView.findViewById(R.id.pbRecords).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        final RecordEntity entity = (RecordEntity) v.getTag();
        if (entity == null) return;
        int id = v.getId();
        switch (id){
            case R.id.ivPlay:
                File file = new File(entity.path);
                if (!file.exists()){
                    AlertHelper.ShowCancelAlert(getActivity(),
                            getContext().getString(R.string.no_file_text),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    presenter.deleteRecord(entity);
                                }
                            });
                    return;
                }else{
                    presenter.onPlayClick(entity.path);
                }
                break;
            case R.id.ivLiked:
                entity.recLike = !entity.recLike;
                adapter.notifyDataSetChanged();
                presenter.onLikeClick(entity);
                break;
            case R.id.ivRecShare:
                presenter.onShareClick(entity.path);
                break;
        }
    }
}
