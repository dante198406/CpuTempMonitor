package com.erobbing.cputempmonitor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zhangzhaolei on 2018/1/30.
 */

public class CpuTempMonitorService extends Service {
    private static final String TAG = "CpuTempMonitorService";
    private static final int TEMPERATURE = 60;

    public CpuTempMonitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //registerReceiverAsUser(mTimetickReceiver, UserHandle.ALL, new IntentFilter(Intent.ACTION_TIME_TICK), null, null);
        registerReceiver(mTimetickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "CpuTempMonitorService.onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "CpuTempMonitorService.onDestroy");
        super.onDestroy();
        unregisterReceiver(mTimetickReceiver);
    }

    private BroadcastReceiver mTimetickReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "onReceive: " + intent);
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                if (Build.PRODUCT.contains("LB1728")) {
                    int currentTemp = getMaxTemp();
                    if (currentTemp > TEMPERATURE) {
                        FanCtrlNative.enableFan();
                    } else {
                        FanCtrlNative.disableFan();
                    }
                }
            }
        }
    };

    private static int getMaxTemp() {
        String temp = "1";
        int max = 1;
        try {
            for (int i = 0; i < 11; i++) {
                BufferedReader reader = new BufferedReader(new FileReader("/sys/class/thermal/thermal_zone" + (i + 4) + "/temp"));
                temp = reader.readLine();
                max = Math.max(max, Integer.parseInt(temp));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return max;
    }
}
