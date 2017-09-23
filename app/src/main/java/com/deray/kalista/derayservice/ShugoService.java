package com.deray.kalista.derayservice;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Deray on 2017/9/23.
 */

public class ShugoService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 每分钟检查一次 service 的运行状态
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        ShugoBroadcastReceiver shugoBroadcastReceiver = new ShugoBroadcastReceiver();
        registerReceiver(shugoBroadcastReceiver, filter);
        return START_REDELIVER_INTENT;
    }

    public class ShugoBroadcastReceiver extends BroadcastReceiver {
        private boolean isServiceRun;

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityManager systemService = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            // 查看文档发现 在 o 弃用并且不会返回第三方开启的服务
            for (ActivityManager.RunningServiceInfo serviceInfo : systemService.getRunningServices(Integer.MAX_VALUE)) {
                if ("com.deray.kalista.derayservice.HomeOnClickService".equals(serviceInfo.service.getClassName())) {
                    isServiceRun = true;
                }
            }
            Intent startService = new Intent(context, HomeOnClickService.class);
            startService(startService);
        }
    }
}
