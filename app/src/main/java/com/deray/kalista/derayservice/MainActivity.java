package com.deray.kalista.derayservice;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 开启
        ComponentName mAdminName = new ComponentName(this, AdminManageReceiver.class);
        SharedPreferences compomemt = getSharedPreferences("compomemt", MODE_PRIVATE);
        SharedPreferences.Editor edit = compomemt.edit();
        String s = JSON.toJSONString(mAdminName);
        edit.putString("name",s).commit();
        showAdminManagement(mAdminName);
        Intent intent = new Intent(this, HomeOnClickService.class);
        intent.putExtra("1", mAdminName);
        startService(intent);
        finish();
    }

    //激活设备管理器
    private void showAdminManagement(ComponentName mAdminName) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "activity device");
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
