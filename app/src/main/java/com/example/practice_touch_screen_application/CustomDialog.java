package com.example.practice_touch_screen_application;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomDialog extends Dialog implements View.OnClickListener{
    public Activity activity;
    public static final int DIALOG_REQUEST=8000;
    public String string = Environment.getExternalStorageDirectory()+"/Screenshot0.jpg";
    public Dialog d;
    public Button yes, no;
    public CustomDialog(Activity a) {
        super(a);
        activity=a;
    }
    public CustomDialog(Activity a,String string) {
        super(a);
        activity=a;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_layout);
        yes = findViewById(R.id.btn_yes);
        no = findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        d = this;
    }
    private void close(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                d.dismiss();
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_yes:
                //Savvewhatever
                close();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(activity,DetectNum.class);
                        intent.putExtra("path",string);
                        activity.startActivityForResult(intent,DIALOG_REQUEST);
                    }
                });
                break;
            case R.id.btn_no:
                close();
                break;
            default:
                break;
        }
    }
}
