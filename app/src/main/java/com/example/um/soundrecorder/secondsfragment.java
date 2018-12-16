package com.example.um.soundrecorder;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class secondsfragment extends Fragment {

    private MediaSQL mediaSQL;
    private Effectstype effect;
    private SQLiteDatabase db;
    private static final int THUMB_SIZE = 150;
    public static final String appID = "wxdc8b5869e66ad83f";
    public IWXAPI wxapi;
    myAdapter adapter;
    ListView mylistview;
    private boolean apply;
    private List<recorderItem> musicList=new ArrayList<>();
    public secondsfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_secondsfragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        initQuery();
    }

    private void init() {
        mediaSQL=new MediaSQL(getContext());
        db=mediaSQL.getReadableDatabase();
        apply=regToWeiXin();
    }

    public void initQuery() {
        Cursor cursor=db.query("MEDIA",null,null,null,null,null,null);
        musicList.clear();
        if(cursor.moveToFirst()) {
            do {

                String time = ms2HMS(Integer.valueOf(cursor.getString(cursor.getColumnIndex("time"))));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                String name=cursor.getString(cursor.getColumnIndex("medianame"));
                recorderItem item=new recorderItem(name,time,path);
                musicList.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        mylistview=(ListView)getView().findViewById(R.id.mylist);
        adapter =new myAdapter(getContext(),R.layout.musiclistview,musicList);
        mylistview.setAdapter(adapter);
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recorderItem item=(recorderItem) parent.getItemAtPosition(position);
                Intent intent=new Intent(getContext(),MusicPlayerr.class);
                intent.putExtra("path",item.getPath());
                intent.putExtra("name",item.getName());
                intent.putExtra("time",item.getTime());
                startActivity(intent);
            }
        });

        mylistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                recorderItem item=(recorderItem) parent.getItemAtPosition(position);
                final String itempath=item.getPath();
                effect=Effectstype.RotateBottom;
                final NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(getContext());
                dialogBuilder
                        .withTitle("Modal Dialog")                                  //.withTitle(null)  no title
                        .withTitleColor("#FFFFFF")                                  //def
                        .withDividerColor("#11000000")                              //def
                        .withMessage("This is a modal Dialog.")                     //.withMessage(null)  no Msg
                        .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                        .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)                               //def
                        .withIcon(getResources().getDrawable(R.drawable.icon))
                        .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                        .withDuration(700)                                          //def
                        .withEffect(effect)                                         //def Effectstype.Slidetop
                        .withButton1Text("Delete")                                      //def gone
                        .withButton2Text("Cancel")                                  //def gone
                        .setCustomView(R.layout.custom_view,getContext())         //.setCustomView(View or ResId,context)
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                delMedia(itempath);
                                dialogBuilder.dismiss();
                                initQuery();
                                Toast.makeText(v.getContext(), "删除成功！", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(v.getContext(), "取消操作", Toast.LENGTH_SHORT).show();
                                dialogBuilder.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });



       /* final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("选择选项");
        builder.setMessage("选择你要的服务");
        mylistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                recorderItem item=(recorderItem) parent.getItemAtPosition(position);
                final String itempath=item.getPath();
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delMedia(itempath);
                        initQuery();
                    }
                });
                builder.setNeutralButton("分享到朋友圈", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharerecord(itempath);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),"取消操作",Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dia=builder.create();
                dia.show();
                return true;
            }
        });*/
    }


    private void delMedia(String path){
        db.delete("MEDIA","path=?",new String[]{path});
        initQuery();
        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(getContext(), "删除文件失败,"+"文件不存在！", Toast.LENGTH_SHORT).show();
        } else {
            file.delete();
            File check=new File(path);
            if(!check.exists()){
                Toast.makeText(getContext(),"删除文件成功",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String ms2HMS(int _ms){
        String HMStime=String.valueOf(_ms/1000);
        return HMStime;

        /*String HMStime;
        _ms/=1000;
        int hour=_ms/3600;
        int mint=(_ms%3600)/60;
        int sed=_ms%60;
        String hourStr=String.valueOf(hour);
        if(hour<10){
            hourStr="0"+hourStr;
        }
        String mintStr=String.valueOf(mint);
        if(mint<10){
            mintStr="0"+mintStr;
        }
        String sedStr=String.valueOf(sed);
        if(sed<10){
            sedStr="0"+sedStr;
        }
        HMStime=hourStr+":"+mintStr+":"+sedStr;
        return HMStime;*/

    }


    private void sharerecord(final String musicUrl){
        if(apply){

            WXMusicObject music = new WXMusicObject();
            //music.musicUrl = "http://www.baidu.com";
            music.musicUrl="http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";
            //music.musicUrl="http://120.196.211.49/XlFNM14sois/AKVPrOJ9CBnIN556OrWEuGhZvlDF02p5zIXwrZqLUTti4o6MOJ4g7C6FPXmtlh6vPtgbKQ==/31353278.mp3";

            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = music;
            msg.title = "Music Titleng";
            msg.description = "Music AlbumLong";

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.haibao);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            bmp.recycle();
            msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("music");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            boolean issucceed=wxapi.sendReq(req);
            Log.d("ummmm","跳转到微信app+"+issucceed);


          /*  WXMusicObject music = new WXMusicObject();
            music.musicUrl=musicUrl;
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = music;
            msg.title = "recorder";
            msg.description ="这是一个录音文件";

            Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.haibao);
            msg.thumbData = ConvertUtils.bitmap2Bytes(thumb,Bitmap.CompressFormat.JPEG);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("music");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            wxapi.sendReq(req);*/
        }
    }
    public boolean regToWeiXin() {
        if (wxapi == null){
            wxapi = WXAPIFactory.createWXAPI(getActivity().getApplicationContext(), appID);
        if (wxapi.isWXAppInstalled()) {
            wxapi.registerApp(appID);
            Log.d("ummmm","register succeed!");
            return true;
        } else {
            Toast.makeText(getContext(), "手机没有安装微信程序，请前往APP应用商店下载微信", Toast.LENGTH_SHORT).show();
            return false;
        }} else {
            return false;
        }
    }

    public String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
