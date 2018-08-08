package com.lj.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lj.mobilesafe.R;
import com.lj.mobilesafe.utils.Constants;
import com.lj.mobilesafe.utils.StreamToString;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
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
    public static final int ENTER_HOME=4;
    private TextView tv_update;
    private SharedPreferences config;
    private boolean auto_update;
    private static final String TAG = "SplashActivity";
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case URL_MATCH_CODE:
                    Toast.makeText(SplashActivity.this,"URL转换失败",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case IO_ERROR_CODE:
                    Toast.makeText(SplashActivity.this,"数据流转换失败",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case JSON_MATCH_CODE:
                    Toast.makeText(SplashActivity.this,"JSON转换失败",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case SHOW_DIALOG:
                    showDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.Ext.init(getApplication());
        //设置不输出日志
        x.Ext.setDebug(false);
        config=getApplication().getSharedPreferences("config",MODE_PRIVATE);
        auto_update=config.getBoolean("auto_update",true);
        tv_update=findViewById(R.id.tv_update);
        ((TextView)findViewById(R.id.tv_version)).setText("版本号:"+getVersionName());
        if(auto_update) {
            checkVersion();
        }else{
            //这里的handler自动处理了主线程以及子线程的转换
            handler.sendEmptyMessageDelayed(ENTER_HOME,2000);
        }
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
        final long startTime=System.currentTimeMillis();
        new Thread(){
            @Override
            public void run() {
                super.run();
                HttpURLConnection conn=null;
                Message message=handler.obtainMessage();
                try {
                    try {
                        URL url = new URL(Constants.version_url);
                        Log.i(TAG,"请求的地址是"+Constants.version_url);
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
                            }else {
                                message.what=ENTER_HOME;
                                Log.i(TAG,"version没有大于");
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
                    long endTime=System.currentTimeMillis();
                    long timeUsed=endTime-startTime;
                    long sleepTime=2000-timeUsed;
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
        final AlertDialog.Builder builder=new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("最新版本"+mVersionName);
        builder.setMessage(mDescription);
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("lj","立即更新");
                downLoadUpdateAPK();
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }
    public void enterHome(){
        Log.i(TAG,"enterHome被调用");
        Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
    private void downLoadUpdateAPK(){
        //判断是否已经挂载
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            RequestParams requestParams=new RequestParams(Constants.update_url);
            requestParams.setSaveFilePath(Environment.getExternalStorageDirectory()+ File.separator+"update.apk");
            requestParams.setAutoRename(false);
            x.http().get(requestParams, new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File result) {
                    Log.i(TAG,"下载成功");
                    tv_update.setVisibility(View.GONE);
                    Intent intent=new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setDataAndType(Uri.fromFile(result),"application/vnd.android.package-archive");
                    startActivityForResult(intent,0);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i(TAG,"下载失败");
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.i(TAG,"下载取消");
                }

                @Override
                public void onFinished() {
                    Log.i(TAG,"下载完成");
                }

                @Override
                public void onWaiting() {
                    Log.i(TAG,"等待下载");
                }

                @Override
                public void onStarted() {
                    Log.i(TAG,"开始下载");
                    //设置成显示
                    tv_update.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    Log.i(TAG,"正在下载中");
                    tv_update.setText("下载进度"+current/total*100+"%");
                }
            });
        }else{
            Toast.makeText(SplashActivity.this,"当前的SD卡还没有挂载",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                //这里是系统安装程序跳转回来的
                enterHome();
                break;
        }
    }
}
