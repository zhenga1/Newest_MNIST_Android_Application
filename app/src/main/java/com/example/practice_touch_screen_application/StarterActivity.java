package com.example.practice_touch_screen_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StarterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
    }
    public void enterApp(View view){
        Intent intent = new Intent(StarterActivity.this,MainActivity.class);
        startActivity(intent);
    }
}