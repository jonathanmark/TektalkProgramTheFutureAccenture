package com.tektalk.finalcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Leaderboards extends AppCompatActivity {

    private Button toMainMenu, toBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);

        toMainMenu = (Button) findViewById(R.id.button3);
        toMainMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mainMen();
            }
        });
        TextView cS = (TextView) findViewById(R.id.currentScore);

        toBack = (Button) findViewById(R.id.backButtonResult);

        int score = getIntent().getIntExtra("SCORES", 0);
        cS.setText(score + "");

        toBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GameResult.class));
            }
        });


    }


    private void mainMen() {
        Intent opens = new Intent(this, MainActivity.class);
        startActivity(opens);
    }
}