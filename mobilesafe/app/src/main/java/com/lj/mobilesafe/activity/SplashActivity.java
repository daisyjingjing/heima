package com.lj.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lj.mobilesafe.R;

public class SplashActivity extends Activity {
    private String tv_version;
    private int versionCode;
    private String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.tv_version)).setText("版本号:"+getVersion());
    }

    public String getVersion() {
        PackageManager packageManager = getPackageManager();
        try {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo("com.lj.mobilesafe", 0);
                versionCode = packageInfo.versionCode;
                versionName = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                //找不到对应的包名
                e.printStackTrace();
            }
        }finally{
            return versionName;
        }

    }
}
