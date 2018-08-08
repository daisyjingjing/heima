package com.lj.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.lj.mobilesafe.R;
import com.lj.mobilesafe.view.SettingRelativeLayout;

/**
 * Created by daisy on 2018/8/8.
 */

public class SettingActivity extends Activity {
    private SettingRelativeLayout re_update;
    private SharedPreferences config;
    private boolean auto_update;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        config=getApplication().getSharedPreferences("config",MODE_PRIVATE);
        auto_update=config.getBoolean("auto_update",true);
        initViews();
    }
    private void initViews(){
        re_update=findViewById(R.id.re_update);
        re_update.initViews();
        re_update.setCheckd(auto_update);
        re_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(re_update.isCheck()){
                    re_update.setCheckd(false);
                    config.edit().putBoolean("auto_update",false).commit();
                }else{
                    re_update.setCheckd(true);
                    config.edit().putBoolean("auto_update",true).commit();
                }
            }
        });
    }

}
