package com.studios.currencyconverter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private Spinner mainSpinner;
    private List<String> spinnerList;
    private ArrayAdapter<String> spinnerAdapter;

    private ListView listView;
    private ArrayList<CurrencyClass> list;
    private AllCurrencyAdapter listAdapter;

    private Button saveButton;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private String mainCurrency;
    private float mainval;

    private ArrayList<String> choosenCurrency;
    private ArrayList<Float> costOfCurrency;

    private ArrayList<Float> allCurrencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Эти 2 ArrayList нужны для хранения выбранных валют и их цен по отношению к доллару
        choosenCurrency = new ArrayList<>();
        costOfCurrency = new ArrayList<>();

        //Список всех валют
        allCurrencies = new ArrayList<>();

        //Объект для управления БД
        dbHelper = new DBHelper(this);
        // Подключение к БД
        db = dbHelper.getReadableDatabase();

        //Объявление спиннера, который позволит выбирать основную валюту
        mainSpinner = findViewById(R.id.main_spinner);
        //Список для спиннера
        spinnerList = new ArrayList<>();
        //Адаптер спиннера (выбран стандартный простой выпадающий список)
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Слушатель выбора одного из объектов выпадающего списка
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Spinner spinner = (Spinner) adapterView;
                mainCurrency = spinner.getItemAtPosition(i).toString();
                mainval = allCurrencies.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listView = findViewById(R.id.currency_list_view);
        list = new ArrayList<CurrencyClass>();
        listAdapter = new AllCurrencyAdapter(this, list);
        listView.setAdapter(listAdapter);

        saveButton = findViewById(R.id.save_button);

        //Слушатель нажатия на кнопку сохранить
        //Возвращает в предыдущий Activity и передает ему все необходимые данные
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CurrencyClass temp : list) {
                    if (temp.isChecked()) {
                        choosenCurrency.add(temp.getName());
                        costOfCurrency.add(temp.getCurrency());
                    }
                }
                if (mainCurrency.equals("")) {
                    mainCurrency = mainSpinner.getSelectedItem().toString();
                    mainval = mainSpinner.getSelectedItemId();
                }
                Intent intent = new Intent();
                intent.putExtra("choosencurrency", choosenCurrency);
                intent.putExtra("costofcurrency", costOfCurrency);
                intent.putExtra("maincurrency", mainCurrency);
                intent.putExtra("mainval", mainval);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        LoadData();

    }

    private void LoadData() {
        //Загрузка всех данных из БД
        Cursor c = db.query("myTable", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int nameIndex = c.getColumnIndex("name");
            int currencyIndex = c.getColumnIndex("currency");

            do {
                String tempcur = c.getString(nameIndex);
                String[] temp = tempcur.split("/");
                spinnerList.add(temp[temp.length-1]);
                allCurrencies.add(c.getFloat(currencyIndex));
                list.add(new CurrencyClass(temp[temp.length-1], c.getFloat(currencyIndex)));
            } while (c.moveToNext());
            mainSpinner.setAdapter(spinnerAdapter);

        } else {
            Log.d("ReadDB", "DB is empty");
        }
        c.close();
    }
}
