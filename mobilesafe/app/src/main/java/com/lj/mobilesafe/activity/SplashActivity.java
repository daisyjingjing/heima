package com.lj.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lj.mobilesafe.R;
import com.lj.mobilesafe.utils.StreamToString;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {
    private String tv_version;
    private int versionCode;
    private String versionName;
    private String mVersionName;
    private int mVersionCode;
    private String mDescription;
    private String mDownloadURL;
    public static final int URL_MATCH_CODE=0;
    public static final int IO_ERROR_CODE=1;
    public static final int JSON_MATCH_CODE=2;
    public static final int SHOW_DIALOG=3;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case URL_MATCH_CODE:
                    Toast.makeText(SplashActivity.this,"URL转换失败",Toast.LENGTH_SHORT).show();
                    break;
                case IO_ERROR_CODE:
                    Toast.makeText(SplashActivity.this,"数据流转换失败",Toast.LENGTH_SHORT).show();
                    break;
                case JSON_MATCH_CODE:
                    Toast.makeText(SplashActivity.this,"JSON转换失败",Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_DIALOG:
                    showDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.tv_version)).setText("版本号:"+getVersionName());
        checkVersion();
    }

    public String getVersionName() {
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
    public int getVersionCode() {
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
            return versionCode;
        }
    }
    public void checkVersion(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                HttpURLConnection conn=null;
                Message message=handler.obtainMessage();
                try {
                    try {
                        URL url = new URL("http://192.168.1.4:8080/update.json");
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setReadTimeout(3000);
                        conn.setConnectTimeout(5000);
                        //开始建立连接
                        conn.connect();
                        //请求成功
                        if (conn.getResponseCode() == 200) {
                            String response = StreamToString.getStringFromStream(conn.getInputStream());
                            JSONObject jsonObject = new JSONObject(response);
                            mVersionName = jsonObject.getString("versionName");
                            mVersionCode = jsonObject.getInt("versionCode");
                            mDescription = jsonObject.getString("description");
                            mDownloadURL = jsonObject.getString("downloadUrl");
                            if (mVersionCode > getVersionCode()) {
                                message.what=SHOW_DIALOG;
                            }
                        }
                    } catch (MalformedURLException e) {
                        //URL格式化错误
                        message.what=URL_MATCH_CODE;
                        e.printStackTrace();
                    } catch (IOException e) {
                        //IO出错
                        message.what=IO_ERROR_CODE;
                        e.printStackTrace();
                    } catch (JSONException e) {
                        //JSON格式化出错
                        message.what=JSON_MATCH_CODE;
                        e.printStackTrace();
                    }
                }finally {
                    if(conn!=null){
                        conn.disconnect();
                        conn=null;
                    }
                    handler.sendMessage(message);
                }
            }
        }.start();
    }
    public void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("最新版本"+mVersionName);
        builder.setMessage(mDescription);
        builder.setNegativeButton("以后再说",null);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("lj","立即更新");
            }
        });
        builder.show();
    }
}
