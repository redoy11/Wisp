package com.company.wisp.wisp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.wisp.wisp.SimpleGestureFilter.*;

import java.util.Locale;

public class MenuActivity extends AppCompatActivity implements TextToSpeech.OnInitListener
        , SimpleGestureListener {

    private SimpleGestureFilter detector;
    private TextToSpeech tts;
    private Button btnSpeak;
    private EditText txtText;
    private TextView txtView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tts = new TextToSpeech(this, this);

        btnSpeak = (Button) findViewById(R.id.btnSpeak);

        txtText = (EditText) findViewById(R.id.txtText);

        txtView = (TextView) findViewById(R.id.testBox);

        // button on click event
        /*
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                speakOut();
            }

        });
        */
        detector = new SimpleGestureFilter(this,this);
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
            Intent i = new Intent(MenuActivity.this, LocalLibrary.class);
            startActivity(i);
        }
        else if(str.equalsIgnoreCase("Swiped Left"))
        {
            Intent i = new Intent(MenuActivity.this, GlobalLibrary.class);
            startActivity(i);
        }
        else if(str.equalsIgnoreCase("Swiped Up"))
        {
            Intent i = new Intent(MenuActivity.this, SearchLocal.class);
            startActivity(i);
        }
        else if(str.equalsIgnoreCase("Swiped Down"))
        {
            Intent i = new Intent(MenuActivity.this, SpeakFileName.class);
            startActivity(i);
        }

    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "you have Double Tapped", Toast.LENGTH_SHORT).show();
        tts.speak("you have Double Tapped", TextToSpeech.QUEUE_FLUSH, null);
        speakOut();
    }
}
