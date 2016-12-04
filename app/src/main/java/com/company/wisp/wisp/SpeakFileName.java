package com.company.wisp.wisp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class SpeakFileName extends AppCompatActivity implements TextToSpeech.OnInitListener
        , SimpleGestureFilter.SimpleGestureListener  {

    private static int SPLASH_TIME_OUT = 2000;
    private SimpleGestureFilter detector;
    private TextToSpeech tts;
    private final int SPEECH_RECOGNITION_CODE = 1234;
    private TextView txtOutput;
    private ImageButton btnMicrophone;
    private String filename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak_file_name);

        tts = new TextToSpeech(this, this);
        detector = new SimpleGestureFilter(this,this);
        //txtView = (TextView) findViewById(R.id.RecordTextView);

        txtOutput = (TextView) findViewById(R.id.txt_output);
        btnMicrophone = (ImageButton) findViewById(R.id.btn_mic);

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

        filename = txtOutput.getText().toString();

        tts.speak(filename, TextToSpeech.QUEUE_FLUSH, null);
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
        tts.speak(strSpeaK, TextToSpeech.QUEUE_FLUSH, null);

        if(str.equalsIgnoreCase("Swiped Right"))
        {
            //do the work functions. It will invoke when Swiped Right
            //startSpeechToText();
            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    startSpeechToText();
                }
            }, SPLASH_TIME_OUT);

        }
        else if(str.equalsIgnoreCase("Swiped Left"))
        {
            //do the work functions. It will invoke when Swiped Left
            if(!filename.isEmpty()){
                Intent i = new Intent(SpeakFileName.this, AudioRecorder.class);
                i.putExtra("message", filename);
                startActivity(i);
            }
            else{
                tts.speak("Speak out a valid file name", TextToSpeech.QUEUE_ADD, null);
            }
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

    /**
     * Start speech to text intent. This opens up Google Speech Recognition API dialog box to listen the speech input.
     * */
    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();

        }
    }
    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    txtOutput.setText(text);
                    speakOut();
                }
                break;

            }
        }
    }
}