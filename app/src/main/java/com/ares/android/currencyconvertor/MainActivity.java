package com.ares.android.currencyconvertor;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ares.android.currencyconvertor.models.Currency;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private String baseUrl = "https://free.currencyconverterapi.com/api/v6/convert";
    // https://free.currencyconverterapi.com/api/v6/convert?q=USD_PHP&compact=ultra

    private Spinner fromSpinner;
    private Spinner toSpinner;
    private Button convertButton;
    private EditText resultEditText;
    private EditText numberEditText;

    private JsonCurrencyParser json;

    private String fromId;
    private String toId;
    private String finalQuery;
    private double number;

    private OkHttpClient client;

    ArrayAdapter<Currency> aa1;
    ArrayAdapter<Currency> aa2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Currency Converter");
        setSupportActionBar(toolbar);

        fromSpinner = findViewById(R.id.from_spinner);
        toSpinner = findViewById(R.id.to_spinner);
        convertButton = findViewById(R.id.convert_button);
        resultEditText = findViewById(R.id.result_edittext);
        numberEditText = findViewById(R.id.number_editText);
        json = JsonCurrencyParser.getInstance();

        client = new OkHttpClient();

        if(!json.isAvailable()) {
            JsonCurrencyParser.getInstance().init(this);
            Log.i(TAG, "init method called");
        }

        if(json.isAvailable()) {
            Log.i(TAG, "data is available");

            aa1 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, json.getCurrencyArrayList());
            aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fromSpinner.setAdapter(aa1);
            fromSpinner.setOnItemSelectedListener(this);

            aa2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, json.getCurrencyArrayList());
            aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            toSpinner.setAdapter(aa2);
            toSpinner.setOnItemSelectedListener(this);

        }

        convertButton.setOnClickListener(this);

    }

    public void getResultFromApi() {

        Observable<Double> observable = new Observable<Double>() {
            @Override
            protected void subscribeActual(Observer<? super Double> observer) {

                String finalUrl = baseUrl + "?q=" + finalQuery + "&compact=y";

                Request request = new Request.Builder()
                        .url(finalUrl)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    JSONObject json = new JSONObject(response.body().string());
                    double result = json.getJSONObject(finalQuery).getDouble("val");
                    observer.onNext(result);
                    observer.onComplete();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Toast.makeText(MainActivity.this, "Network Error Occurred", Toast.LENGTH_SHORT).show();
                } catch (JSONException jex) {
                    jex.printStackTrace();
                    Toast.makeText(MainActivity.this, "JSON Parse Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        };

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // nothing
                    }

                    @Override
                    public void onNext(Double aDouble) {
                        resultEditText.setText(Double.toString(aDouble * number));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.convert_button) {
            Log.i(TAG, "Convert button pressed");
            if(fromId!=null && toId!=null && !TextUtils.isEmpty(numberEditText.getText())) {
                Log.i(TAG, "Both items selected");
                number = Double.parseDouble(numberEditText.getText().toString());
                finalQuery = fromId + "_" + toId;
                getResultFromApi();
            } else {
                Log.i(TAG, "Both items not selected");
                Toast.makeText(this, "Select both from and to options", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if(adapterView.getAdapter() == aa1) {
            Currency c = (Currency) adapterView.getItemAtPosition(i);
            fromId = c.getId();
            Log.i(TAG, "from adapter used. Selected item = " + fromId);
        }
        else if(adapterView.getAdapter() == aa2) {
            Currency c = (Currency) adapterView.getItemAtPosition(i);
            toId = c.getId();
            Log.i(TAG, "to adapter used. Selected item = " + toId);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
