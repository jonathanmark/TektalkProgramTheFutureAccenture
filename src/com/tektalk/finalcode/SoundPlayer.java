package com.tektalk.finalcode;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.MediaPlayer;

public class SoundPlayer {
    private static SoundPool soundPool;
    private static MediaPlayer player;
    private static int hitCorrect;
    private static int overSound;
    private static int tada;
    private Context context;

    public SoundPlayer(Context context){
        this.context = context;
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);



        hitCorrect = soundPool.load(context, R.raw.horay, 1);
        overSound = soundPool.load(context, R.raw.wrong, 1);
        tada = soundPool.load(context, R.raw.tada, 1);


    }

    public void playHitSound () {
        soundPool.play(hitCorrect, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitWrong()
    {
        soundPool.play(overSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playTada(){
        soundPool.play(tada, 1.0f, 1.0f, 1, 0, 1.0f);
    }



    public void playBGMusic(){
        player = MediaPlayer.create(context, R.raw.amok);
        player.start();
    }

    public void stopPlayer()
    {
        player.release();
    }

}
