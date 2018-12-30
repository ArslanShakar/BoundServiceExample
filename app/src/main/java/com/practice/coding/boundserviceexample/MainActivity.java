package com.practice.coding.boundserviceexample;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isMusicCompleted = intent.getBooleanExtra(Constants.MESSAGE_KEY, false);
            if(isMusicCompleted)
            {
                btPlayMusic.setText("Play");
            }

            Log.d(Constants.TAG, "onReceive called.");
        }
    };

    private Button btPlayMusic;
    private MusicPlayerService musicPlayerService;
    private boolean mBound;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            MusicPlayerService.MyServiceBinder serviceBinder = (MusicPlayerService.MyServiceBinder) iBinder;
            musicPlayerService = serviceBinder.getService();
            mBound = true;
            Log.d(Constants.TAG, "Service Connected!");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Log.d(Constants.TAG, "Service Disconnected!");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btPlayMusic = findViewById(R.id.btPlayMusic);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.d(Constants.TAG, "onStart called");

        //use both bound and started services now this intent is recieved in onStart Command.
        startService(intent);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.MUSIC_COMPLETED));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mBound)
        {
            unbindService(serviceConnection);
            mBound = false;
            Log.d(Constants.TAG, "onStop called");
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    public void playMusic(View view) {
        if(mBound)
        {
            if(musicPlayerService.isPlaying())
            {
                musicPlayerService.pauseMusic();
                btPlayMusic.setText("Pause");

                Log.d(Constants.TAG, "Music Pause. ");
            }else
            {
             musicPlayerService.playMusic();
             btPlayMusic.setText("Playing");

                Log.d(Constants.TAG, "Music Playing. . .");
            }
        }
    }
}
