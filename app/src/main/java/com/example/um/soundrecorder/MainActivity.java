package com.example.um.soundrecorder;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.Toast;



import java.io.File;

public class MainActivity extends Activity {

    private RadioButton button1,button2;
    private firstfragment fragment1;
    private secondsfragment fragment2;

    //申请录音权限变量
    private static final int GET_RECODE_AUDIO = 1;
    private static String[] PERMISSION_AUDIO = {
            Manifest.permission.RECORD_AUDIO
    };

    //申请读写权限变量
    private static int REQUEST_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //申请上网权限变量
    private static int REQUEST_INTERNET_CODE=1;
    private static String[] PERMISSION_INTERNET={
            Manifest.permission.INTERNET
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init(){
        button1=(RadioButton)findViewById(R.id.btn1);
        button2=(RadioButton)findViewById(R.id.btn2);
        showFragment(R.id.btn1);                                                    //选择录音和播放录音界面
        verifyAudioPermissions(MainActivity.this);                          //申请录音权限
        verifyStoragePermissions(MainActivity.this);                        //申请读写权限
        verifyInternetPermissions(MainActivity.this);                       //申请上网权限
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(R.id.btn1);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(R.id.btn2);
            }
        });
    }


    public void showFragment(int index) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        hideFragment(ft);
        switch (index) {
            case R.id.btn1:
                if (fragment1 == null) {
                    fragment1 = new firstfragment();
                    ft.add(R.id.layout, fragment1,"fragment1");
                } else {
                    ft.show(fragment1);
                }
                break;
            case R.id.btn2:
                if (fragment2 == null) {
                    fragment2 = new secondsfragment();
                    ft.add(R.id.layout, fragment2,"fragment2");
                } else {
                    fragment2.initQuery();
                    ft.show(fragment2);
                }
                break;
        }
        ft.commitAllowingStateLoss();

    }



    public void hideFragment(FragmentTransaction ft) {
        //如果不为空，就先隐藏起来
        if (fragment1 != null) {
            ft.hide(fragment1);
        }
        if (fragment2 != null) {
            ft.hide(fragment2);
        }
    }

    //申请录音权限
    public static void verifyAudioPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO);
        //检测是否有录音的权限
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //若没有录音权限，则会申请，会弹出对话框
            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
                    GET_RECODE_AUDIO);
        }
    }
    //申请读写权限
    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_PERMISSION_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //申请网络连接权限
    public static void verifyInternetPermissions(Activity activity){
        int permission=ActivityCompat.checkSelfPermission(activity,Manifest.permission.INTERNET);
        //检测是否有上网的权限
        if(permission!=PackageManager.PERMISSION_GRANTED){
            //若没有上网权限，则会申请，会弹出对话框
            ActivityCompat.requestPermissions(activity,PERMISSION_INTERNET,REQUEST_INTERNET_CODE);
        }
    }

}
