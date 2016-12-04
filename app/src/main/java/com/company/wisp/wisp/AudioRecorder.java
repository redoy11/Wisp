package com.company.wisp.wisp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioRecorder extends AppCompatActivity implements TextToSpeech.OnInitListener
        , SimpleGestureFilter.SimpleGestureListener {

    private static int SPLASH_TIME_OUT = 3500;
    private SimpleGestureFilter detector;
    private TextToSpeech tts;
    //private Button btnSpeak;
    //private EditText txtText;
    private TextView txtView;
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private String outputFile = null;
    private TextView text;

    private boolean start_flag=true;
    private boolean stop_flag=false;
    private boolean play_flag=false;
    private boolean stop_play_flag=false;

    private String dialogue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        dialogue="Swipe Right to start recording";
        tts = new TextToSpeech(this, this);
        detector = new SimpleGestureFilter(this,this);
        txtView = (TextView) findViewById(R.id.RecordTextView);

        text = (TextView) findViewById(R.id.RecordTextView);

        // store it to sd card
        File outputDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/wisp/local_records");
        outputDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "WISP_" + timeStamp + ".3gpp";

        outputFile = Environment.getExternalStorageDirectory().
                getAbsolutePath() + "/wisp/local_records/" + fileName;

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
                dialogue="Swipe Right to stop recording.   Recording Started";
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut() {

        //String text = txtView.getText().toString();

        tts.speak(dialogue, TextToSpeech.QUEUE_FLUSH, null);
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
        //tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);

        if(str.equalsIgnoreCase("Swiped Right"))
        {
            //do the work functions. It will invoke when Swiped Right
            if(start_flag==true){
                tts.speak(dialogue, TextToSpeech.QUEUE_FLUSH, null);
                new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        startRecording();
                    }
                }, SPLASH_TIME_OUT);

            }
            else if(stop_flag==true){
                stopRecording();
                dialogue="Swipe Right to start playing";
                tts.speak(dialogue, TextToSpeech.QUEUE_FLUSH, null);

            }
            else if(play_flag==true){
                dialogue="Swipe Right to stop playing.   Started playing";
                tts.speak(dialogue, TextToSpeech.QUEUE_FLUSH, null);
                new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        play();
                    }
                }, SPLASH_TIME_OUT);

            }
            else if(stop_play_flag==true){
                stopPlay();
                dialogue="Swipe Right to start playing again";
                tts.speak(dialogue, TextToSpeech.QUEUE_FLUSH, null);
            }

        }
        else if(str.equalsIgnoreCase("Swiped Left"))
        {
            //do the work functions. It will invoke when Swiped Left

        }
        else if(str.equalsIgnoreCase("Swiped Up"))
        {
            //do the work functions. It will invoke when Swiped Up

        }
        else if(str.equalsIgnoreCase("Swiped Down"))
        {
            //do the work functions. It will invoke when Swiped Down

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
        start_flag=false;
        stop_flag=true;

        Toast.makeText(getApplicationContext(), "Start recording...",
                Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        try {
            myRecorder.stop();
            myRecorder.release();
            myRecorder  = null;

            stop_flag=false;
            play_flag=true;
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

            play_flag=false;
            stop_play_flag=true;
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
                play_flag=true;
                stop_play_flag=false;
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
