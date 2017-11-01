package com.example.sevice.nokillsevice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class RomoteService extends Service {
    private static final String TAG="RomoteService";
    MyConn conn;
    MyBinder binder;
    private Timer timer;
    private TimerTask timerTask;
    private int counter=0;
    public RomoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        conn = new MyConn();
        binder = new MyBinder();
        this.bindService(new Intent(this, LocalService.class), conn, Context.BIND_IMPORTANT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, " 远程服务started", Toast.LENGTH_SHORT).show();
        Notification notification;
        Notification.Builder builder=new Notification.Builder(this);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setContentTitle("ceshi");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentInfo("info");
        builder.setWhen(System.currentTimeMillis());
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);
        builder.setContentIntent(pi);
        notification=builder.build();
        Toast.makeText(RomoteService.this, " 本地服务started", Toast.LENGTH_SHORT).show();
        startForeground(startId,notification);
        startTimer();
        return START_STICKY;
    }

    class MyBinder extends IMyAidlInterface.Stub {
        @Override
        public String getServiceName() throws RemoteException {
            return RomoteService.class.getSimpleName();
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }

    class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(RomoteService.this, "本地服务killed", Toast.LENGTH_SHORT).show();

            //开启本地服务
            RomoteService.this.startService(new Intent(RomoteService.this, LocalService.class));
            //绑定本地服务
            RomoteService.this.bindService(new Intent(RomoteService.this, LocalService.class), conn, Context.BIND_IMPORTANT);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //开启本地服务
        stopTimer();
        RomoteService.this.startService(new Intent(RomoteService.this, LocalService.class));
        //绑定本地服务
        RomoteService.this.bindService(new Intent(RomoteService.this, LocalService.class), conn, Context.BIND_IMPORTANT);

    }

    public void startTimer(){
        timer=new Timer();
        initializaTimerTask();
        timer.schedule(timerTask,1000,1000);
    }
    public  void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    public void initializaTimerTask() {
        timerTask=new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG,"int timer +++++"+(counter++));
            }
        };
    }
}
