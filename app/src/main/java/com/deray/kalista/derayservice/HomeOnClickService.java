package com.deray.kalista.derayservice;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Deray on 2017/9/19.
 */

public class HomeOnClickService extends Service {
    //两个常量是写死的
    public static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    public static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    public static final int EXIT = 1500;

    private HomeOnClickReceiver homeOnClickReceiver;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAdminName = intent.getParcelableExtra("1");
        homeOnClickReceiver = new HomeOnClickReceiver();
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeOnClickReceiver, homeFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("deray.kalista.reset_lock");
        sendBroadcast(intent);
    }

    public class HomeOnClickReceiver extends BroadcastReceiver {
        long lastTime;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (intent.getAction().equals("deray.kalista.reset_lock")) {
                Intent in = new Intent(context, MainActivity.class);
                startActivity(in);
            }
            if (System.currentTimeMillis() - lastTime <= EXIT) {
                if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                        lockScreen(mAdminName);
                    }
                }
            } else {
                lastTime = System.currentTimeMillis();
            }
        }
    }

    public void lockScreen(ComponentName componentName) {
        if (mDPM.isAdminActive(componentName)) {
            mDPM.lockNow();
        }
    }
}
