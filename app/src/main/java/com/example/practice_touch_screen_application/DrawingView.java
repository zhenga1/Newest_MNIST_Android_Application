package com.example.practice_touch_screen_application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.File;

public class DrawingView extends View {
    //drawing path
    private Path drawPath;
    private boolean draw=false,erase=false;
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
        drawPaint.setStrokeWidth(80);
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
            drawPaint.setStrokeWidth(80);
            draw=true;
        }
    }
}
