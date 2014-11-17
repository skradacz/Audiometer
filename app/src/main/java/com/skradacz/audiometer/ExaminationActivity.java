package com.skradacz.audiometer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


public class ExaminationActivity extends Activity {

    private TextView textview2;
    private TextView textview3;
    private TextView textview4;
    private TextView textview5;
    private TextView textview6;
    private TextView textview7;

    private AudioManager audioManager;
    private ToneGen toneGen;
    private final Handler handler = new Handler();
    private final StringBuilder stringBuilder = new StringBuilder();

    private double frequency = 250;                  // toneGen frequency
    private final int duration = 6;                  // toneGen duration in seconds
    private double amplitude = 1.0f;                 // toneGen amplitude

    private int mode = 0;
    private boolean stop = false;
    private String result;
    private boolean rightEar = false;
    private boolean leftEar = false;
//    public double freqChecker = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TextView clickHere = (TextView) findViewById(R.id.start_text_view);
        textview2 = (TextView) findViewById(R.id.textView2);
        textview3 = (TextView) findViewById(R.id.textView3);
        textview4 = (TextView) findViewById(R.id.textView4);
        textview5 = (TextView) findViewById(R.id.textView5);
        textview6 = (TextView) findViewById(R.id.textView6);
        textview7 = (TextView) findViewById(R.id.testInfoTextView);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        Global global = ((Global)getApplicationContext());
        if (global.getTestChecked()) {
            textview2.setVisibility(View.VISIBLE);
            textview3.setVisibility(View.VISIBLE);
            textview4.setVisibility(View.VISIBLE);
            textview5.setVisibility(View.VISIBLE);
            textview6.setVisibility(View.VISIBLE);
            textview7.setVisibility(View.VISIBLE);
        }else {
            textview2.setVisibility(View.INVISIBLE);
            textview3.setVisibility(View.INVISIBLE);
            textview4.setVisibility(View.INVISIBLE);
            textview5.setVisibility(View.INVISIBLE);
            textview6.setVisibility(View.INVISIBLE);
            textview7.setVisibility(View.INVISIBLE);
        }

        textview2.setText("toneGen.volume: " + String.valueOf(mode/10f));
        textview3.setText("Amplitude: " + String.valueOf(amplitude));
        textview4.setText("StreamVolume: " + String.valueOf(audioManager.getStreamVolume(
                AudioManager.STREAM_MUSIC)));
        textview5.setText("mode: " + String.valueOf(mode-1));
        textview6.setText("frequency: " + String.valueOf(frequency));
        textview7.setText("left ear: " + String.valueOf(leftEar) + " right ear: "
                + String.valueOf(rightEar));

        clickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //if (freqChecker != frequency) {
                //'if' makes sure frequency changed before button was clicked
                if (!rightEar && leftEar) {
                    stringBuilder.append("Left ear: For freq " + frequency + " mode is "
                            + (mode - 1) + "\n");
                } else if (rightEar && !leftEar) {
                    stringBuilder.append("Right ear: For freq " + frequency + " mode is "
                            + (mode - 1) + "\n");
                }
                mode = 11;
                if (frequency == 8000 && rightEar) {
                    result = stringBuilder.toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ExaminationActivity.this);
                    builder.setTitle("RESULT");
                    builder.setMessage(result);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                toneGen.stop();
                //freqChecker = frequency;
                //}
            }
        });


        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);

        toneGen = new ToneGen(frequency, duration, amplitude);
        toneGen.play();

        VolumeUp();
    }

    private void VolumeUp(){
        // gets volumeChanger running every 'delay' seconds
        int delay = 1000 * 4;
        handler.postDelayed(volumeChanger, delay);
    }

    private final Runnable volumeChanger = new Runnable() {
        @Override
        public void run() {

            if (!stop){

                VolumeUp();

                if (!rightEar && !leftEar) {
                    leftEar = true;
                    frequency = 250;
                }

                if (mode == 11) {
                    mode = 0;
                    if (frequency == 0){
                        frequency = 250;
                    }else if (frequency == 250) {
                        frequency = 500;
                    }else if (frequency == 500) {
                        frequency = 1000;
                    }else if (frequency == 1000) {
                        frequency = 2000;
                    }else if (frequency == 2000) {
                        frequency = 4000;
                    }else if (frequency == 4000) {
                        frequency = 8000;
                    }else if (frequency == 8000) {
                        if (!rightEar && leftEar) {
                            rightEar = true;
                            leftEar = false;
                            frequency = 250;
                        }else if (rightEar && !leftEar){
                            leftEar = true;
                        }
                        if (rightEar && leftEar) {
                            stop = true;
                        }
                    }
                }

                if (!stop){

                    if (mode == 0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                        amplitude = 1.0;
                    }else if (mode == 1) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                        amplitude = 1.2;
                    }else if (mode == 2) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                        amplitude = 1.2;
                    }else if (mode > 2 && mode < 9) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                        amplitude = (Math.pow(2, mode-2));
                    }else if (mode == 9){
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 3, 0);
                        amplitude = 128;
                    }else if (mode == 10){
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 6, 0);
                        amplitude = 128;
                    }

                    toneGen.stop();
                    toneGen = new ToneGen(frequency, duration, amplitude);
                    toneGen.play();
                    if (rightEar) {
                        toneGen.volume(0.0f, mode/10f);
                    }else if (leftEar) {
                        toneGen.volume(mode/10f, 0.0f);
                    }
                    mode++;

                    textview2.setText("toneGen.volume: " + String.valueOf(mode/10f));
                    textview3.setText("Amplitude: " + String.valueOf(amplitude));
                    textview4.setText("StreamVolume: " + String.valueOf(audioManager
                            .getStreamVolume(AudioManager.STREAM_MUSIC)));
                    textview5.setText("Mode: " + String.valueOf(mode-1));
                    textview6.setText("frequency: " + String.valueOf(frequency));
                    textview7.setText("left ear: " + String.valueOf(leftEar) + " right ear: "
                            + String.valueOf(rightEar));

                }else {
                    toneGen.stop();
                }
            }

        }
    };

    @Override
    public void onPause(){
        super.onPause();
        toneGen.stop();
        stop = true;
    }

    @Override
    public void onResume(){
        super.onResume();
        stop = false;
    }

}