package com.example.um.soundrecorder;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.os.DeadObjectException;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class firstfragment extends Fragment {

    private Button start,pause;
    private Chronometer chronometer;
    private EditText et;
    private boolean mBound = false;
    private AlertDialog alertDialog;
    private String truename;
    private MediaSQL sql;
    private SQLiteDatabase db;
    private boolean isopen=true,isBound,ispause=true;
    private ArrayList<String> arr_name;
    private long recordingTime = 0;// 记录下来的总时间
    private Recorder mService;
    private Intent intent;
    public firstfragment() {
        // Required empty public constructor
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Recorder.LocalBinder binder = (Recorder.LocalBinder) service;
            mService = binder.getService();
            mService.startRecording(truename);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_firstfragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    @SuppressLint("Range")
    public void init(){
        start=(Button)getView().findViewById(R.id.btn_record);
        pause=(Button)getView().findViewById(R.id.pause);
        chronometer=(Chronometer)getView().findViewById(R.id.chronometer);
        arr_name=new ArrayList<>();
        sql = new MediaSQL(getContext());
        db = sql.getWritableDatabase();
        initname();
    }

    private void initname() {
        Cursor cursor = db.query("MEDIA", null, null, null, null, null, null);
        arr_name.clear();
        if (cursor.moveToFirst()) {
            do {
                arr_name.add(cursor.getString(cursor.getColumnIndex("medianame")));
            } while (cursor.moveToNext());
            Collections.sort(arr_name);
            cursor.close();
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(getActivity(),Recorder.class);
                initname();
                if(isopen){
                    File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
                    if (!folder.exists()) {
                        folder.mkdir();
                    }
                    setFileName();
                    ((Button)v).setBackground(getResources().getDrawable(R.drawable.onrecord));
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"录音结束!",Toast.LENGTH_SHORT).show();
                    recordingTime = 0;
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.stop();
                    //getActivity().stopService(intent);
                    if (mBound) {
                        getContext().unbindService(mConnection);
                        mBound = false;
                    }
                    ((Button)v).setBackground(getResources().getDrawable(R.drawable.record));
                }
                isopen=!isopen;
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mService!=null){
                    if(isopen==false){
                        if(ispause){
                            chronometer.stop();
                            recordingTime = SystemClock.elapsedRealtime()- chronometer.getBase();
                            mService.pauserecorder();
                            ((Button)v).setBackground(getResources().getDrawable(R.drawable.continu));
                        }
                        else{
                            chronometer.setBase(SystemClock.elapsedRealtime() - recordingTime);// 跳过已经记录了的时间，起到继续计时的作用
                            chronometer.start();
                            mService.pauserecorder();
                            ((Button)v).setBackground(getResources().getDrawable(R.drawable.pause));
                        }
                        ispause=!ispause;
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(),"录音没有开始！无法暂停",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"录音没有开始！无法暂停",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setFileName() {
        et = new EditText(getContext());
        Log.d("ummmm","3");
        alertDialog = new AlertDialog.Builder(getContext()).setTitle("音频命名")
                .setMessage("请为你的音频命名")
                //添加输入框
                .setView(et)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .show();
        Log.d("ummmm","4");
        //拿到按钮并判断是否是POSITIVEBUTTON，然后我们自己实现监听
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ummmm","5");
                truename = et.getText().toString();
                Log.d("ummmm","6");
                if (truename.equals("")) {
                    Toast.makeText(getContext(), "内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                } else if (arr_name.contains(truename)) {
                    Toast.makeText(getContext(), "该音频名字已存在!", Toast.LENGTH_SHORT).show();
                    et.setText("");
                } else {
                    truename= et.getText().toString();
                    alertDialog.cancel();
                    startrecorder();
                }
            }
        });
    }

    private void startrecorder() {
        Toast.makeText(getActivity().getApplicationContext(),"开始录音！",Toast.LENGTH_SHORT).show();
        chronometer.setBase(SystemClock.elapsedRealtime() - recordingTime);
        chronometer.start();
        //将给当前音频文件的命名传入录音service中方便存入数据库
       /* intent.putExtra("truename",truename);
        getActivity().startService(intent);*/
        isBound=getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d("ummmm",String.valueOf(isBound));
    }



}
