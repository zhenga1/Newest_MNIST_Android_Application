package com.example.practice_touch_screen_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawingView drawView;
    private final int WIDTH=400,HEIGHT=400,padding=50;
    private Button erase, start, end,save,restore,scan;
    private ImageButton currPaint;
    private int WRITE_REQUEST_CODE=100,READ_REQUEST_CODE=200,number=0;
    private LinearLayout linearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView = (DrawingView)findViewById(R.id.drawing);
        linearlayout = findViewById(R.id.ll1);
        erase = (Button)findViewById(R.id.erase);
        erase.setOnClickListener(this);
        start=(Button)findViewById(R.id.start);
        start.setOnClickListener(this);
        save=(Button)findViewById(R.id.save);
        save.setOnClickListener(this);
        scan=(Button)findViewById(R.id.scan);
        scan.setOnClickListener(this);
        end=(Button)findViewById(R.id.end);
        end.setOnClickListener(this);
        restore=(Button)findViewById(R.id.restore);
        restore.setOnClickListener(this);
        currPaint = (ImageButton) linearlayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
    }
    public void paintClicked(View v)
    {
        if(v!=currPaint){
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton)v;
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            String color = v.getTag().toString();
            drawView.setColor(color);
        }
    }
    public void onClick(View view){
        if(view.getId()==start.getId()) {
            drawView.startdrawing(true);
        }else if(view.getId()==end.getId()){
            drawView.startdrawing(false);
        }else if(view.getId()==erase.getId())
        {
            drawView.seteraser(true);
        }else if(view.getId()==save.getId()){
            if(savePermissions())
            {
                savefile();
            }
        }else if(view.getId()==restore.getId()) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Image" + Integer.toString(number - 1) + ".jpg");
            while(!file.exists() && number!=0)
            {
                number-=1;
                file = new File(Environment.getExternalStorageDirectory() + "/Image" + Integer.toString(number - 1) + ".jpg");
            }
            if(number==0 || !file.exists()){
                Toast.makeText(getApplicationContext(),"Nothing is made yet",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                drawView.setBackgroundtoFile(file);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }else if(view.getId()==scan.getId()){
            int centerX = (int)this.getWindowManager().getDefaultDisplay().getWidth()/4;
            int centerY = (int)this.getWindowManager().getDefaultDisplay().getHeight()/4;
            try{
                drawView.startscan(padding,centerX,centerY,WIDTH,HEIGHT);
            }catch( Exception e){
                e.printStackTrace();
            }
        }
    }
    private void savefile(){
        File file = new File(Environment.getExternalStorageDirectory()+"/Image"+ Integer.toString(number)+".jpg");
        try {
            drawView.canvasBitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        number+=1;
    }
    private boolean savePermissions(){
        int write = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE);
        if(write == PackageManager.PERMISSION_GRANTED && read ==PackageManager.PERMISSION_GRANTED){
            return true;
        }
        if(write != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    Toast.makeText(getApplicationContext(),"The application needs the permission of write storage in order to save the drawn image",Toast.LENGTH_LONG).show();
                }
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        WRITE_REQUEST_CODE);
            }
        }
        if(read != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    Toast.makeText(getApplicationContext(),"The application needs the permission of write storage in order to save the drawn image",Toast.LENGTH_LONG).show();
                }
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                        READ_REQUEST_CODE);
            }
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        savefile();
    }
}