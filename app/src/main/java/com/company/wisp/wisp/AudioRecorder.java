package com.company.wisp.wisp;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
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

import java.util.Locale;

public class AudioRecorder extends AppCompatActivity implements TextToSpeech.OnInitListener
        , SimpleGestureFilter.SimpleGestureListener {

    private SimpleGestureFilter detector;
    private TextToSpeech tts;
    //private Button btnSpeak;
    //private EditText txtText;
    private TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        tts = new TextToSpeech(this, this);
        detector = new SimpleGestureFilter(this,this);
        txtView = (TextView) findViewById(R.id.RecordTextView);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audio_recorder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
