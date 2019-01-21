package com.ares.android.currencyconvertor;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.ares.android.currencyconvertor.models.Currency;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonCurrencyParser {

    private static JsonCurrencyParser mJsonParser = null;
    ArrayList<Currency> currencies = null;
    private boolean isJSONAvailable = false;

    private JsonCurrencyParser() {

    }

    public static JsonCurrencyParser getInstance() {
        if(mJsonParser == null) {
            mJsonParser = new JsonCurrencyParser();
        }
        return mJsonParser;
    }

    public void init(Context application) {
        Log.i("MainActivity", "init method called");

        currencies = new ArrayList<>();

        String json = null;
        try {
            InputStream is = application.getAssets().open("currencies.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        if(json != null) {
            try {
                JSONObject obj = new JSONObject(json).getJSONObject("results");

                Iterator<String> iterator = obj.keys();
                while(iterator.hasNext()) {
                    String key = iterator.next();
                    Log.i("MainActivity", "Key = " + key);
                    JSONObject temp = obj.getJSONObject(key);
                    String currencyName = null, currencySymbol = null, id = null;
                    try {
                        currencyName = temp.getString("currencyName");
                        id = temp.getString("id");
                        currencySymbol = temp.getString("currencySymbol");
                    } catch(JSONException ex) {

                    } finally {
                        Currency c = new Currency(
                                currencyName,
                                currencySymbol,
                                id
                        );
                        currencies.add(c);
                    }
                }

                isJSONAvailable = true;

            } catch(JSONException jex) {
                jex.printStackTrace();
            }
        }
    }

    public ArrayList<Currency> getCurrencyArrayList() {
        return this.currencies;
    }

    public boolean isAvailable() {
        return isJSONAvailable;
    }

}
