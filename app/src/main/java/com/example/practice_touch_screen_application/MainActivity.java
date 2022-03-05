package com.example.practice_touch_screen_application;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawingView drawView;
    private int WIDTH,HEIGHT,padding;
    private boolean scanned=false,firstscale=true,isize=true, dsize=true;
    private Button erase, start, end,save,restore,scan,plus,minus,capture;
    private ImageButton currPaint;
    private ImageView imageView;
    private float posX,posY,dX,dY;
    private ConstraintLayout constraintLayout;
    private int WRITE_REQUEST_CODE=100,READ_REQUEST_CODE=200,number=0,scrnnum=58;
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
        initscanfeatures();
    }
    private Bitmap takescreenshot(int x, int y, int width,int height){
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return Bitmap.createBitmap(bitmap, x, y, width, height);
    }
    private void initscanfeatures(){
        constraintLayout = (ConstraintLayout)findViewById(R.id.content_view);
        imageView = new ImageView(MainActivity.this);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) drawView.getLayoutParams();
        imageView.setImageResource(R.mipmap.scanning_foreground);
        ConstraintLayout.LayoutParams newparams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        newparams.leftToLeft=params.leftToLeft;
        newparams.rightToRight=params.rightToRight;
        newparams.topToBottom=params.topToBottom;
        newparams.bottomToTop=params.bottomToTop;
        imageView.setLayoutParams(newparams);
        imageView.setId(imageView.generateViewId());
        posX = imageView.getX(); posY=imageView.getY();
        WIDTH=imageView.getMaxWidth();HEIGHT=imageView.getMaxHeight();
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(view.getId()==imageView.getId()){
                    float touchX=event.getX();
                    float touchY=event.getY();
                    if(touchX<=posX+WIDTH && touchX>=posX)
                    {
                        if(touchY<=posY+HEIGHT && touchY>=posY)
                        {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    dX=view.getX()-event.getRawX();
                                    dY=view.getY()-event.getRawY();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    view.animate()
                                            .x(dX+event.getRawX())
                                            .y(dY+event.getRawY())
                                            .setDuration(0)
                                            .start();

                                    break;
                                case MotionEvent.ACTION_UP:
                                    dX=0;dY=0;
                                    break;
                                default:
                                    return false;
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        };
        imageView.setOnTouchListener(onTouchListener);
        imageView.setVisibility(View.GONE);
        constraintLayout.addView(imageView, -1);

        capture = new Button(MainActivity.this);
        capture.setVisibility(View.GONE);
        capture.setBackgroundColor(Color.MAGENTA);
        capture.setTextColor(Color.BLACK);
        capture.setId(View.generateViewId());
        capture.setPadding(20,20,20,20);
        capture.setText("Capture Area");
        ConstraintLayout.LayoutParams capParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        capParams.rightToRight=constraintLayout.getId();
        capParams.leftToLeft=constraintLayout.getId();
        capParams.topToTop=drawView.getId();
        constraintLayout.addView(capture,-1,capParams);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int width = imageView.getWidth();
                int height = imageView.getHeight();
                int[] p = new int[2]; imageView.getLocationOnScreen(p);
                int xcoord = (int) (p[0]+Math.round(width*0.08));
                int ycoord = (int) (p[1]+Math.round(height*0.08));
                int awidth = (int) (Math.round(width*0.84));
                int aheight = (int) (Math.round(height*0.84));
                Bitmap screenshot = takescreenshot(xcoord,ycoord,awidth,aheight);
                //Bitmap screenshot = takescreenshot(p[0]+60,p[1]+60,width-120,height-120);

                if(screenshot==null){
                    Toast.makeText(getApplicationContext(),"Image capture has failed for " +
                            "an unknown reason. Please consult the great developer Aaron Haowen Zheng " +
                            "for more information",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(savePermissions()){
                    File folder = new File(Environment.getExternalStorageDirectory()+"/practice_app");
                    boolean success=true;
                    File file;
                    if(!folder.exists()) success = folder.mkdir();
                    if(success){
                        file = new File(Environment.getExternalStorageDirectory()+"/practice_app/"+ Integer.toString(scrnnum)+".jpg");
                        try {
                            screenshot.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        scrnnum+=1;
                        Toast.makeText(getApplicationContext(),"Sucessfully captured and saved image",Toast.LENGTH_SHORT).show();
                        CustomDialog customDialog = new CustomDialog(MainActivity.this,file.getAbsolutePath());
                        customDialog.show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Cannot save image, it is necessary.",Toast.LENGTH_SHORT).show();
                }

            }
        });

        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomToBottom=drawView.getId();
        layoutParams.leftToLeft=params.leftToLeft;
        layoutParams.rightToRight=params.rightToRight;
        linearLayout.setLayoutParams(layoutParams);
        plus = new Button(MainActivity.this);
        plus.setId(View.generateViewId());
        plus.setPadding(0,0,0,0);
        plus.setBackgroundColor(Color.WHITE);
        plus.setTextColor(Color.BLACK);
        plus.setText("+");
        plus.setTextSize(20);
        minus = new Button(MainActivity.this);
        minus.setPadding(0,0,0,0);
        minus.setId(View.generateViewId());
        minus.setBackgroundColor(Color.WHITE);
        minus.setTextColor(Color.BLACK);
        minus.setText("-");
        minus.setTextSize(20);
        linearLayout.addView(plus,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(minus,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        View.OnClickListener onScaleClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firstscale) {
                    imageView.getLayoutParams().width = imageView.getWidth();
                    imageView.getLayoutParams().height = imageView.getHeight();
                    firstscale=false;
                }
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int theight = displayMetrics.heightPixels; int twidth = displayMetrics.widthPixels;
                if(imageView.getLayoutParams().height>=theight || imageView.getLayoutParams().width>=twidth){
                    Toast.makeText(getApplicationContext(),"The scanning block cannot be expanded further",Toast.LENGTH_LONG).show();
                    isize=false;
                }
                if(imageView.getLayoutParams().height<=0 || imageView.getLayoutParams().width<=0){
                    Toast.makeText(getApplicationContext(),"The scanning block cannot be decreased in size further",Toast.LENGTH_LONG).show();
                    dsize=false;
                }
                if(view.getId()==plus.getId() && isize)
                {
                    dsize=true;
                    imageView.getLayoutParams().height=imageView.getLayoutParams().height+50;
                    imageView.getLayoutParams().width=imageView.getLayoutParams().width+50;
                    imageView.requestLayout();
                    //INCREASE THE SIZE OF THE SCANNING IMAGE THING

                }else if(view.getId()==minus.getId() && dsize){
                    isize=true;
                    imageView.getLayoutParams().height=imageView.getLayoutParams().height-50;
                    imageView.getLayoutParams().width=imageView.getLayoutParams().width-50;
                    imageView.requestLayout();
                    //DECREASE THE SIZE OF THE SCANNING IMAGE THING

                }
            }
        };
        plus.setOnClickListener(onScaleClick);
        minus.setOnClickListener(onScaleClick);
        plus.setVisibility(View.GONE);
        minus.setVisibility(View.GONE);
        constraintLayout.addView(linearLayout,-1);
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
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CustomDialog.DIALOG_REQUEST){
            if(resultCode==Activity.RESULT_OK){
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
            }

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
            else{
                Toast.makeText(getApplicationContext(),"Application cannot save.Required " +
                        "save permissions not granted",Toast.LENGTH_SHORT).show();
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
            if(!scanned) {
                scanned=true;
                capture.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                plus.setVisibility(View.VISIBLE);
                minus.setVisibility(View.VISIBLE);
            }
            else{
                scanned=false;
                imageView.setVisibility(View.GONE);
                capture.setVisibility(View.GONE);
                plus.setVisibility(View.GONE);
                minus.setVisibility(View.GONE);
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