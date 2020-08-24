package com.tektalk.finalcode;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tektalk.finalcode.env.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpeechActivity extends AppCompatActivity {
    private static final Logger LOGGER = new Logger();
    public TextView txvResult, speechCaptured, talkbackScore;
    public TextView stringfromDetector;
    public String finalLetter, fromDetector;
    public Button btnMenu, btnLearnAgain, btConvert;
    TextToSpeech textToSpeech, tS;
    private SoundPlayer sound;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_activity);
        txvResult = (TextView) findViewById(R.id.txvResult);
        speechCaptured = (TextView) findViewById(R.id.speechCaptured);
        talkbackScore = (TextView) findViewById(R.id.talkbackScore);
        stringfromDetector = (TextView) findViewById(R.id.passResultFromDetector);
        btnMenu = (Button) findViewById(R.id.btnMainMenuTalkBack);
        btnLearnAgain = (Button) findViewById(R.id.btnLearnAgain);


        speechCaptured.setVisibility(View.INVISIBLE);
        talkbackScore.setVisibility(View.INVISIBLE);

        final String fromDetector = getIntent().getStringExtra("NAME");
        stringfromDetector.setText(fromDetector);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        btnLearnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DetectorActivity.class));
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = textToSpeech.setLanguage(Locale.KOREAN);
                }

            }
        });

        tS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = tS.setLanguage(Locale.CANADA);
                }

            }
        });

        btConvert = findViewById(R.id.custom_button);
        btConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = TensorFlowObjectDetectionAPIModel.getC1();
                int speech = textToSpeech.speak(fromDetector, TextToSpeech.QUEUE_FLUSH, null);

            }
        });

    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko_");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the Captured Word");
        intent.putExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES, true);



        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 100);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Toast myToast;
        Handler handler = new Handler();
        sound = new SoundPlayer(SpeechActivity.this);

        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String confidenceLevel = RecognizerIntent.EXTRA_CONFIDENCE_SCORES;
                    float [] tektalkbackScore = data.getFloatArrayExtra(confidenceLevel);

                    for(int i=0; i<result.size(); i++){
                        if(tektalkbackScore != null){
                            if(tektalkbackScore.length>0){
                                LOGGER.i("tektalkbackscore" + String.valueOf(tektalkbackScore[0]));
                            }
                            else{
                                LOGGER.i("score not availabe");
                            }
                        }else{
                            LOGGER.i("WALANG KWENTA");
                        }
                    }


                    txvResult.setText(result.get(0));
                    finalLetter = result.get(0);

                    String fromDetector = getIntent().getStringExtra("NAME");

                    TextView scoreSpeech = (TextView) findViewById(R.id.resultScoring);


                    talkbackScore.setVisibility(View.VISIBLE);
                    speechCaptured.setVisibility(View.VISIBLE);

                    if(finalLetter != null && !finalLetter.isEmpty()){
                        int wordCapture = fromDetector.length();
                        int speechCapture = finalLetter.length();
                        int max = wordCapture;
                        int min = speechCapture;
                        int results = 0;
                        if(wordCapture < speechCapture){
                            max = speechCapture;
                            min = wordCapture;
                        }
                        for (int index = 0; index < min; index++){
                            if(fromDetector.charAt(index) != finalLetter.charAt(index)){
                                results++;
                            }
                        }
                        Double gR = (((double)(max) - (double) (results))/(double) (max)) *100;
                        Double gG = Double.parseDouble(new Float(tektalkbackScore[0]).toString());
                        Double finalScore = gR * gG;


                        LOGGER.i("mema" + gR);
                        LOGGER.i("mema1" + finalScore);

                        scoreSpeech.setText(String.format("%.2f", finalScore));

                        // 0-10, 11-25, 26-40, 41-60, 61-75, 76-90, 90-96, 100
                        if(0 >= finalScore && finalScore <= 10){
                            int speech = tS.speak("Say it Again, practice more", TextToSpeech.QUEUE_FLUSH, null);
                            myToast = Toast.makeText(SpeechActivity.this, "Say it Again, practice more", Toast.LENGTH_LONG);
                            myToast.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myToast.cancel();
                                }
                            }, 3000);
                        } else if (11 >= finalScore && finalScore <= 25){
                            int speech = tS.speak("You need to practice slight, speak more", TextToSpeech.QUEUE_FLUSH, null);
                            myToast = Toast.makeText(SpeechActivity.this, "You need to practice slight, speak more", Toast.LENGTH_LONG);
                            myToast.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myToast.cancel();
                                }
                            }, 3000);
                        } else if (26 >= finalScore && finalScore <= 40) {
                            int speech = tS.speak("You're almost there, keep going", TextToSpeech.QUEUE_FLUSH, null);
                            myToast = Toast.makeText(SpeechActivity.this, "You're almost there, keep going", Toast.LENGTH_LONG);
                            myToast.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myToast.cancel();
                                }
                            }, 3000);
                        } else if (41 >= finalScore && finalScore <= 60) {
                            int speech = tS.speak("Keep practicing, you're going to achieve it", TextToSpeech.QUEUE_FLUSH, null);
                            myToast = Toast.makeText(SpeechActivity.this, "Keep practicing, you're going to achieve it", Toast.LENGTH_LONG);
                            myToast.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myToast.cancel();
                                }
                            }, 3000);
                        } else if (61 >= finalScore && finalScore <= 75) {
                            int speech = tS.speak("You're doing great!", TextToSpeech.QUEUE_FLUSH, null);
                            myToast = Toast.makeText(SpeechActivity.this, "You're doing great!", Toast.LENGTH_LONG);
                            myToast.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myToast.cancel();
                                }
                            }, 3000);
                        } else if (76 >= finalScore && finalScore <= 90) {
                            int speech = tS.speak("WOW! Good Job Tektalkers", TextToSpeech.QUEUE_FLUSH, null);
                            myToast = Toast.makeText(SpeechActivity.this, "WOW! Good Job Tektalkers", Toast.LENGTH_LONG);
                            myToast.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myToast.cancel();
                                }
                            }, 3000);
                        } else if (91 >= finalScore && finalScore <= 99) {
                            int speech = tS.speak("VIOLA! Tektalker is on fire. Very Good!", TextToSpeech.QUEUE_FLUSH, null);
                            myToast = Toast.makeText(SpeechActivity.this, "VIOLA! Tektalker is on fire. Very Good!", Toast.LENGTH_LONG);
                            myToast.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myToast.cancel();
                                }
                            }, 3000);
                        } else if (finalScore == 100) {
                            int speech = tS.speak("Offical Tektalker", TextToSpeech.QUEUE_FLUSH, null);
                            myToast = Toast.makeText(SpeechActivity.this, "PERFECTEKTALKBACK", Toast.LENGTH_LONG);
                            myToast.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myToast.cancel();
                                }
                            }, 3000);
                        }
                    }
                }
                break;
        }
    }



}