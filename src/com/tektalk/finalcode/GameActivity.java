

package com.tektalk.finalcode;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tektalk.finalcode.env.BorderedText;
import com.tektalk.finalcode.env.ImageUtils;
import com.tektalk.finalcode.env.Logger;
import com.tektalk.finalcode.tracking.MultiBoxTracker;
import com.tektalk.finalcode.tracking.MultiBoxTrackerGame;

import com.tektalk.finalcode.OverlayView.DrawCallback;
import com.tektalk.finalcode.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class GameActivity extends CameraActivityGame implements OnImageAvailableListener {

    public TextView translatedTv;
    public static String translatedText;
    private static final Logger LOGGER = new Logger();
    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final String TF_OD_API_MODEL_FILE =
            "file:///android_asset/ssd_mobilenet_v1_android_export.pb";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt"; ///////////////LABELLLLL
    private enum DetectorMode {TF_OD_API;}
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.6f;
    private static final float MINIMUM_CONFIDENCE_MULTIBOX = 0.1f;
    private static final boolean MAINTAIN_ASPECT = MODE == DetectorMode.TF_OD_API;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    private Integer sensorOrientation;
    private Classifier detector;
    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;
    private boolean computingDetection = false;
    private long timestamp = 0;
    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private MultiBoxTrackerGame trackerGame;
    private byte[] luminanceCopy;
    public static boolean startCapture = false;
    public static boolean startStart = false;
    public static String Namestringsss;
    public static String chosenLanguagee;
    public static String apiLanguagee;
    public static String random;
    public static List<String> Wordlist;
    final Random randomGenerator = new Random();
    public boolean changeLanguage = false;
    public int score = 0;
    private BorderedText borderedText;
    private long timeLeftinMillisecond = 60000; // 60 seconds
    private boolean timerRunning;
    private CountDownTimer countDownTimer;
    private int countWord=0;
    public static String stringWithCut;
    public static String stringWithCutLang;
    private SoundPlayer sound;

    Translate translate;
    DatabaseReference databaseWord;
    Button btConvert;
    TextToSpeech textToSpeech;


    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {

        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        trackerGame = new MultiBoxTrackerGame(this);

        int cropSize = TF_OD_API_INPUT_SIZE;
        if (MODE == DetectorMode.TF_OD_API) {
            try {
                detector = TensorFlowObjectDetectionAPIModel.create(
                        getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE);
                cropSize = TF_OD_API_INPUT_SIZE; //CLASSIFICATION
            } catch (final IOException e) {
                LOGGER.e(e, "Exception initializing classifier!");
                Toast toast =
                        Toast.makeText(
                                getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        trackerGame.draw(canvas);
                        if (isDebug()) {
                            trackerGame.drawDebug(canvas);
                        }
                    }
                });

        addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        if (!isDebug()) {
                            return;
                        }
                        final Bitmap copy = cropCopyBitmap;
                        if (copy == null) {
                            return;
                        }

                        final int backgroundColor = Color.argb(100, 0, 0, 0);
                        canvas.drawColor(backgroundColor);

                        final Matrix matrix = new Matrix();
                        final float scaleFactor = 2;
                        matrix.postScale(scaleFactor, scaleFactor);
                        matrix.postTranslate(
                                canvas.getWidth() - copy.getWidth() * scaleFactor,
                                canvas.getHeight() - copy.getHeight() * scaleFactor);
                        canvas.drawBitmap(copy, matrix, new Paint());

                    }
                });
    }

    public void Capture(View Button){

        startTimer();
        sound.playBGMusic();

        if(!startCapture){
            startCapture = true;
        } else {
            startCapture = false;
        }
        Button.setVisibility(View.INVISIBLE);

    }

    OverlayView trackingOverlay;

    @Override
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        byte[] originalLuminance = getLuminance();


        trackerGame.onFrame(
                previewWidth,
                previewHeight,
                getLuminanceStride(),
                sensorOrientation,
                originalLuminance,
                timestamp);
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        if (luminanceCopy == null) {
            luminanceCopy = new byte[originalLuminance.length];
        }
        System.arraycopy(originalLuminance, 0, luminanceCopy, 0, originalLuminance.length);
        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }


        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {

                        LOGGER.i("Running detection on image " + currTimestamp);

                        final long startTime = SystemClock.uptimeMillis();
                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);

                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap);
                        final Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStyle(Style.STROKE);
                        paint.setStrokeWidth(2.0f);

                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                        switch (MODE) {
                            case TF_OD_API:
                                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                break;

                        }

                        final List<Classifier.Recognition> mappedRecognitions =
                                new LinkedList<Classifier.Recognition>();

                        for (final Classifier.Recognition result : results) {
                            final RectF location = result.getLocation();
                            if (location != null && result.getConfidence() >= minimumConfidence) {
                                canvas.drawRect(location, paint);

                                cropToFrameTransform.mapRect(location);
                                result.setLocation(location);
                                if(startCapture == true) {
                                    String s1 = TensorFlowObjectDetectionAPIModel.getC1();
                                    Namestringsss = s1;
                                    mappedRecognitions.add(result);
                                }

                            }

                        }

                        trackerGame.trackResults(mappedRecognitions, luminanceCopy, currTimestamp);

                        trackingOverlay.postInvalidate();


                        requestRender();
                        computingDetection = false;
                    }
                });

    }



    protected void getRandom() {
        Wordlist = new ArrayList<>();
        databaseWord = FirebaseDatabase.getInstance().getReference("SavedWord");
        final Button btnSub = (Button) findViewById(R.id.btnSubmit);
        final TextView tV = (TextView) findViewById(R.id.gameRandom);
        final String f1 = TensorFlowObjectDetectionAPIModel.getC1();
        final TextView sT = (TextView)  findViewById(R.id.scoring);
        sound = new SoundPlayer(this);


        sT.setText("Score: " + score);

        databaseWord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    for (DataSnapshot wordSnapshot : snapshot.getChildren()) {
                        String wS = wordSnapshot.child("wordLanguage").getValue(String.class);
                        Wordlist.add(wS);
                    }

                    random = Wordlist.get(randomGenerator.nextInt(Wordlist.size()));
                    stringWithCut = random.substring(0, random.length() - 2);
                    stringWithCutLang = random.substring(random.length() - 2);

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try (InputStream is = getResources().openRawResource(R.raw.credentials)) {

                        //Get credentials:
                        final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

                        //Set credentials and get translate service:
                        TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
                        translate = translateOptions.getService();

                    } catch (IOException ioe) {
                        ioe.printStackTrace();

                    }

                    Translation translation = translate.translate(stringWithCut, Translate.TranslateOption.targetLanguage(stringWithCutLang), Translate.TranslateOption.model("base"));
                    translatedText = translation.getTranslatedText();

                    tV.setText(translatedText);

                    btnSub.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (stringWithCut.equalsIgnoreCase(Namestringsss)) {
                                sound.playHitSound();
                                changeLanguage = true;
                                score++;
                                sT.setText("Score: " + score);

                                trackerGame.getScreenRects().clear();
                                trackerGame.getObjectTracker().clearInstance();
                                trackerGame.getTrackedObjects().clear();


                                final Toast myToast = Toast.makeText(GameActivity.this, "CORRECT!", Toast.LENGTH_LONG);
                                myToast.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        myToast.cancel();
                                    }
                                }, 1000);

                            } else {
                                sound.playHitWrong();
                                final Toast myToasts = Toast.makeText(GameActivity.this, "WRONG! Find other object", Toast.LENGTH_LONG);
                                myToasts.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        myToasts.cancel();
                                    }
                                }, 1000);
                                score = 0;
                                sT.setText("Score: " + score);
                            }

                            if(changeLanguage == true) {
                                random = Wordlist.get(randomGenerator.nextInt(Wordlist.size()));
                                stringWithCut = random.substring(0, random.length() - 2);
                                stringWithCutLang = random.substring(random.length() - 2);

                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);

                                try (InputStream is = getResources().openRawResource(R.raw.credentials)) {

                                    //Get credentials:
                                    final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

                                    //Set credentials and get translate service:
                                    TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
                                    translate = translateOptions.getService();

                                } catch (IOException ioe) {
                                    ioe.printStackTrace();

                                }

                                Translation translation = translate.translate(stringWithCut, Translate.TranslateOption.targetLanguage(stringWithCutLang), Translate.TranslateOption.model("base"));
                                translatedText = translation.getTranslatedText();

                                tV.setText(translatedText);
                            }
                        }
                    });

                } else {
                    Toast.makeText(GameActivity.this, "Please save a word to your bookmark!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void startTimer()
    {
        countDownTimer = new CountDownTimer(timeLeftinMillisecond, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftinMillisecond = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                sound.playTada();
                sound.stopPlayer();
                Intent intent = new Intent(getApplicationContext(), GameResult.class);
                intent.putExtra("SCORE", score);
                startActivity(intent);

                // Toast.makeText(GameActivity.this, "TAPOS NA", Toast.LENGTH_LONG).show();
            }
        }.start();

        timerRunning = true;
    }

    public void stopTimer()
    {
        countDownTimer.cancel();
    }

    public void updateTimer(){
        final TextView countdownText = (TextView) findViewById((R.id.txtViewTimer));
        int minute = (int) timeLeftinMillisecond/60000;
        int seconds = (int) timeLeftinMillisecond % 60000/1000;
        String timeLeftText;
        timeLeftText = "" + minute;
        timeLeftText += ":";
        if (seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;
        countdownText.setText(timeLeftText);
    }

    protected void GameActivity(){
        sound.stopPlayer();
    }

    protected void stopTime(){
        stopTimer();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    public void onSetDebug(final boolean debug) {
        detector.enableStatLogging(debug);
    }
}
