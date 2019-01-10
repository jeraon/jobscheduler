package com.jiayongqiang.jobschedulerdemo;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MyJobService extends JobService {
    private static final String TAG = MyJobService.class.getSimpleName();
    private MyJobHandler mHandler = new MyJobHandler();
    private MyRunnable mRunnable;
    public MyJobService() {
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(TAG,"StartJob---->Job#"+params.getJobId());
        long duration = params.getExtras().getLong("duration");
        mRunnable = new MyRunnable(params);
        mHandler.postDelayed(mRunnable,duration * 1000);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"StopJob---->job#"+params.getJobId());
        mHandler.removeCallbacks(mRunnable);
        return true;
    }

    static class MyJobHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

     class MyRunnable implements Runnable{

        private JobParameters mParams;
        MyRunnable(JobParameters params){
            this.mParams = params;
        }
        @Override
        public void run() {
            jobFinished(mParams,false);
            Log.d(TAG,"Finished Job#"+mParams.getJobId());
        }
    }

}
