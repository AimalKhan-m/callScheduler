package com.example.callscheduler.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import com.example.callscheduler.App;
import com.example.callscheduler.AppUtils;
import com.example.callscheduler.R;
import com.example.callscheduler.SCallModel;
import com.example.callscheduler.ScheduleCallService;
import com.example.callscheduler.databinding.ActivityAskBeforeCallBinding;

import java.io.IOException;

public class AskBeforeCallActivity extends AppCompatActivity {

    ActivityAskBeforeCallBinding abcBinding ;
    SCallModel sCallModel ;
    MediaPlayer mediaPlayer ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        abcBinding = ActivityAskBeforeCallBinding.inflate(getLayoutInflater());
        setContentView(abcBinding.getRoot());
        sCallModel =  getIntent().getParcelableExtra(AppUtils.KEY_SCALL_OBJ);


        mediaPlayer = MediaPlayer.create(this, Uri.parse(sCallModel.getAlarmAudioUri()));



        mediaPlayer.setOnPreparedListener(mp -> {
            mediaPlayer.setLooping(true);

            mp.start();
        });

        abcBinding.calleeNameAbcTV.setText(sCallModel.getCalleeName());
        abcBinding.calleeNumberAbcTV.setText(sCallModel.getCalleeNumber());


        abcBinding.endCallFabAbc.setOnClickListener(v -> {
            stopServiceFun();
        });
        abcBinding.pickCallFabAbc.setOnClickListener(v -> {
            stopServiceFun();
            AppUtils.makeAphoneCall(this,sCallModel.getCalleeNumber(),sCallModel.getAllowSpeakerOn());
        });
    }

    private void stopServiceFun() {
      stopService(new Intent(this, ScheduleCallService.class));
      mediaPlayer.start();
      mediaPlayer.release();
      mediaPlayer = null ;
        finish();
    }





}