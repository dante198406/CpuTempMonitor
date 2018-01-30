package com.erobbing.cputempmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zhangzhaolei on 2018/1/30.
 */

public class CpuTempMonitorReceiver extends BroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent service = new Intent(context, CpuTempMonitorService.class);
            context.startService(service);
        }
    }

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
