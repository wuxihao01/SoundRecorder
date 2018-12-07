package com.example.um.soundrecorder;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;
import java.util.TimerTask;

import static java.security.AccessController.getContext;

public class Recorder extends Service {
    private final IBinder mBinder = new LocalBinder();
    private MediaRecorder mediaRecorder;
    private String fileName, filePath,truename,command;
    private MediaSQL sql;
    private boolean ispause=true;
    private File f;
    private SQLiteDatabase db;
    private long StartingTime = 0, truetime = 0;

    public Recorder() {
    }

    public class LocalBinder extends Binder {
        Recorder getService() {
            // Return this instance of LocalService so clients can call public methods
            return Recorder.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
       /* startRecording();
        truename=intent.getStringExtra("truename");
        */
        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            stopRecording();
        }
    }

    public void startRecording(String name) {
        truename=name;
        init();
        try {
            if (mediaRecorder == null) {
                Log.d("ummmm", "recorder is null");
            }
            mediaRecorder.prepare();
            mediaRecorder.start();
            Log.d("ummmm","录音开始");
            StartingTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        try {
            if (mediaRecorder == null) {
                Log.d("ummmm", "recorder is null");
            }
            Log.d("ummmm","录音结束1");
            mediaRecorder.stop();
            Log.d("ummmm","录音结束2");
            truetime = System.currentTimeMillis() - StartingTime;
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            savedate();
        } catch (Exception ab) {
            ab.printStackTrace();
        }

    }

    private void savedate() {
        sql = new MediaSQL(this);
        db = sql.getWritableDatabase();
        ContentValues values = new ContentValues();
        String time = String.valueOf(truetime);
        values.put("time", time);
        values.put("path", filePath);
        values.put("medianame", truename);
        Log.d("ummmm", "time:" + time);
        Log.d("ummmm", "path:" + filePath);
        Log.d("ummmm", "path:" + truename);
        db.insert("MEDIA", null, values);
    }

    public void init() {
        try {
            //初始化mediarecorder
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            Log.d("ummmm", "init is ok");
            fileName = (System.currentTimeMillis()) + ".m4a";
            do {
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecorder/" + fileName;
                f = new File(filePath);
            } while (f.exists() && !f.isDirectory());
            mediaRecorder.setOutputFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauserecorder(){
        if(mediaRecorder!=null){
            if(ispause){
                mediaRecorder.pause();
                Toast.makeText(getApplicationContext(),"录音已暂停",Toast.LENGTH_SHORT).show();
            }
            else{
                mediaRecorder.resume();
                Toast.makeText(getApplicationContext(),"录音已继续",Toast.LENGTH_SHORT).show();
            }
            ispause=!ispause;
        }
    }

}
