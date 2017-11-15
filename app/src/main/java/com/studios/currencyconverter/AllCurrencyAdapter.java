package com.studios.currencyconverter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//Адаптер для ListView в SettingsActivity, который отображает все доступные валюты

public class AllCurrencyAdapter<T> extends BaseAdapter {

    private final LayoutInflater mInflater;

    private Context context;
    private ArrayList<CurrencyClass> list;

    public AllCurrencyAdapter(Context context, ArrayList<CurrencyClass> list) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.currency_item_list, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = view.findViewById(R.id.textview);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(list.get(i).getName());
        final int pos = i;

        if (list.get(i).isChecked()) {
            viewHolder.textView.setTextColor(Color.BLUE);
        } else {
            viewHolder.textView.setTextColor(Color.BLACK);
        }

        //При выборе валюты текст становится синим, при отмены выбора обратно черным
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.get(pos).isChecked()) {
                    list.get(pos).setChecked(false);
                    viewHolder.textView.setTextColor(Color.BLACK);
                } else {
                    list.get(pos).setChecked(true);
                    viewHolder.textView.setTextColor(Color.BLUE);
                }
            }
        });

        return view;
    }

    private class ViewHolder {
        TextView textView;
    }

}