package com.lj.mobilesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lj.mobilesafe.R;

/**
 * Created by daisy on 2018/8/8.
 */

public class SettingRelativeLayout extends RelativeLayout {
    private static final String TAG = "SettingRelativeLayout";
    private String title;
    private String desc_on;
    private String desc_off;
    private TextView tv_title;
    private TextView tv_desc;
    private CheckBox cb_status;
    public SettingRelativeLayout(Context context) {
        super(context);
    }
    //指定属性的时候走这里
    public SettingRelativeLayout(Context context, AttributeSet attrs) {

        super(context, attrs);
        if(attrs!=null){
            TypedArray typedArray=null;
            try {
                typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SettingRelativeLayout);
                title = typedArray.getString(R.styleable.SettingRelativeLayout_title);
                desc_on = typedArray.getString(R.styleable.SettingRelativeLayout_desc_on);
                desc_off = typedArray.getString(R.styleable.SettingRelativeLayout_desc_off);
                Log.e(TAG,"结果是"+title+";"+desc_on+";"+desc_off);
            }finally {
                if(typedArray!=null){
                    //这里进行复用
                    typedArray.recycle();
                }
            }
        }
    }
    //指定属性以及style的时候走这里
    public SettingRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }
    public void initViews(){
        //将以下的布局填充
        View.inflate(getContext(), R.layout.setting_item,this);
        tv_title=findViewById(R.id.tv_top);
        tv_desc=findViewById(R.id.tv_bottom);
        cb_status=findViewById(R.id.cb_status);
        setTitle(title);
        tv_desc.setText(desc_on);
        cb_status.setChecked(true);
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public boolean isCheck(){
        return cb_status.isChecked();
    }

    public void setCheckd(boolean checkd){
        cb_status.setChecked(checkd);
        if(checkd){
            tv_desc.setText(desc_on);
        }else{
            tv_desc.setText(desc_off);
        }
    }

}
