package com.example.sevice.nokillsevice;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class MyJobHandleService extends JobService {
    private int jobId=0x0008;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        scheduleJob(getJonInfo());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        boolean isLocalServiceWorking=isServiceWork(this,"com.example.sevice.nokillsevice.LocalService");
        boolean isRomoteServiceWorking=isServiceWork(this,"com.example.sevice.nokillsevice.RomoteService");
        if(!isLocalServiceWorking||!isRomoteServiceWorking){
            this.startService(new Intent(this,LocalService.class));
            this.startService(new Intent(this,RomoteService.class));
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
    private void scheduleJob(JobInfo job){
        JobScheduler js = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        js.schedule(job);
    }
    private JobInfo getJonInfo(){
        JobInfo.Builder builder=new JobInfo.Builder(jobId,new ComponentName(this,MyJobHandleService.class));
        builder.setPersisted(true);
        builder.setPeriodic(100);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        return builder.build();
    }

    public boolean isServiceWork(Context context,String serviceName){
        boolean isWorked=false;
        ActivityManager manager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list=manager.getRunningServices(128);
        if (list.size()<0){
            return false;
        }
        for (int i=0;i<list.size();i++){
            String name=list.get(i).service.getClassName().toString();
            if (serviceName.equals(name)){
                isWorked=true;
                break;
            }
        }
        return isWorked;
    }
}
