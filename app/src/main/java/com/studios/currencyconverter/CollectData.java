package com.studios.currencyconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CollectData extends AsyncTask<String, String, String> {

    HttpURLConnection urlConnection;
    private Context context;

    public CollectData(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... args) {

        StringBuilder result = new StringBuilder();

        try {
            //По заданному URL получаем входяший поток данных от сервера
            URL url = new URL("https://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;

            //Весь входящий поток данных преобразуется в строку line
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Отключение соединения с сервером после получения данных
            urlConnection.disconnect();
        }

        Log.d("response", result.toString());
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {

        try {
            //Создаем объект для создания и управления версиями БД
            DBHelper dbHelper = new DBHelper(context);

            //Подключение к БД
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //Преобразование строкового результата в JSON
            JSONObject response = new JSONObject(result);

            JSONArray jsonArray = response.getJSONObject("list").getJSONArray("resources");

            Cursor c = db.query("myTable", null, null, null, null, null, null);
            boolean isFirst;

            //Сдвигаем курсор в самое начало таблицы для проверки является ли она пустой или заполненной
            //иными словами это проверка на то, в первый раз заполняется таблица БД или нет
            if (c.moveToFirst()) {
                isFirst = false;
            } else {
                isFirst = true;
            }
            c.close();
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d("jsonarray", jsonArray.getJSONObject(i).getJSONObject("resource").getJSONObject("fields").getString("name"));

                //Подготавливаем данные к записи в БД
                ContentValues cv = new ContentValues();
                cv.put("name", jsonArray.getJSONObject(i).getJSONObject("resource").getJSONObject("fields").getString("name"));
                cv.put("currency", jsonArray.getJSONObject(i).getJSONObject("resource").getJSONObject("fields").getString("price"));

                long rowID;
                String id = Integer.toString(i+1);

                //При первом заполнении таблицы данные добавляются с помощью команды insert
                if (isFirst) {
                    rowID = db.insert("myTable", null, cv);
                    Log.d("insertedRowID", rowID+"");
                } else {
                    //При вторичном заполнении таблицы данные просто обновляются командой update
                    rowID = db.update("myTable", cv, "id = ?", new String[] {id} );
                    Log.d("UpdatedRowID", rowID+"");
                }
            }
            dbHelper.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}