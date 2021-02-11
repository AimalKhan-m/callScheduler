package com.example.callscheduler;

import android.animation.FloatEvaluator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.room.TypeConverter;

import com.example.callscheduler.Models.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.TELECOM_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.telephony.PhoneStateListener.LISTEN_CALL_STATE;

public class AppUtils {


    public static final String KEY_SCALL_OBJ = "sCallObj";
    private static final String TAG = "AppUtils";
    public static MediaPlayer mediaPlayer = new MediaPlayer();


    public static class DateConverter {

        @TypeConverter
        public static Date toDate(Long timestamp) {
            return timestamp == null ? null : new Date(timestamp);
        }

        @TypeConverter
        public static Long toTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }
    }

    public static String getDTin_YYMMDD_time(Date date) {

        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yy hh:mm a");
        String stringDate = DateFor.format(date);
        return stringDate.toUpperCase();
    }

    public static void makeAphoneCall(Context context, String number, Boolean allowSpeakOn) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));//change the number
        callIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        ContextCompat.startActivity(context, callIntent, ActivityOptionsCompat.makeBasic().toBundle());

        if (allowSpeakOn) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
            // Phone State Listener Class
            PhoneStateListener listener = new PhoneStateListener() {
                private boolean isPickUp = false;

                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    String stateString = "N/A";
                    switch (state) {
                        case TelephonyManager.CALL_STATE_IDLE:
                            stateString = "Idle";
                            if (isPickUp) {
                                audioManager.setSpeakerphoneOn(false);

                            }
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            stateString = "Off Hook";
                            callPicked();
                            break;
                        case TelephonyManager.CALL_STATE_RINGING:
                            stateString = "Ringing";
                            callPicked();
                            break;
                    }
                }

                private void callPicked() {
                    isPickUp = true;
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(true);
                }
            };

            tm.listen(listener, LISTEN_CALL_STATE);
        }


    }

    public static void setSCallToAlaramManager(Context context, SCallModel sCallModel) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, ScheduleCallReciver.class);
        intent.putExtra(KEY_SCALL_OBJ, sCallModel);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) sCallModel.getId(), intent, 0);


        if (sCallModel.getsCallTime() != null && sCallModel.getsCallTime().after(Calendar.getInstance().getTime())) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, sCallModel.getsCallTime().getTime(), pendingIntent);
            Toast.makeText(context, "Alarm Set ", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(context, "Date and Time is Invalid", Toast.LENGTH_SHORT).show();

        }
    }

    public static void deleteSCallToAlaramManager(Context context, SCallModel sCallModel) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, ScheduleCallReciver.class);
        intent.putExtra(KEY_SCALL_OBJ, sCallModel);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) sCallModel.getId(), intent, 0);
        alarmManager.cancel(pendingIntent);
        if (sCallModel.getsCallTime() != null && sCallModel.getsCallTime().after(Calendar.getInstance().getTime())) {

            Toast.makeText(context, "Scheduled Call Cancel ", Toast.LENGTH_SHORT).show();

        }
    }


    public static void startAlarmAudio(Context context, Uri uri) throws IllegalStateException {
        Log.e(TAG, "startAlarmAudio: ALAmm START");

        if (mediaPlayer != null) {
            stopAlarmAudio();
        }
        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.setLooping(true);
        AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_VIBRATE);
        mediaPlayer.start();
    }

    public static void stopAlarmAudio() throws IllegalStateException {
        Log.e(TAG, "startAlarmAudio: ALAmm STOP");
        if(mediaPlayer!=null){

            mediaPlayer.setLooping(false);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


}
