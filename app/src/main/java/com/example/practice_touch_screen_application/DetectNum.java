package com.example.practice_touch_screen_application;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DetectNum extends AppCompatActivity {
    private ImageView imageView;
    private TextView result;
    private int[] array;
    private int[] pixels;
    private float[][] outputarr = new float[1][10];
    private ByteBuffer compressedBuffer;
    private Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        imageView = (ImageView) findViewById(R.id.imageview);
        result = (TextView) findViewById(R.id.result);
        if(getIntent().hasExtra("path")) {
            File file = new File(getIntent().getStringExtra("path"));
            if (!file.exists()) return;
            imageView.setImageDrawable(Drawable.createFromPath(file.toString()));
        }
        else {
            Bitmap bitmap = Bitmap.createScaledBitmap(MainActivity.scrnshotbitmap,1080,1080,false);
            imageView.setImageDrawable(new BitmapDrawable(getResources(),bitmap));
        }
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        imageView.setLayoutParams(params);
        result.setText("Processing...");
        startmachinelearningmodel();

    }
    public void finishthing(View view){
        setResult(Activity.RESULT_OK);
        finish();
    }
    public void startmachinelearningmodel(){
        try{
            interpreter = new Interpreter(loadModelFile(),null);
        }catch (Exception e){
            e.printStackTrace();
        }
        Bitmap imageBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap,28,28,false);
        Log.i("thing",Integer.toString(scaledBitmap.getByteCount()));
        Tensor inputTensor = interpreter.getInputTensor(0);
        pixels = new int[inputTensor.shape()[1]*inputTensor.shape()[2]];
        array = new int[28*28];
        scaledBitmap.getPixels(pixels, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
        for(int i=0;i<scaledBitmap.getWidth();i++){
            for(int j=0;j<scaledBitmap.getHeight();j++){
                int r = Color.red(pixels[28*i+j]);
                int g = Color.green(pixels[28*i+j]);
                int b = Color.blue(pixels[28*i+j]);
                int avg = (r + g + b) / 3;
                int newColor = Color.argb(255, 255 - avg, 255 - avg, 255 - avg);
                array[28*i+j] = (newColor);
            }
        }
        Bitmap newbitmap = scaledBitmap.copy(scaledBitmap.getConfig(),true);
        newbitmap.setPixels(array,0,scaledBitmap.getWidth(),0,0,scaledBitmap.getWidth(),scaledBitmap.getHeight());
        int imageSize = 28*28;
        compressedBuffer = ByteBuffer.allocateDirect(imageSize*4);
        compressedBuffer.order(ByteOrder.nativeOrder());
        convertBitmaptoByteBuffer();
        Tensor outputTensor = interpreter.getOutputTensor(0);
        TensorBuffer outputBuffer= TensorBuffer.createFixedSize(outputTensor.shape(),outputTensor.dataType());
        TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{1, 28, 28,1}, DataType.FLOAT32);
        inputFeature.loadBuffer(compressedBuffer,new int[]{1,28,28,1});
        interpreter.setNumThreads(2);
        interpreter.run(compressedBuffer,outputarr);
        float[] maxlist = new float[2];
        maxlist[0]=0f;maxlist[1]=0f;
        for(int i=0;i<10;i++){
            if(maxlist[0]<outputarr[0][i]){
                maxlist[0]=outputarr[0][i];
                maxlist[1]=i;
            }
        }
        int pred = (int)maxlist[1];
        result.setText(String.valueOf(pred));
    }
    private void convertBitmaptoByteBuffer(){
        compressedBuffer.rewind();
        for (int i=0;i<28*28;i++) {
            int pixel = array[i];
            float r = (pixel >> 16) & 0xFF;
            float g = (pixel >> 8) & 0xFF;
            float b = (pixel) & 0xFF;
            float avgpixel = (r+g+b)/3.0f;
            float normalizedpixel = avgpixel/ 255.0f;
            compressedBuffer.putFloat(normalizedpixel);
        }
    }
    private MappedByteBuffer loadModelFile() throws IOException
    {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("mnist.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();

        long startOffset = assetFileDescriptor.getStartOffset();
        long len = assetFileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,len);

    }
}