package com.example.practice_touch_screen_application;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import androidx.annotation.Nullable;

import java.io.File;

public class DrawingView extends View {
    //drawing path
    private Path drawPath;
    private boolean draw=false,erase=false,scanned=false;
    private float posX=0,posY=0,WIDTH=0,HEIGHT=0,padding=0;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    protected Canvas drawCanvas;
    //canvas bitmap
    protected Bitmap canvasBitmap;
    public DrawingView(Context context) {

        super(context);
        setupDrawing();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        setupDrawing();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupDrawing();
    }
    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

    }
    public void setBackgroundtoFile(File file){
        canvasBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        canvasBitmap=canvasBitmap.copy(Bitmap.Config.ARGB_8888,true);
        drawCanvas=new Canvas(canvasBitmap);
        invalidate();
    }
    public void setColor(String newColor){
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//view given size
        super.onSizeChanged(w,h,oldw,oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }
    @Override
    protected void onDraw(Canvas canvas) {
//draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }
    public void startdrawing(boolean b){
        draw = b;
        seteraser(false);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//detect user touch
        float touchX = event.getX();
        float touchY = event.getY();
        if (draw) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
            invalidate();
            return true;
        }
        else if(scanned){
            if(touchX<=posX+WIDTH && touchX>=posX)
            {
                if(touchY<=posY+HEIGHT && touchY>=posY)
                {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            draw=false;
                            scanned=true;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            drawRect(padding,touchX-WIDTH/2,touchY-HEIGHT/2,WIDTH,HEIGHT);
                            break;
                        case MotionEvent.ACTION_UP:
                            scanned=false;
                            draw=true;
                            break;
                        default:
                            return false;
                    }
                }
            }
            return true;
        }
        else{
            return super.onTouchEvent(event);
        }
    }
    public void seteraser(boolean b){
        erase = b;
        if(erase) {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            drawPaint.setStrokeWidth(300);
            draw=true;
        }
        else {
            drawPaint.setXfermode(null);
            drawPaint.setStrokeWidth(20);
            draw=true;
        }
    }
    protected void drawRect(float padding, float X, float Y, float width, float height){
        Paint interiorpaint = new Paint();
        interiorpaint.setStyle(Paint.Style.STROKE);
        interiorpaint.setStrokeWidth(padding+2);
        interiorpaint.setAntiAlias(true);
        interiorpaint.setColor(Color.WHITE);
        drawCanvas.drawRect(posX,posY,posX+WIDTH,posY+HEIGHT,interiorpaint);
        invalidate();
        Paint exteriorpaint = new Paint();
        exteriorpaint.setStyle(Paint.Style.STROKE);
        exteriorpaint.setStrokeWidth(padding);
        exteriorpaint.setColor(Color.BLACK);
        drawCanvas.drawRect(X,Y,X+width,Y+height,exteriorpaint);
        invalidate();
        posX=X;posY=Y;WIDTH=width;HEIGHT=height;
    }
    protected void startscan(int pad,int X, int Y,int width, int height){
        //int centerX = (int)this.getResources().getDisplayMetrics().widthPixels/2;
        //int centerY= (int) this.getResources().getDisplayMetrics().heightPixels/2;
        padding=pad;
        if(!scanned){
            Paint exteriorpaint = new Paint();
            exteriorpaint.setStyle(Paint.Style.STROKE);
            exteriorpaint.setStrokeWidth(pad);
            exteriorpaint.setColor(Color.BLACK);
            /*Paint interiorpaint = new Paint();
            interiorpaint.setStyle(Paint.Style.FILL);
            interiorpaint.setColor(Color.GREEN);
            interiorpaint.setAntiAlias(true);*/
            drawCanvas.drawRect(X,Y,X+width,Y+height,exteriorpaint);
            //  drawView.drawCanvas.drawRect(centerX+padding,centerY+padding,centerX+WIDTH-padding,centerY+HEIGHT-padding,interiorpaint);
            invalidate();
            posX=X;posY=Y;WIDTH=width;HEIGHT=height;
            scanned=true;
            draw=false;
        }
        else{
            Paint interiorpaint = new Paint();
            interiorpaint.setStyle(Paint.Style.STROKE);
            interiorpaint.setStrokeWidth(pad);
            interiorpaint.setAntiAlias(true);
            interiorpaint.setColor(Color.WHITE);
            drawCanvas.drawRect(posX,posY,posX+WIDTH,posY+HEIGHT,interiorpaint);
            invalidate();
            scanned=false;
        }

    }
}
