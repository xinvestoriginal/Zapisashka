package us.xinvestoriginal.callrec.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import us.xinvestoriginal.callrec.Presenters.PlayerPresenter;
import us.xinvestoriginal.callrec.R;

public class PlayerActivity extends AppCompatActivity implements IPlayerView, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    public static final String PATH_KEY = "audioPath";

    private PlayerPresenter presenter;
    private SeekBar          sbPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        sbPlayer = (SeekBar)findViewById(R.id.sbPlayer);
        sbPlayer.setOnSeekBarChangeListener(this);

        ImageButton ibPlayAction = (ImageButton)findViewById(R.id.ibPlayAction);
        ibPlayAction.setOnClickListener(this);

        ImageButton ibClosePlayer = (ImageButton)findViewById(R.id.ibClosePlayer);
        ibClosePlayer.setOnClickListener(this);

        findViewById(R.id.llMainPlayer).setOnClickListener(this);

        presenter = PlayerPresenter.getInstance();
        presenter.takeView(this);

    }

    @Override
    public void onBackPressed(){
        close();
    }

    @Override
    public void setSeekBar(int pos) {
        sbPlayer.setProgress(pos);
    }

    @Override
    public void setSeekBarMax(int seekBarMax) {
        sbPlayer.setMax(seekBarMax);
    }

    @Override
    public void setFileNameHeader() {
        TextView tvPlayerFileName = (TextView)findViewById(R.id.tvPlayerFileName);
        tvPlayerFileName.setText(getPlayerPath());
    }

    @Override
    public void close() {
        presenter.drop();
        finish();
    }

    @Override
    public void setActionImage(boolean isPause) {
        ImageButton ibPlayAction = (ImageButton)findViewById(R.id.ibPlayAction);
        ibPlayAction.setImageResource(!isPause ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    @Override
    public String getPlayerPath() {
        return getIntent().getStringExtra(PATH_KEY);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        presenter.seekTo(seekBar.getProgress());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.ibPlayAction:
                presenter.onActionClick();
                break;
            case R.id.ibClosePlayer:
                close();
                break;
            case R.id.llMainPlayer:
                close();
                break;
        }
    }
}
