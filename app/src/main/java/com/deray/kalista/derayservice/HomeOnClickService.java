package com.deray.kalista.derayservice;

import android.app.ActivityManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;

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
        if (mAdminName == null) {
            SharedPreferences compomemt = getSharedPreferences("compomemt", MODE_PRIVATE);
            String name = compomemt.getString("name", "");
            mAdminName = JSON.parseObject(name, ComponentName.class);
        }
        homeOnClickReceiver = new HomeOnClickReceiver();
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeOnClickReceiver, homeFilter);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class HomeOnClickReceiver extends BroadcastReceiver {
        long lastTime;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isServiceRun = false;

            ActivityManager systemService = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            // 查看文档发现 在 o 弃用并且不会返回第三方开启的服务
            for (ActivityManager.RunningServiceInfo serviceInfo : systemService.getRunningServices(Integer.MAX_VALUE)) {
                if ("com.deray.kalista.derayservice.ShugoService".equals(serviceInfo.service.getClassName())) {
                    System.out.println(serviceInfo.service.getClassName());
                    isServiceRun = true;
                }
            }
            Intent startService = new Intent(context, ShugoService.class);
            startService(startService);

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
