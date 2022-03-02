package com.example.practice_touch_screen_application;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
        int imageSize = scaledBitmap.getRowBytes() * scaledBitmap.getHeight();
        ByteBuffer compressedBuffer = ByteBuffer.allocateDirect(imageSize);
        scaledBitmap.copyPixelsToBuffer(compressedBuffer);
        Tensor outputTensor = interpreter.getOutputTensor(0);
        TensorBuffer outputBuffer= TensorBuffer.createFixedSize(outputTensor.shape(),outputTensor.dataType());
        interpreter.run(compressedBuffer,outputBuffer.getBuffer().rewind());
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
    private MappedByteBuffer loadModelFile() throws IOException
    {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("mnist.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();

        long startOffset = assetFileDescriptor.getStartOffset();
        long len = assetFileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,len);

    }
}