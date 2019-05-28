package com.example.recognitionface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private Button capturebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (OpenCVLoader.initDebug()) Log.e("Load", "Successful");
        else Log.e("Load", "False");

        save_Haar();
        load_Image();

        capturebutton = (Button)findViewById(R.id.btnChangeCapture);
        capturebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCapture();
            }
        });
    }
    void OpenCapture(){
        Intent intent = new Intent(this, Camera.class);
        startActivity(intent);
    }

    void save_Haar() {
        File filehaar = new File(getFilesDir(), "haarcascade_frontalface_alt.xml");
        if (filehaar.exists()) {
            Log.e("haarcascade", "OK");
        }
        else {
            Log.e("haarcascade", "not OK");
            InputStream inputStream = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            try {
                OutputStream output = new FileOutputStream(filehaar);
                byte[] buffer = new byte[1024]; // or other buffer size
                int read;
                Log.e("read", "start");
                while ((read = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
                output.close();
                Log.e("read", "end");
            } catch (Exception e){
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("haarcascade", "OK");
        }
    }

    void load_Image() {
        File fileimage = new File(getFilesDir(), "image2.jpg");
        if (fileimage.exists()) {
            Log.e("fileimage", "OK");
        }
        else {
            Log.e("fileimage", "not OK");
            InputStream inputStream = getResources().openRawResource(R.raw.img);
            try {
                OutputStream output = new FileOutputStream(fileimage);
                byte[] buffer = new byte[1024]; // or other buffer size
                int read;
//                Log.e("read", "start");
                while ((read = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
                output.close();
                Log.e("read", "end");
            } catch (Exception e){
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File fileimage2 = new File(getFilesDir(), "image2.jpg");
        if (fileimage2.exists()) {
            Log.e("fileimage2", "OK");
        }
        else {
            Log.e("fileimage2", "not OK");
        }
    }
}
