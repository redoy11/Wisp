package com.company.wisp.wisp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

public class AudioRecorder extends AppCompatActivity implements TextToSpeech.OnInitListener
        , SimpleGestureFilter.SimpleGestureListener {

    private SimpleGestureFilter detector;
    private TextToSpeech tts;
    //private Button btnSpeak;
    //private EditText txtText;
    private TextView txtView;
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private String outputFile = null;
    private TextView text;

    private boolean startBtn=true;
    private boolean stopBtn=false;
    private boolean playBtn=false;
    private boolean stopPlayBtn=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        tts = new TextToSpeech(this, this);
        detector = new SimpleGestureFilter(this,this);
        txtView = (TextView) findViewById(R.id.RecordTextView);

        text = (TextView) findViewById(R.id.RecordTextView);
        // store it to sd card
        outputFile = Environment.getExternalStorageDirectory().
                getAbsolutePath() + "/javacodegeeksRecording.3gpp";

        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myRecorder.setOutputFile(outputFile);



    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // btnSpeak.setEnabled(true);
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut() {

        String text = txtView.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT : str = "Swiped Right";
                break;
            case SimpleGestureFilter.SWIPE_LEFT :  str = "Swiped Left";
                break;
            case SimpleGestureFilter.SWIPE_DOWN :  str = "Swiped Down";
                break;
            case SimpleGestureFilter.SWIPE_UP :    str = "Swiped Up";
                break;

        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        String strSpeaK="you have "+str;
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);

        if(str.equalsIgnoreCase("Swiped Right"))
        {
            //do the work functions. It will invoke when Swiped Right
            if(startBtn==true){
                startRecording();
            }

        }
        else if(str.equalsIgnoreCase("Swiped Left"))
        {
            //do the work functions. It will invoke when Swiped Left
            if(stopBtn==true){
                stopRecording();
            }
        }
        else if(str.equalsIgnoreCase("Swiped Up"))
        {
            //do the work functions. It will invoke when Swiped Up
            if(playBtn==true){
                play();
            }
        }
        else if(str.equalsIgnoreCase("Swiped Down"))
        {
            //do the work functions. It will invoke when Swiped Down
            if(stopPlayBtn==true){
                stopPlay();
            }
        }

    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "you have Double Tapped", Toast.LENGTH_SHORT).show();
        tts.speak("you have Double Tapped", TextToSpeech.QUEUE_FLUSH, null);
        speakOut();
    }


    private void startRecording() {
        try {
            myRecorder.prepare();
            myRecorder.start();
        } catch (IllegalStateException e) {
            // start:it is called before prepare()
            // prepare: it is called after start() or before setOutputFormat()
            e.printStackTrace();
        } catch (IOException e) {
            // prepare() fails
            e.printStackTrace();
        }

        text.setText("Recording Point: Recording");
        startBtn=false;
        stopBtn=true;

        Toast.makeText(getApplicationContext(), "Start recording...",
                Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        try {
            myRecorder.stop();
            myRecorder.release();
            myRecorder  = null;

            stopBtn=false;
            playBtn=true;
            text.setText("Recording Point: Stop recording");

            Toast.makeText(getApplicationContext(), "Stop recording...",
                    Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            //  it is called before start()
            e.printStackTrace();
        } catch (RuntimeException e) {
            // no valid audio/video data has been received
            e.printStackTrace();
        }
    }

    public void play() {
        try{
            myPlayer = new MediaPlayer();
            myPlayer.setDataSource(outputFile);
            myPlayer.prepare();
            myPlayer.start();

            playBtn=false;
            stopPlayBtn=true;
            text.setText("Recording Point: Playing");

            Toast.makeText(getApplicationContext(), "Start play the recording...",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        try {
            if (myPlayer != null) {
                myPlayer.stop();
                myPlayer.release();
                myPlayer = null;
                playBtn=true;
                stopPlayBtn=false;
                text.setText("Recording Point: Stop playing");

                Toast.makeText(getApplicationContext(), "Stop playing the recording...",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
