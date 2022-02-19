package com.example.practice_touch_screen_application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class BorderedTextView {
    private Paint interiorpaint;
    private Paint exteriorpaint;

    public BorderedTextView(int border, int background){
        interiorpaint = new Paint();
        interiorpaint.setStyle(Paint.Style.FILL);
        interiorpaint.setColor(background);
        interiorpaint.setAntiAlias(true);
      //  interiorpaint.setAlpha(255);

        exteriorpaint= new Paint();
        exteriorpaint.setStyle(Paint.Style.FILL_AND_STROKE);
        exteriorpaint.setColor(border);
       // exteriorpaint.setAlpha(255);
    }
    protected void drawView(Canvas canvas, float X, float Y,float width,float height){
        canvas.drawRect(X,Y+height,X+width,Y,exteriorpaint);
        canvas.drawRect(X,Y+height,X+width,Y,interiorpaint);
    }
}
