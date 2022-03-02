package com.example.practice_touch_screen_application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class DetectNum extends AppCompatActivity {
    private ImageView imageView;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        imageView = (ImageView) findViewById(R.id.imageview);
        result = (TextView) findViewById(R.id.result);
        File file = new File(getIntent().getStringExtra("path"));
        imageView.setImageDrawable(Drawable.createFromPath(file.toString()));
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        imageView.setLayoutParams(params);
    }
    public void finishthing(View view){
        setResult(Activity.RESULT_OK);
        finish();
    }
}