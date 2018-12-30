package com.practice.coding.boundserviceexample;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MusicPlayerService extends Service {

    private MediaPlayer mediaPlayer;
    private final Binder mBinder = new MyServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constants.TAG, "onCreate called : MusicPlayerService");

        mediaPlayer = MediaPlayer.create(this, R.raw.ring_tune);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                boolean isMusicCompleted = true;
                Intent intent = new Intent(Constants.MUSIC_COMPLETED);
                intent.putExtra(Constants.MESSAGE_KEY, isMusicCompleted);

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                //for started service when app close music still run and when it complete service destroy,
                stopSelf();
            }
        });
    }

     class MyServiceBinder extends Binder
    {
        //this method return an object of this MyServiceBinder class
        MusicPlayerService getService()
        {
            return MusicPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(Constants.TAG, "onStartCommand called.");

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(Constants.TAG, "onBind called.");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(Constants.TAG, "onUnbind called.");
        return true;
    }


    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(Constants.TAG, "onRebind called.");

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //when this method is called, the music play release
        mediaPlayer.release();

        Log.d(Constants.TAG, "onDestroy called.");
    }

    //client methods

    // Music Playing or not
        public boolean isPlaying()
        {
           return mediaPlayer.isPlaying();
        }

    // Play Music
    public void playMusic()
    {
        mediaPlayer.start();
    }

    //Pause Music
    public void pauseMusic()
    {
        mediaPlayer.pause();
    }

}
