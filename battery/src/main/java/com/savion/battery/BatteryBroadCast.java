package com.savion.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

/**
 * @Author: savion
 * @Date: 2022/2/23 13:28
 * @Des: 电池电量与状态变化广播监听
 **/
public class BatteryBroadCast extends BroadcastReceiver {

    private BatteryChangeCallBack changeCallBack;


    public interface BatteryChangeCallBack {
        void onBatteryChange(int level, ChargeMode isCharge);
    }

    public void setChangeCallBack(BatteryChangeCallBack changeCallBack) {
        this.changeCallBack = changeCallBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Intent.ACTION_BATTERY_CHANGED) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int chargeState = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            Log.e("savion", String.format("batter : %s , isCharge : %s", level, chargeState));
            if (changeCallBack != null) {
                if (chargeState == BatteryManager.BATTERY_PLUGGED_AC) {
                    //充电器充电
                    changeCallBack.onBatteryChange(level, ChargeMode.AC);
                } else if (chargeState == BatteryManager.BATTERY_PLUGGED_USB) {
                    //USB充电
                    changeCallBack.onBatteryChange(level, ChargeMode.USB);
                } else if (chargeState == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                    //无线充电
                    changeCallBack.onBatteryChange(level, ChargeMode.WIRELESS);
                } else if (chargeState != 0) {
                    //未识别的充电方式
                    changeCallBack.onBatteryChange(level, ChargeMode.UNKNOW);
                } else {
                    //未充电
                    changeCallBack.onBatteryChange(level, ChargeMode.NONE);
                }
            }
        }
    }

    public void unregiste(Context context) {
        if (context != null) {
            try {
                context.unregisterReceiver(this);
            } catch (Exception e) {
                Log.e("savion", "unregiste battery broadcastreceiver failed:" + e.getMessage());
            }
        }
    }

    public void registe(Context context) {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            context.registerReceiver(this, intentFilter);
        } catch (Exception e) {
            Log.e("savion", "registe battery broadcastreceiver failed:" + e.getMessage());
        }
    }
}
