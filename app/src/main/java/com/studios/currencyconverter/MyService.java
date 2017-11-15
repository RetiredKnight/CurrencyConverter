package com.studios.currencyconverter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

//Сервис, который будет синхронизировать данные с сервера
// 2 раза в день даже при закрытой программе или выключенном экране

public class MyService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("Myservice", "oncreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("myservice", "onstart");

        ConnectionReceiver();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void ConnectionReceiver() {
        Log.d("serviceLogs", "I'mHere!");

        //Вызов AsyncTask для получения данных с сервера
        CollectData data = new CollectData(this);
        data.execute();
    }
}
