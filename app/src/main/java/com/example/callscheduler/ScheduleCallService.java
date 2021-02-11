package com.example.callscheduler;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.callscheduler.Activities.AskBeforeCallActivity;
import com.example.callscheduler.Activities.MainActivity;

import static com.example.callscheduler.App.CHANNEL_ID;
import static com.example.callscheduler.ScheduleCallReciver.ACTION_CALL_END_CLICK;
import static com.example.callscheduler.ScheduleCallReciver.ACTION_CALL_PICK_CLICK;

public class ScheduleCallService extends Service {

    private static final String TAG = "ScheduleCallService";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SCallModel sCallModel =  intent.getParcelableExtra(AppUtils.KEY_SCALL_OBJ);


        AppUtils.startAlarmAudio(this,Uri.parse(sCallModel.getAlarmAudioUri()));



        RemoteViews rv_smallNotification = new RemoteViews(getPackageName(),R.layout.scall_notification_small_layout);
        rv_smallNotification.setOnClickPendingIntent(R.id.callPickBtn_smallRv,getPendingSelfIntent(this,ACTION_CALL_PICK_CLICK,sCallModel,1));
        rv_smallNotification.setOnClickPendingIntent(R.id.callEndBtn_smallRv,getPendingSelfIntent(this,ACTION_CALL_END_CLICK,sCallModel,2));
        rv_smallNotification.setTextViewText(R.id.calleeNumberTV_smallRv,sCallModel.getCalleeNumber());
        rv_smallNotification.setTextViewText(R.id.calleeNameTV_smallRv,sCallModel.getCalleeName());


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setCustomContentView(rv_smallNotification)
                .setSmallIcon(R.drawable.ic_pick_call)
                .build();
        startForeground(1, notification);


  ;


        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
         AppUtils.stopAlarmAudio();
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action,SCallModel sCallModel,int rq) {
        Intent intent = new Intent(context, ScheduleCallReciver.class);
        Log.e(TAG, "getPendingSelfIntent: " + sCallModel.getCalleeName());
        intent.putExtra(AppUtils.KEY_SCALL_OBJ,(Parcelable) sCallModel);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
