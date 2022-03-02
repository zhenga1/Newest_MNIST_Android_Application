package com.example.practice_touch_screen_application;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetectNum extends AppCompatActivity {
    private ImageView imageView;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        imageView = (ImageView) findViewById(R.id.imageview);
        result = (TextView) findViewById(R.id.result);
    }
    public void finishthing(View view){
        setResult(Activity.RESULT_OK);
        finish();
    }
}