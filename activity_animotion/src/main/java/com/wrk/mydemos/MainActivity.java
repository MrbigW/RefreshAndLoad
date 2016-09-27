package com.wrk.mydemos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void optClick(View v){
        Intent intent = new Intent(MainActivity.this,SecondActivity.class);
        startActivity(intent);
//        overridePendingTransition(R.anim.class);
    }
}
