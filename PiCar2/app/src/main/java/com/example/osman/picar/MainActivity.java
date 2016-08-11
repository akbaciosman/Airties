package com.example.osman.picar;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor accSensor;
    private Sensor gravSensor;
    TextView display,display1;
    Button forward,back,right,left,stop,connectHost,exit;
    MySyncTask asyncTask;

    String ipAddress; //Connect to Host
    int portNumber;

    StringBuilder st=null;      //for button
    StringBuilder build=null; //for sensor

    boolean connect;
    Socket socket;
    PrintStream writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display =  (TextView) findViewById(R.id.display);
        final Context context = this;

        forward = (Button)findViewById(R.id.ileri);
        back =(Button)findViewById(R.id.geri);
        right =(Button)findViewById(R.id.sag);
        left =(Button)findViewById(R.id.sol);
        stop = (Button)findViewById(R.id.stop);
        exit = (Button)findViewById(R.id.exit);
        connectHost = (Button)findViewById(R.id.connect);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);


        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   st.delete(0, st.toString().length());
                   st.append("w");
                   st.append("_");
                   st.append("0");
                   writer.println(st);
                   writer.flush();


            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    st.delete(0, st.toString().length());
                    st.append("s");
                    st.append("_");
                    st.append("0");

                    writer.println(st);
                    writer.flush();


            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(st.indexOf("w")==0){
                    st.delete(0,st.toString().length());
                    st.append("w_2");
                }else if(st.indexOf("s")==0){
                    st.delete(0,st.toString().length());
                    st.append("s_2");
                }

                writer.println(st);
                writer.flush();

            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(st.indexOf("w")==0){
                    st.delete(0,st.toString().length());
                    st.append("w_8");
                }else if(st.indexOf("s")==0){
                    st.delete(0,st.toString().length());
                    st.append("s_8");
                }

                writer.println(st);
                writer.flush();

            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                writer.println("x");
                writer.flush();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                st.delete(0, st.toString().length());
                st.append("e");

                writer.println(st);
                writer.flush();
                writer.close();
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.exit(1);


            }
        });

        connectHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.propmts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText ip = (EditText) promptsView
                        .findViewById(R.id.ip_address);
                final EditText port = (EditText) promptsView
                        .findViewById(R.id.port_number);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        ipAddress=(String) ip.getText().toString();
                                        portNumber=Integer.parseInt(port.getText().toString());

                                        createThread();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

    }


    public void createThread(){
        asyncTask=new MySyncTask ();

        asyncTask.execute(10);
        st = new StringBuilder("w_0");
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float mSensorX, mSensorY,mSensorZ;

        mSensorX = event.values[0];
        mSensorY = event.values[1];
        mSensorZ = event.values[2];


        build = new StringBuilder();
        build.append("X:");
        build.append(mSensorX);
        build.append("   Y:");
        build.append(mSensorY);
        //display.setText(build);
        /*
        writer.println(build);
        writer.flush();*/
    }





    class MySyncTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {




            try {
                socket = new Socket(ipAddress, portNumber);
                System.err.println("socket return value : " + socket.isClosed());
                connect=true;

                    try {


                        OutputStream out = socket.getOutputStream();
                        writer = new PrintStream(out);

                        while (connect) {


                        }


                        writer.close();


                    } catch (Exception e) {
                        System.err.println("Error: write socket");
                        OutputStream out = socket.getOutputStream();
                        PrintStream writer = new PrintStream(out);
                        e.printStackTrace();
                    }
                socket.close();
            }catch (IOException e){
                System.err.println("Socket");
                e.printStackTrace();
            }

            return  null;

        }

        protected void onPostExecute(String result) {
            String temp="Connect Host";
            //display.setText(temp);
        }


    }



    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,accSensor,sensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {



    }
}
