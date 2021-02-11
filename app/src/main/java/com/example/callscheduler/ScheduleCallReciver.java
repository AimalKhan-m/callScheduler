package com.example.callscheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.ExecutionException;

import static com.example.callscheduler.AppUtils.KEY_SCALL_OBJ;


public class ScheduleCallReciver extends BroadcastReceiver {

    private static final String TAG = "ScheduleCallReciver";

    public static final String ACTION_CALL_PICK_CLICK = "pickSCall";
    public static final String ACTION_CALL_END_CLICK = "endSCall";

    @Override
    public void onReceive(Context context, Intent intent) {



           Bundle b = intent.getExtras();
           SCallModel sCallModel =  b.getParcelable(KEY_SCALL_OBJ);
        try {
            sCallIsDone(context,sCallModel);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "onReceive: sCall"  + sCallModel.getAskBeforeCall() );
           if( sCallModel.getAskBeforeCall() && NotificationManagerCompat.from(context).areNotificationsEnabled()) {
               startAskBeforeCallServices(context,sCallModel);
           }else {
               AppUtils.makeAphoneCall(context,sCallModel.getCalleeNumber(),sCallModel.getAllowSpeakerOn());
           }




    if(intent.getAction() != null){
        if (ACTION_CALL_PICK_CLICK.equals(intent.getAction())){
            //your onClick action is here
            AppUtils.stopAlarmAudio();
            AppUtils.makeAphoneCall(context,sCallModel.getCalleeNumber(),sCallModel.getAllowSpeakerOn());
            Intent serviceIntent = new Intent(context.getApplicationContext(), ScheduleCallService.class);
            context.stopService(serviceIntent);
            Log.e(TAG, "onReceive:  " + intent.getAction() );
        }else if (ACTION_CALL_END_CLICK.equals(intent.getAction())){
            AppUtils.stopAlarmAudio();
            Log.e(TAG, "onReceive:  " + intent.getAction() );
            Intent serviceIntent = new Intent(context, ScheduleCallService.class);
            context.stopService(serviceIntent);


        }
    }



        Toast.makeText(context, "ALARAMED"+ sCallModel.getAskBeforeCall(), Toast.LENGTH_SHORT).show();
        Log.e("TAG_REVICER", "onReceive: CALLED " + sCallModel.getAskBeforeCall() );
    }

    private void startAskBeforeCallServices(Context c,SCallModel sCallModel) {
        Intent serviceIntent = new Intent(c, ScheduleCallService.class);
        serviceIntent.putExtra(KEY_SCALL_OBJ, sCallModel);
        ContextCompat.startForegroundService(c, serviceIntent);
    }

    public void sCallIsDone(Context context , SCallModel sCallModel) throws ExecutionException, InterruptedException {
        Repository repository = new Repository(context);
        sCallModel.setSCallDone(true);
        repository.updateSCall(sCallModel);

    }


}
