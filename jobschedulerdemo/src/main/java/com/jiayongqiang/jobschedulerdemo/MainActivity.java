package com.jiayongqiang.jobschedulerdemo;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button mButton_schedule,mButton_cancelAll,mButton_finish;
    private RadioButton mRadioButton_network_any,mRadioButton_network_wifi;
    private CheckBox mCheckBox_charging,mCheckBox_deviceIdle;
    private EditText mEditText_duration,mEditText_delay,mEditText_deadline;
    private JobScheduler mJobScheduler;
    private JobInfo.Builder mBuilder;
    private int mJobId;
    private boolean mRequiresCharging;
    private boolean mRequiresDeviceIdle;
    private int mRequiredNetworkType;
    private long mDelayTime;
    private long mDeadLine;
    private long mDurationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mJobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    private void initView() {

        mButton_cancelAll = findViewById(R.id.button_cancelall);
        mButton_schedule = findViewById(R.id.button_schedule);
        mButton_finish = findViewById(R.id.button_finish);
        mButton_cancelAll.setOnClickListener(this);
        mButton_schedule.setOnClickListener(this);
        mButton_finish.setOnClickListener(this);

        mRadioButton_network_any = findViewById(R.id.radio_1);
        mRadioButton_network_wifi = findViewById(R.id.radio_2);

        mCheckBox_charging = findViewById(R.id.checkbox_charging);
        mCheckBox_deviceIdle = findViewById(R.id.checkbox_di);
        mCheckBox_charging.setOnCheckedChangeListener(this);
        mCheckBox_deviceIdle.setOnCheckedChangeListener(this);

        mEditText_duration = findViewById(R.id.editText_duration);
        mEditText_delay = findViewById(R.id.editText_delay);
        mEditText_deadline = findViewById(R.id.editText_deadline);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.button_schedule:
                String duration = mEditText_duration.getText().toString();
                String delay = mEditText_delay.getText().toString();
                String deadline = mEditText_deadline.getText().toString();
                mRequiredNetworkType = mRadioButton_network_wifi.isChecked() ? JobInfo.NETWORK_TYPE_UNMETERED
                        :JobInfo.NETWORK_TYPE_ANY;
                PersistableBundle bundle = new PersistableBundle();
                //default 2s to finish job
                bundle.putLong("duration",TextUtils.isEmpty(duration) ? 2L : Long.valueOf(duration));

                mBuilder = new JobInfo.Builder(mJobId++, new ComponentName(this,MyJobService.class));
                mBuilder.setRequiresCharging(mRequiresCharging)
                        .setRequiresDeviceIdle(mRequiresDeviceIdle)
                        .setRequiredNetworkType(mRequiredNetworkType)
                        .setExtras(bundle);
                if (!TextUtils.isEmpty(deadline)) {
                    mBuilder.setOverrideDeadline(Long.valueOf(deadline) * 1000);
                }
                if (!TextUtils.isEmpty(delay)) {
                    mBuilder.setMinimumLatency(Long.valueOf(delay) * 1000);
                }

                mJobScheduler.schedule(mBuilder.build());
                break;
            case R.id.button_cancelall:
                mJobScheduler.cancelAll();
                break;
            case R.id.button_finish:
                List<JobInfo> allPendingJobs = mJobScheduler.getAllPendingJobs();
                if (allPendingJobs.size() != 0){
                    int jobId = allPendingJobs.get(0).getId();
                    mJobScheduler.cancel(jobId);
                    Toast.makeText(this,"the job#"+jobId+" is canceled.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"No Job active.",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkbox_charging:
                mRequiresCharging = isChecked;
                break;
            case R.id.checkbox_di:
                mRequiresDeviceIdle = isChecked;
                break;
        }
    }
}
