package edu.kaist.cps.ubitap.chessclient;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import DataStructure.AudioChunk;
import DataStructure.Utils;

public class Main extends AppCompatActivity {

    Button connect,disconnect,startAudioStream,stopAudioStream;
    Context context = this;
    //Path for storing audio files that are recorderd. (This has no use in the actual application, i used this for testing.)
    Wrapper engine = new Wrapper("/storage/emulated/0/AudioRecorder/",this);
    public static final int RequestPermissionCode =1;

    //The following two values are just hardcoded here but will be replaced by the values provided from the UI.
    public static int SERVERPORT=3352;
    public static String IP ="192.168.0.4";
    public static String ID="";

    public static OutputStream out;
    public static InputStream in ;
    public static Socket serverSocket = null;

    public static InetAddress address;
    public static ByteArrayOutputStream byteStream ;
    public static ObjectOutputStream os;

    public static long t1;

    public static DatagramSocket dSock ;


    //Just the tag for Log
    public static String tag = "UbiTap";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Reference to the buttons created of the UI
        connect = findViewById(R.id.connect);
        disconnect= findViewById(R.id.disConnect);
        startAudioStream= findViewById(R.id.startAudioStream);
        stopAudioStream=findViewById(R.id.stopAudioStream);

        //Button click trigger these function for connect button
        connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                connect.setEnabled(false);

                EditText deviceID=findViewById(R.id.IDE);
                EditText HostIP=findViewById(R.id.hostNameE);
                EditText HostPort=findViewById(R.id.hostPortE);

                Main.ID= deviceID.getText().toString();
                Main.IP= HostIP.getText().toString();
                Main.SERVERPORT = Integer.parseInt(HostPort.getText().toString());

                Wrapper.isRecording = true;

                try {

                    Main.dSock = new DatagramSocket();


                    Main.address = InetAddress.getByName(Main.IP);
                    Main.byteStream = new ByteArrayOutputStream(5000);
                    Main.os = new ObjectOutputStream(new BufferedOutputStream(Main.byteStream));




                    byte[] b = new byte[1];

                    AudioChunk c = new AudioChunk(b,Utils.getCurrentTime(),Main.ID);
                    c.dataType=1111;
                    Utils.sendData(c);
                } catch (IOException e) {
                    Log.d(tag,"Exception while creating the Sockets in the Main file.");
                    Utils.Alert(context,"Connection Error",e.getMessage());
                    connect.setEnabled(true);
                }

            }
        });

        //Button click trigger these function for disconnect button
        disconnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {


                    byte[] b = new byte[1];
                    AudioChunk c = new AudioChunk(b,Utils.getCurrentTime(),Main.ID);
                    c.dataType=1010;
                    Utils.sendData(c);

                    try {
                        Main.os.close();
                        Main.dSock.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                connect.setEnabled(true);
            }
        });

        startAudioStream.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                engine.startRecording();//Start the recording and start to get data from the microphone.
                try {
                    Thread.sleep(100);
                    Detector d = new Detector(context);
                    d.start();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startAudioStream.setEnabled(false);
            }
        });

        stopAudioStream.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                engine.stopRecording();




                startAudioStream.setEnabled(true);
            }
        });
    }
}