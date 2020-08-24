package com.tektalk.finalcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        TextView scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        TextView highScoreLabel = (TextView) findViewById(R.id.highScoreLabel);
        Button leaderBoard = (Button) findViewById(R.id.button4);

        int score = getIntent().getIntExtra("SCORE", 0);
        scoreLabel.setText(score + "");

        SharedPreferences settings = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE);

        final int highScore = settings.getInt("HIGH_SCORE", 0);



        if(score > highScore) {
            highScoreLabel.setText("High Score: " + score);

            //UPDATE HIGH SCORE
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", score);
            editor.commit();
        } else {
            highScoreLabel.setText("High Score: " + highScore);
        }

        leaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Leaderboards.class);
                intent.putExtra("SCORES", highScore);
                startActivity(intent);
            }
        });

    }

    public void tryAgain(View view){
        startActivity(new Intent(getApplicationContext(), GameActivity.class));
    }

    public void mainMen(View view){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


}