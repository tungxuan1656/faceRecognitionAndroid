package com.example.recognitionface;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Recognition extends AppCompatActivity {
    Button btnRecognition;
    TextView nameText;
    ImageView imageView;
    EditText resultText;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        btnRecognition = (Button)findViewById(R.id.btnRecognition);
        nameText = (TextView)findViewById(R.id.nameText);
        imageView = (ImageView)findViewById(R.id.imageView);
        resultText = (EditText) findViewById(R.id.resulttext);

        queue = Volley.newRequestQueue(this);

        File file = new File(getFilesDir(), "image.jpg");
        if (file.exists())
            Log.e("recog image.jpg", "OK");
        else
            Log.e("recog image.jpg","not OK");

        final int number_face = displayFaceDetection();

        btnRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number_face != 1) {
                    Toast.makeText(Recognition.this, "Không thể nhận diện", Toast.LENGTH_SHORT).show();
                }
                else Recognition();
            }
        });
    }

    int displayFaceDetection() {
        Mat image = Imgcodecs.imread(getFilesDir() + "/image.jpg");
        Log.e("image", image.toString());

        ArrayList<Mat> facecrop = detectFace(image);
        Imgcodecs.imwrite(getFilesDir() + "/image_detect_face.jpg", image);

        nameText.setText("Đã phát hiện " + facecrop.size() + " khuôn mặt");
        if (facecrop.size() == 1) {
            Mat newimage = new Mat();
            Imgproc.resize(facecrop.get(0), newimage, new Size(100,100));
            Imgcodecs.imwrite(getFilesDir() + "/image_face.jpg", newimage);
        }
        imageView.setImageURI(Uri.parse(getFilesDir() + "/image_detect_face.jpg"));
        return facecrop.size();
    }

    ArrayList<Mat> detectFace(Mat frame) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();
        int absoluteFaceSize=0;
        CascadeClassifier faceCascade=new CascadeClassifier();
        ArrayList<Mat> facecrop = new ArrayList<Mat>();

        File filehaar = new File(getFilesDir(), "haarcascade_frontalface_alt.xml");
        if (filehaar.exists()) Log.e("haar", "Exists");
        else Log.e("haar", "not Exists");
        String path = filehaar.getPath();
        Log.e("haarcas", path);
        faceCascade.load(path);

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);
        Log.e("gray frame", grayFrame.toString());

        // compute minimum face size (1% of the frame height, in our case)

        int height = grayFrame.rows();
        if (Math.round(height * 0.2f) > 0)
        {
            absoluteFaceSize = Math.round(height * 0.01f);
        }

        // detect faces
        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(absoluteFaceSize, absoluteFaceSize), new Size(height,height));


//      each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Rect newrectface = new Rect(facesArray[i].x, facesArray[i].y, facesArray[i].width, facesArray[i].height);
            Mat newface = new Mat(grayFrame, newrectface);
//            Log.e("X", newface.cols() + " " + newface.rows());
            if (newface.cols() < 500) continue;

            facecrop.add(newface);
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 20);
        }
        return facecrop;
    }

    void Recognition(){
//        Log.e("Test", "OK");
        nameText.setText("Wait...");

        String s = "";
        File filein = new File(getFilesDir(), "image_face.jpg");
        FileInputStream fin = null;
        List<String> strl = new ArrayList<String>();
//        Log.e("Test", "OK");
        try {
            fin = new FileInputStream(getFilesDir() + "/image_face.jpg");
            int i = 0;
            while ((i = fin.read()) != -1) {
                s += i + "_";
            }
            fin.close();
        } catch (Exception e) {

        } finally {
            try {
                fin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        s = TextUtils.join("_", strl);
//        Log.e("String s ", s.length() + "");
        Random rand = new Random();
        int n1 = rand.nextInt(100000);
        int n2 = rand.nextInt(100000);
        String n = n1 + "" + n2;
//        Log.e("Test random", n);

        int j, k=0;
        for (int i = 0; i < s.length(); i+=500) {
            if (i+500 > s.length()) j = s.length();
            else j = i+500;
            String ss = k + "=" + n + "=" + s.substring(i, j) + "=";

            sendRequest(ss);
            k++;
        }
//        Log.e("Test send test", k + " OK");
        sendRequest(k + "=" + n + "=-1=" + k);
//        nameText.setTextColor(Color.RED);
    }

    void sendRequest(String s) {
        String url = "https://intense-lowlands-83977.herokuapp.com/recognition/a/" + s;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        nameText.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                nameText.setText("That didn't work!");
            }
        });
        queue.add(stringRequest);
//        Log.e("Response", String.valueOf(stringRequest.getUrl().length()));
    }
}
