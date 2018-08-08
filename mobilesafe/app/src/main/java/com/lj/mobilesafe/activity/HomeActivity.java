package com.lj.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lj.mobilesafe.R;

import org.w3c.dom.Text;

/**
 * Created by daisy on 2018/8/6.
 */

public class HomeActivity  extends Activity {
    private static final String TAG = "HomeActivity";
    private GridView gv_home;
    private String[] item_names=new String[]{"手机防盗","通讯卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具","设置中心"};
    private int[] item_images=new int[]{R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps,R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan,R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings};
    private SharedPreferences config;
    private String mPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_home);
        config=getApplication().getSharedPreferences("config",MODE_PRIVATE);
        gv_home=findViewById(R.id.gv_home);
        gv_home.setAdapter(new MyAdapter());
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 8:
                        Intent intent=new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                    case 0:
                        showPasswordDialog();
                        break;
                }
            }
        });
    }
    private void showPasswordDialog(){
        mPassword=config.getString("password",null);
        if(TextUtils.isEmpty(mPassword)){
            showSetPasswordDialog();
        }else{
            showInputPasswordDialog();
        }
    }

    private void showInputPasswordDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
        final Dialog dialog=builder.create();
        View v=View.inflate(HomeActivity.this,R.layout.dialog_input_password,null);
        dialog.show();
        dialog.setContentView(v);
        final EditText et_password=v.findViewById(R.id.et_input_password);
        Button btn_OK=v.findViewById(R.id.btn_OK);
        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=et_password.getText().toString();
                if(!TextUtils.isEmpty(password)){
                    if(password.equals(mPassword)){
                        //保存
                        Toast.makeText(HomeActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(HomeActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btn_cancel=v.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void showSetPasswordDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
        final Dialog dialog=builder.create();
        View v=View.inflate(HomeActivity.this,R.layout.dialog_set_password,null);
        dialog.show();
        dialog.setContentView(v);
        final EditText et_password=v.findViewById(R.id.et_password);
        final EditText et_confirmPassword=v.findViewById(R.id.et_confirm_password);
        Button btn_OK=v.findViewById(R.id.btn_OK);
        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=et_password.getText().toString();
                String confirmPassword=et_confirmPassword.getText().toString();
                if(!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(confirmPassword)){
                    if(password.equals(confirmPassword)){
                        //保存
                        config.edit().putString("password",password).commit();
                    }else{
                        Toast.makeText(HomeActivity.this,"两次密码不相等",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btn_cancel=v.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return item_names.length;
        }

        @Override
        public Object getItem(int position) {
            return item_names[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null) {
                View view = View.inflate(HomeActivity.this, R.layout.home_item, null);
                viewHolder=new ViewHolder();
                viewHolder.imageView=view.findViewById(R.id.iv_item);
                viewHolder.textView=view.findViewById(R.id.tv_item);
                view.setTag(viewHolder);
                convertView=view;
            }else{
                viewHolder= (ViewHolder) convertView.getTag();
            }
            viewHolder.imageView.setImageResource(item_images[position]);
            viewHolder.textView.setText(item_names[position]);
            return convertView;
        }
        class ViewHolder{
            public ImageView imageView;
            public TextView textView;
        }

    }

}
