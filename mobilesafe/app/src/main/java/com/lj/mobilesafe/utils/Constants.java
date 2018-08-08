package com.lj.mobilesafe.utils;

/**
 * Created by daisy on 2018/8/8.
 */

public class Constants {

    public static final String host_ip="192.168.18.164";

    public static final String host_prefix="http://"+host_ip+":8080/";

    //下载APK的URL
    public static final String update_url=host_prefix+"app-debug.apk";

    //更新version的URL
    public static final String version_url=host_prefix+"update.json";

}
