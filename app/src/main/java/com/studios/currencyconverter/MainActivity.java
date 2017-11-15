package com.studios.currencyconverter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int SETTINGS_REQUEST_CODE = 9011;

    private Button settingsButton;
    private ListView listView;
    private TextView chooseCurrencyTextView;

    private ArrayList<String> list;
    private ArrayAdapter adapter;

    private SharedPreferences prefs;

    private ArrayList<String> choosenCurrency;
    private ArrayList<Float> costOfCurrency;
    private String mainCurrency;
    private AlarmManager am;
    private float mainval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsButton = findViewById(R.id.settings_button);
        listView = findViewById(R.id.list_view);
        chooseCurrencyTextView = findViewById(R.id.choose_currency_textview);

        list = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        //В Shared preferences хранится информация о последнем выборе пользователя:
        //основная валюта и список
        prefs = getApplicationContext().getSharedPreferences("currency", 0);

        //Проверка на первый запуск приложения
        if (prefs.getString("main", "").equals("")) {
            chooseCurrencyTextView.setVisibility(View.VISIBLE);
            //Вызов функции, которая запускает AlarmManager для синхронизации с сервером
            //2 раза в день
            setAlarm();
            Log.d("prefs", "first launch");
        } else {
            //Данные в Shared Preferences хранятся в формате Json
            //Вытаскиваем данные, сохраненные с последней настройки валют
            Gson gson = new Gson();
            String json = prefs.getString("choosencurrency", null);
            String json2 = prefs.getString("costofcurrency", null);
            String main = prefs.getString("main", null);
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            choosenCurrency = gson.fromJson(json, type);
            costOfCurrency = gson.fromJson(json2, type);

            //Функция для отображения списка валют в ListView
            ShowCurrency(main, choosenCurrency, costOfCurrency);
            //------

            Log.d("prefs", main);
            Log.d("prefs", choosenCurrency.toString());
            Log.d("prefs", costOfCurrency.toString());
        }

        //Слушатель нажатия на кнопку настройки валют
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), SETTINGS_REQUEST_CODE);
            }
        });

    }

    private void setAlarm() {
        //Запуск AlarmManager, который будет синхронизироваться с сервером 2 раза в день
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent service = new Intent(MainActivity.this, MyService.class);
        PendingIntent pi = PendingIntent.getService(MainActivity.this, 0, service, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 12*60*60*1000, pi);
        Log.d("setAlarm", "STARTED!");
    }

    private void ShowCurrency(String main, ArrayList<String> choosenCurrency, ArrayList<Float> costOfCurrency) {
        //Перед началом заполнения листа, очищаем его
        list.clear();


        //В цикле формируется строка, которая будет отображаться в ListView
        //Так как адаптер использовался стандартный, то ArrayList заполняется обычной строкой
        for (int i = 0; i < choosenCurrency.size(); i++) {
            String curname = "";
            curname = "1 " + choosenCurrency.get(i) + " = " + costOfCurrency.get(i) + " " + main;
            list.add(curname);
        }
        listView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            //Смотрим данные, которые пришли с SettingsActivity
            case SETTINGS_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {

                    chooseCurrencyTextView.setVisibility(View.INVISIBLE);

                    //Принимаем ArrayList'ы для списка выбранных курсов и их цена по отношению к доллару
                    choosenCurrency = (ArrayList<String>) data.getSerializableExtra("choosencurrency");
                    costOfCurrency = (ArrayList<Float>) data.getSerializableExtra("costofcurrency");

                    //Принимаем основную валюту
                    mainCurrency = data.getStringExtra("maincurrency");
                    //Принимаем цену основной валюты по отношению к доллару
                    mainval = data.getFloatExtra("mainval", 0);

                    Log.d("mainval", mainval+"");

                    for (String str : choosenCurrency) {
                        Log.d("choosen", str);
                    }

                    //В цикле делим цену основной валюты на цену интересующей валюты, чтобы получить их курс
                    for (int i = 0; i < costOfCurrency.size(); i++) {
                        float temp = mainval/costOfCurrency.get(i);
                        costOfCurrency.set(i, temp);
                        Log.d("cost", costOfCurrency.get(i)+"");
                    }
                    Log.d("main", mainCurrency);

                    //Полученные настройки сохраняем в Shared preferences
                    prefs = getApplicationContext().getSharedPreferences("currency", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("main", mainCurrency);
                    Gson gson = new Gson();
                    String json = gson.toJson(choosenCurrency);
                    String json2 = gson.toJson(costOfCurrency);
                    editor.putString("choosencurrency", json);
                    editor.putString("costofcurrency", json2);
                    editor.commit();

                    //Отображаем все в ListView
                    ShowCurrency(mainCurrency, choosenCurrency, costOfCurrency);
                }
                break;
        }
    }

}