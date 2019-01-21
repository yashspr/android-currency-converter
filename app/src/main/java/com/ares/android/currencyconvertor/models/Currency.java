package com.ares.android.currencyconvertor.models;

public class Currency {

    private String currencyName;
    private String currencySymbol;
    private String id;

    public Currency() {

    }

    public Currency(String cn, String cs, String id) {
        currencyName = cn;
        currencySymbol = cs;
        this.id = id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getCurrencyName() + " (" + getId() + ")";
    }
}
