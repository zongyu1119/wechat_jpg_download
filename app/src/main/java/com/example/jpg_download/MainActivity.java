package com.example.jpg_download;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action)&&type!=null){
            if ("text/plain".equals(type)){
                try {
                    dealTextMessage(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(type.startsWith("image/")){
                dealPicStream(intent);
            }
        }else if (Intent.ACTION_SEND_MULTIPLE.equals(action)&&type!=null){
            if (type.startsWith("image/")){
                dealMultiplePicStream(intent);
            }
        }
    }

    void dealTextMessage(Intent intent) throws Exception {
        String share = intent.getStringExtra(Intent.EXTRA_TEXT);
        String title = intent.getStringExtra(Intent.EXTRA_TITLE);
        TextView tv_link=findViewById(R.id.tv_link);
        tv_link.setText(share);
        String url=share.substring(share.indexOf("http"));
        action(url);
    }

    void action(String url) throws Exception {
        bitmaps=new ArrayList<Bitmap>();
        String url_html=getHTML(url);
        TextView tv_html=findViewById(R.id.tv_html);
        tv_html.setText(url_html);
        ArrayList<String> src_list=get_src(url_html);
        for(String s:src_list){
            bitmaps.add(getBitMBitmap(s));
        }
        ListView lv=findViewById(R.id.lv_image);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_item,src_list);//listdata和str均可
        lv.setAdapter(arrayAdapter);
    }
    /***
     * 通过html获得src
     *
     *
     *
     */
    private ArrayList<String> get_src(String html){
        ArrayList<String> re_str=new ArrayList<String>() ;
        String[] html_lines=html.split("\n");
        for(int i=0;i<html_lines.length;i++){
            if(html_lines[i].contains("<img")&&html_lines[i].contains("src")){
                String src_end=html_lines[i].substring(html_lines[i].indexOf("src"));
                src_end=src_end.substring(src_end.indexOf("\""));
                int src_end_index=src_end.indexOf("\"");
                re_str.add(src_end.substring(0,src_end_index));
            }
        }
        return re_str;
    }

    ArrayList<Bitmap> bitmaps;
    /***
     * 获取HTML内容
     *
     * @param url
     * @return
     * @throws Exception
     */
    private String getHTML(String url) throws Exception {
        URL uri = new URL(url);
        URLConnection connection = uri.openConnection();
        InputStream in = connection.getInputStream();
        byte[] buf = new byte[1024];
        int length = 0;
        StringBuffer sb = new StringBuffer();
        while ((length = in.read(buf, 0, buf.length)) > 0) {
            sb.append(new String(buf));
        }
        in.close();
        return sb.toString();
    }

    /**通过图片url生成Bitmap对象
     * @param urlpath
     * @return Bitmap
     * 根据图片url获取图片对象
     */
    public static Bitmap getBitMBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
    /**通过图片url生成Drawable对象
     * @param urlpath
     * @return Bitmap
     * 根据url获取布局背景的对象
     */
    public static Drawable getDrawable(String urlpath){
        Drawable drawable = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            drawable = Drawable.createFromStream(in, "background.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    void dealPicStream(Intent intent){
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
    }

    void dealMultiplePicStream(Intent intent){
        ArrayList<Uri> arrayList = intent.getParcelableArrayListExtra(intent.EXTRA_STREAM);
    }
}
