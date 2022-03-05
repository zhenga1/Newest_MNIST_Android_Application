package com.example.practice_touch_screen_application;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
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
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DetectNum extends AppCompatActivity {
    private ImageView imageView;
    private TextView result;
    private int[] pixels;
    private TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{1, 28, 28,1}, DataType.FLOAT32);
    private ByteBuffer compressedBuffer;
    private Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        imageView = (ImageView) findViewById(R.id.imageview);
        result = (TextView) findViewById(R.id.result);
        File file = new File(getIntent().getStringExtra("path"));
        if(!file.exists()) return;
        imageView.setImageDrawable(Drawable.createFromPath(file.toString()));
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
        int imageSize = scaledBitmap.getHeight() * scaledBitmap.getWidth();
        compressedBuffer = ByteBuffer.allocateDirect(imageSize*4);
        convertBitmaptoByteBuffer(scaledBitmap);
        Tensor outputTensor = interpreter.getOutputTensor(0);
        TensorBuffer outputBuffer= TensorBuffer.createFixedSize(outputTensor.shape(),outputTensor.dataType());
        inputFeature.loadBuffer(compressedBuffer,new int[]{1,28,28,1});
        interpreter.run(inputFeature.getBuffer(),outputBuffer.getBuffer().rewind());
        float[] floatarray = outputBuffer.getFloatArray();
        float[] maxlist = new float[2];
        maxlist[0]=0f;maxlist[1]=0f;
        for(int i=0;i<floatarray.length;i++){
            if(maxlist[0]<floatarray[i]){
                maxlist[0]=floatarray[i];
                maxlist[1]=i;
            }
        }
        int pred = (int)maxlist[1];
        result.setText(String.valueOf(pred));
    }
    private void convertBitmaptoByteBuffer(Bitmap bitmap){
        compressedBuffer.rewind();
        Tensor inputTensor = interpreter.getInputTensor(0);
        pixels = new int[inputTensor.shape()[1]*inputTensor.shape()[2]];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i=0;i<inputTensor.shape()[1]*inputTensor.shape()[2];i++) {
            int pixel = pixels[i];
            float val=convertPixel(pixel);
            compressedBuffer.putFloat(val);
        }
    }
    private float convertPixel(int pixel){
        float rChannel = (pixel >> 16) & 0xFF;
        float gChannel = (pixel >> 8) & 0xFF;
        float bChannel = (pixel) & 0xFF;
        int pixelValue = (int)((rChannel + gChannel + bChannel) / 3 );
        pixelValue=Math.min(pixelValue,255);
        pixelValue=Math.max(pixelValue,0);
        return 1.0f-pixelValue/255f;
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