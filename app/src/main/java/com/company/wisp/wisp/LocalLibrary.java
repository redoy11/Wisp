package com.company.wisp.wisp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LocalLibrary extends AppCompatActivity implements TextToSpeech.OnInitListener
        , SimpleGestureFilter.SimpleGestureListener {

    private SimpleGestureFilter detector;
    private TextToSpeech tts;
    private Button btnSpeak;
    private EditText txtText;
    private TextView txtView;

    private String path;

    private MediaPlayer myPlayer;
    private boolean play_flag=false;
    private boolean stop_play_flag=false;

    private  String runningFile=null;
    private List fileNames;
    private int curPosition;
    private TextView txtViewFileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_library);

        tts = new TextToSpeech(this, this);

        txtView = (TextView) findViewById(R.id.localLibraryTextView);
        detector = new SimpleGestureFilter(this,this);

        txtViewFileName = (TextView) findViewById(R.id.localLibraryFileName);
        curPosition = 0;

        path = "/";
        if (getIntent().hasExtra("path")) {
            path = getIntent().getStringExtra("path");
        }

        // Read all files sorted into the values-array
        fileNames = new ArrayList();
        File dir = new File(path);
        /*if (!dir.canRead()) {
            setTitle(getTitle() + " (inaccessible)");
        }*/
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    fileNames.add(file);
                }
            }
        }
        Collections.sort(fileNames);

        // Put the data into the list
        //ArrayAdapter adapter = new ArrayAdapter(this,
        //        android.R.layout.simple_list_item_2, android.R.id.text1, values);
        //setListAdapter(adapter);
        showCurrentFile();
    }

    public void showCurrentFile() {
        if(fileNames.size() == 0) {
            speakOut();
            return;
        }
        if(curPosition >= fileNames.size()) curPosition = 0;
        else if(curPosition < 0) curPosition = fileNames.size() - 1;
        txtViewFileName.setText((CharSequence) fileNames.get(curPosition).toString());
        speakOutCurFile();
    }

    /*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String filename = (String) getListAdapter().getItem(position);
        if (path.endsWith(File.separator)) {
            filename = path + filename;
        } else {
            filename = path + File.separator + filename;
        }
        if (new File(filename).isDirectory()) {
            Intent intent = new Intent(this, LocalLibrary.class);
            intent.putExtra("path", filename);
            startActivity(intent);
        } else {
            //Toast.makeText(this, filename + " is not a directory", Toast.LENGTH_LONG).show();
            if(filename.equalsIgnoreCase(runningFile)) {
                stopPlay();
            } else {
                if (stop_play_flag) stopPlay();
                play(filename);
            }
        }
    }
*/
    public void play(String selectedFile) {
        try{
            myPlayer = new MediaPlayer();
            myPlayer.setDataSource(selectedFile);
            myPlayer.prepare();
            myPlayer.start();

            play_flag=false;
            stop_play_flag=true;
            runningFile = selectedFile;
            //text.setText("Recording Point: Playing");

            Toast.makeText(getApplicationContext(), "Start play the recording...",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
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
                runningFile = null;
                //text.setText("Recording Point: Stop playing");

                Toast.makeText(getApplicationContext(), "Stop playing the recording...",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private void speakOutCurFile() {
        String text = txtViewFileName.getText().toString();
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
        tts.speak("you have " + str, TextToSpeech.QUEUE_FLUSH, null);

        String strSpeaK="you have "+str;
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);

        if(str.equalsIgnoreCase("Swiped Right"))
        {
            if(stop_play_flag) stopPlay();
            curPosition++;
            showCurrentFile();
        }
        else if(str.equalsIgnoreCase("Swiped Left"))
        {
            if(stop_play_flag) stopPlay();
            curPosition--;
            showCurrentFile();
        }
        else if(str.equalsIgnoreCase("Swiped Up"))
        {
            if(stop_play_flag) stopPlay();
        }
        else if(str.equalsIgnoreCase("Swiped Down"))
        {
            String filename = path + "/" + fileNames.get(curPosition).toString();

            if(filename.equalsIgnoreCase(runningFile)) {
                stopPlay();
            } else {
                if (stop_play_flag) stopPlay();
                play(filename);
            }
        }
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "you have Double Tapped", Toast.LENGTH_SHORT).show();
        tts.speak("you have Double Tapped", TextToSpeech.QUEUE_FLUSH, null);
        speakOut();
    }
}

