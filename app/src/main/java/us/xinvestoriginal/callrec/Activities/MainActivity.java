package us.xinvestoriginal.callrec.Activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.List;

import us.xinvestoriginal.callrec.Adapters.ContactAdapter;
import us.xinvestoriginal.callrec.Adapters.GroupPageAdapter;
import us.xinvestoriginal.callrec.Helpers.AlertHelper;

import us.xinvestoriginal.callrec.Models.NumberEntity;
import us.xinvestoriginal.callrec.Presenters.MainPresenter;
import us.xinvestoriginal.callrec.Helpers.FilterDialog;
import us.xinvestoriginal.callrec.R;
import us.xinvestoriginal.callrec.Services.RecordService;

public class MainActivity extends AppCompatActivity implements RecordService.BeginService,
                                                               IMainView, TextWatcher {

    private MainPresenter presenter;
    private Dialog           dialog;
    private EditText   etSearchText;
    private AdView          mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = null;
        presenter = MainPresenter.getInstance();
        RecordService.Init(this,this);
        mAdView = findViewById(R.id.adView);
        if (isDemopack()){
            mAdView.setVisibility(View.VISIBLE);
            mAdView.loadAd(new AdRequest.Builder().build());
        }else{
            mAdView.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId){
            case R.id.share:
                share();
                break;
            case R.id.rate:
                rate();
                break;
            case R.id.clear_all:
                presenter.deleteRecords(false);
                break;
            case R.id.search:
                changeSearchContainerVisible();
                break;
            case R.id.clear_without_liked:
                presenter.deleteRecords(true);
                break;
            case R.id.rec_all:
                presenter.setEnableRecord(true);
                break;
            case R.id.rec_selected:
                presenter.setEnableRecord(null);
                break;
            case R.id.mFilter:
                presenter.showFilter();
                break;
            case R.id.rec_disable:
                presenter.setEnableRecord(false);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        int color = ContextCompat.getColor(this, R.color.colorNameText);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(color, mode);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    public void onBeginService() {
        if (presenter.isFirstStart() && presenter.dirNotEmpty()){
            DialogInterface.OnClickListener okClick = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    presenter.addAllTracksToBase(new MainPresenter.RecordSdLoaded() {
                        @Override
                        public void onLoad() {
                            presenter.takeView(MainActivity.this,MainActivity.this);
                        }
                    });
                }
            };
            DialogInterface.OnClickListener noClick = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    presenter.takeView(MainActivity.this,MainActivity.this);
                }
            };

            AlertHelper.ShowAlert(this, getString(R.string.need_add_tracks_to_base),
                    false, okClick, noClick,true);
        }else{
            presenter.takeView(this,this);
        }

        etSearchText = findViewById(R.id.etSearchText);
        findViewById(R.id.ibSearchClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearchText.setText("");
                changeSearchContainerVisible();
            }
        });

        etSearchText.addTextChangedListener(this);
    }

    @Override
    public void onNamesUpdated() {
        final ViewPager vpMain = findViewById(R.id.vpMain);
        vpMain.setAdapter(new GroupPageAdapter(getSupportFragmentManager(),this));
        TabLayout tlGroup = findViewById(R.id.tlMain);
        tlGroup.setupWithViewPager(vpMain);
    }

    @Override
    protected void onPause() {
        if (dialog != null) dialog.dismiss();
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenter == null) {
            finish();
        }else{
            if (mAdView != null) {
                mAdView.resume();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void showFilterDialog(List<NumberEntity> items) {
        dialog = FilterDialog.show(this, items, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object[] tags = (Object[]) v.getTag();
                ContactAdapter adapter = (ContactAdapter)tags[0];
                NumberEntity entity = (NumberEntity)tags[1];
                entity.enable = !entity.enable;
                adapter.notifyDataSetChanged();
                presenter.onNewPhoneEnableState(entity.phone, entity.enable);
            }
        }, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                MainActivity.this.dialog = null;
            }
        });
    }

    @Override
    public void close() {
        presenter.drop();
        finish();
    }

    @Override
    public void share() {
        String header = getString(R.string.share_header);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, header);
        String link = marketLink();
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " " + link + " :)");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_text)));
    }

    @Override
    public void rate() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(marketLink())));
        }
    }

    @Override
    public void changeSearchContainerVisible() {
        View llSearchContainer = findViewById(R.id.llSearchContainer);
        boolean isVisible = llSearchContainer.getVisibility() == View.VISIBLE;
        isVisible = !isVisible;
        llSearchContainer.setVisibility(isVisible ? View.VISIBLE: View.GONE);
    }

    @Override
    public void playFileWithNativePlayer(String path) {
        Intent i = new Intent(this,PlayerActivity.class);
        i.putExtra(PlayerActivity.PATH_KEY,path);
        startActivity(i);
    }

    @Override
    public void playFileWithSystemPlayer(String path) {
        String newMimeType = "";
        try {

            //launch intent
            Intent i = new Intent(Intent.ACTION_VIEW);

            Uri uri = Uri.fromFile(new File(path));
            String url = uri.toString();

            //grab mime
            newMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl(url));

            i.setDataAndType(uri, newMimeType);
            startActivity(i);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //Log.e("(((",e.toString());
            String message = getString(R.string.no_app_for_file) + " " + newMimeType;
            AlertHelper.Toast(this,message);
        }
    }

    @Override
    public void setSubtitle(int subtitle) {
        getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void shareFile(String path) {
        File file = new File(path);
        if (file.exists()){
            Uri uri = Uri.parse(path);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/*");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, getString(R.string.share_audio)));
        }
    }

    @Override
    public boolean isDemopack() {
        return !getApplicationContext().getPackageName().equals("us.xinvestoriginal.callrec");
        //return false;
    }

    @Override
    public String marketLink() {
        return "http://play.google.com/store/apps/details?id=" + getPackageName();
    }

    @Override
    public String searchText() {
        return etSearchText != null ? etSearchText.getText().toString() : "";
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        presenter.loadRecords();
    }
}
