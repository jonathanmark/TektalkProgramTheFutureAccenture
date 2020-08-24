package com.tektalk.finalcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.tektalk.finalcode.R;

public class MainActivity extends AppCompatActivity {
    private Button learnBtn;
    private Button bookmarkBtn;
    private Button gameBtn;

    private ImageView tektalkLogo;
    private ImageView splashScreen;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameBtn = (Button) findViewById(R.id.btnGame);
        gameBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openGameAct();
            }
        });

        learnBtn = (Button) findViewById(R.id.button);
        learnBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openDetectorAct();
            }
        });

        bookmarkBtn = (Button) findViewById(R.id.buttonBookmark);
        bookmarkBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openBookmarkAct();
            }
        });

        tektalkLogo = (ImageView) findViewById(R.id.imageView3);
        splashScreen = (ImageView) findViewById(R.id.imageView5);

        tektalkLogo.postDelayed(new Runnable() {
            @Override
            public void run() {
                splashScreen.setVisibility(View.INVISIBLE);
                tektalkLogo.setVisibility(View.VISIBLE);
                tektalkLogo.animate().setDuration(500).translationYBy(-500).scaleX(0.5F).scaleY(0.5F).setDuration(2000);
            }
        }, 1000);

        gameBtn.postDelayed(new Runnable() {
            public void run() {
                gameBtn.setVisibility(View.VISIBLE);
            }
        }, 3000);

        bookmarkBtn.postDelayed(new Runnable() {
            public void run() {
                bookmarkBtn.setVisibility(View.VISIBLE);
            }
        }, 3000);


        learnBtn.postDelayed(new Runnable() {
            public void run() {
                learnBtn.setVisibility(View.VISIBLE);
            }
        }, 3000);
    }

    private void openDetectorAct() {
        Intent open = new Intent(this, ChooseLanguage.class);
        startActivity(open);
    }
    private void openBookmarkAct() {
        Intent opens = new Intent(this, Bookmark.class);
        startActivity(opens);
    }
    private void openGameAct() {
        Intent openss = new Intent(this, GameActivity.class);
        startActivity(openss);
    }

}