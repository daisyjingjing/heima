package com.lj.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daisy on 2018/8/6.
 */

public class StreamToString {
    public static String getStringFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        int length;
        byte[] buffer=new byte[1024];
        while((length=inputStream.read(buffer))!=-1){
            out.write(buffer,0,length);
        }
        inputStream.close();
        out.close();
        return out.toString();
    }
}
