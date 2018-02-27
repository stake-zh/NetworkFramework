package com.stake.networkframework;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.stake.networkframework.net.RetroAdapter;

import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RetroAdapter.getService().getInfo("")
                .subscribeOn(Schedulers.io())
                .subscribe(it->{
                    Log.d("msg-log", it);
                },Throwable::printStackTrace);
    }
}
