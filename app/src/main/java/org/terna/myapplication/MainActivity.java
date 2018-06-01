package org.terna.myapplication;


import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;

import android.util.SparseArray;

import android.view.SurfaceHolder;

import android.view.SurfaceView;

import android.widget.TextView;



import com.google.android.gms.vision.CameraSource;

import com.google.android.gms.vision.Detector;

import com.google.android.gms.vision.text.TextBlock;

import com.google.android.gms.vision.text.TextRecognizer;



import java.io.IOException;



public class MainActivity extends AppCompatActivity {



    SurfaceView cameraView;

    TextView textView;

    CameraSource cameraSource;

    final int RequestCameraPermissionID = 1001;

    boolean flag = false;
    boolean exit = false;



    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case RequestCameraPermissionID: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        return;

                    }

                    try {

                        cameraSource.start(cameraView.getHolder());

                    } catch (IOException e) {

                        e.printStackTrace();

                    }



                }

            }

            break;

        }

    }



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        cameraView = (SurfaceView) findViewById(R.id.surface_view);

        textView = (TextView) findViewById(R.id.text_view);



        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {

            Log.w("MainActivity", "Detector dependencies are not yet available");

        } else {



            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)

                    .setFacing(CameraSource.CAMERA_FACING_BACK)

                    .setRequestedPreviewSize(1280, 1024)

                    .setRequestedFps(2.0f)

                    .setAutoFocusEnabled(true)

                    .build();

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

                @Override

                public void surfaceCreated(SurfaceHolder surfaceHolder) {



                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {



                            ActivityCompat.requestPermissions(MainActivity.this,

                                    new String[]{Manifest.permission.CAMERA},

                                    RequestCameraPermissionID);

                            return;

                        }

                        cameraSource.start(cameraView.getHolder());

                    } catch (IOException e) {

                        e.printStackTrace();

                    }

                }



                @Override

                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {



                }



                @Override

                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                    cameraSource.stop();

                }

            });



            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {

                @Override

                public void release() {



                }



                @Override

                public void receiveDetections(Detector.Detections<TextBlock> detections) {



                    final SparseArray<TextBlock> items = detections.getDetectedItems();

                    if(items.size() != 0)

                    {

                        textView.post(new Runnable() {

                            @Override

                            public void run() {

                                StringBuilder stringBuilder = new StringBuilder();

                                    for (int i = 0; i < items.size(); ++i)

                                    {

                                        TextBlock item = items.valueAt(i);

                                        stringBuilder.append(item.getValue());

                                        stringBuilder.append("\n");

                                        String number = checkMobileNo(stringBuilder.toString());

                                        if (number.length() == 10) {
                                            Intent intent = new Intent(MainActivity.this, phone.class);
                                            intent.putExtra("number", number);
                                            //finish();
                                            startActivity(intent);
                                        }


                                    }

                                    textView.setText(stringBuilder.toString());

                            }

                        });

                    }

                }

            });

        }

    }

    private String checkMobileNo(String string) {

        int i,j,a,b,last=0,start=0;
        String number="";
        for (i=0; i< string.length();i++)
        {   if (Character.isDigit(string.charAt(i))) {
                 a=b=i;
                for (j=0;j<10;j++)
                { if(b < string.length())
                    if (Character.isDigit(string.charAt(b)))
                    {
                        b++;
                    }
                    else
                        break;
                }

                if(b==(a+10))
                {
                    last=b;
                    start=a;
                }
            }

        }

        number=string.substring(start,last);

        return number;
    }

}
