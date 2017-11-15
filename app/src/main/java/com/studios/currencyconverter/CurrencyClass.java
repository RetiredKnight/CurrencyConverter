package com.studios.currencyconverter;

/**
 * Created by Personal on 15.11.2017.
 */

public class CurrencyClass {

    private String name;
    private float currency;
    private boolean isChecked;

    public CurrencyClass(String name, float currency) {
        this.name = name;
        this.currency = currency;
        this.isChecked = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCurrency() {
        return currency;
    }

    public void setCurrency(float currency) {
        this.currency = currency;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
