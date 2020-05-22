package us.xinvestoriginal.callrec.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import us.xinvestoriginal.callrec.Helpers.AlertHelper;
import us.xinvestoriginal.callrec.R;

public class StartActivity extends AppCompatActivity implements IStartView {

    private static final String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                  Manifest.permission.READ_PHONE_STATE,
                                                  Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS };
    private int currentPermissionIndex;
    private static int REQUEST_PERMISSION_PHONE_STATE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        currentPermissionIndex = 0;
        boolean needDisclaimerScreen = false;
        for (String permission : permissions) {
            if (!needDisclaimerScreen && Build.VERSION.SDK_INT >= 23 &&
                    checkSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) needDisclaimerScreen = true;
        }
        if (needDisclaimerScreen){
            showDisclaimerAlert();
        }else{
            loadPermissions();
        }
    }

    private void showDisclaimerAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.attention_text)
                .setMessage(R.string.disclaimer_text)
                .setIcon(R.drawable.icongray)
                .setCancelable(false)
                .setNegativeButton("OK.",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                loadPermissions();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_PERMISSION_PHONE_STATE) return;

        if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            onNoPermissions();
        }else{
            currentPermissionIndex++;
            loadPermissions();
        }
    }

    @Override
    public void loadPermissions() {

        boolean requestPermission = false;
        while (!requestPermission && currentPermissionIndex < permissions.length){
            boolean hasPermission = hasPermission(permissions[currentPermissionIndex]);
            if (hasPermission) {
                currentPermissionIndex++;
            }else{
                requestPermission = true;
            }
        }

        boolean isObtained = currentPermissionIndex == permissions.length && !requestPermission;
        if (isObtained) onPermissionObtain();

    }

    @Override
    public void onPermissionObtain() {
        startActivity(new Intent(this,MainActivity.class));
        close();
    }

    @Override
    public void onNoPermissions() {
        AlertHelper.ShowAlert(this,getString(R.string.needPermissions));
    }



    @Override
    public boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{permission},
                    REQUEST_PERMISSION_PHONE_STATE);
            return false;
        }else{
            return true;
        }
    }

}
