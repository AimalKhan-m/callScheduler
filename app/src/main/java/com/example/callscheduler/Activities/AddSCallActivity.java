package com.example.callscheduler.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.example.callscheduler.AppUtils;
import com.example.callscheduler.Models.AddSCallViewModel;
import com.example.callscheduler.SCallModel;
import com.example.callscheduler.ScheduleCallService;
import com.example.callscheduler.databinding.ActivityAddSCallBinding;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class AddSCallActivity extends AppCompatActivity {

    private static final String TAG = "AddSCallActivity";

    ActivityAddSCallBinding sCallBinding;
    public static final int RESULT_PICK_CONTACT = 111;
    public static final int REQ_PICK_AUDIO = 101;

    AddSCallViewModel addSCallVM ;
    int toBeUpdateId ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sCallBinding = ActivityAddSCallBinding.inflate(getLayoutInflater());
        setContentView(sCallBinding.getRoot());
        addSCallVM =  new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new AddSCallViewModel(getApplication());
            }
        }).get(AddSCallViewModel.class);

        Log.e(TAG, "AddSCallViewModel: Model Id " + addSCallVM.sCallModel.getId() );
        Bundle b= getIntent().getExtras() ;
        if(b!=null){
            SCallModel sCallModel =   b.getParcelable(AppUtils.KEY_SCALL_OBJ);
            addSCallVM.sCallModel = sCallModel ;
            setViewsData(addSCallVM.sCallModel);
            toBeUpdateId = sCallModel.getId() ;
            Log.e(TAG, toBeUpdateId + " odl  Model Id " + addSCallVM.sCallModel.getId() );


        }

        sCallBinding.cancelScallBtn.setOnClickListener(v -> {
            finish();
        });
        sCallBinding.addSCallBtn.setOnClickListener(v -> {
            if(isAllOkFun()){
                sCallBinding.addSCallBtn.setClickable(false);
                sCallBinding.cancelScallBtn.setClickable(false);
                try {
                   if(sCallBinding.addSCallBtn.getText().toString().toLowerCase().equals("add")){
                       addSCallFun();
                   }else{
                    updateSCallFun();
                   }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setUpScheduleCall();
            }
            else{
                Toast.makeText(this, "Something is Invalid", Toast.LENGTH_SHORT).show();
            }
        });
        // call to Select Contact From Phone Contact
        sCallBinding.selectContextBtn.setOnClickListener(v -> {
            Intent it = new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(it, RESULT_PICK_CONTACT);
        });
        // call to pick up Date and Time
        sCallBinding.pickDTBtn.setOnClickListener(v -> {
            pickDTFun();
        });
        sCallBinding.pickAaudioBtn.setOnClickListener(v->{
//------
       /*     Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*"); // specify "audio/mp3" to filter only mp3 files
            startActivityForResult(intent,REQ_PICK_AUDIO);*/

            Intent audio_picker_intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(audio_picker_intent, REQ_PICK_AUDIO);
        });


    }

    private void setViewsData(SCallModel sCallModel) {

        sCallBinding.addSCallBtn.setText("Update");

        sCallBinding.calleeNameET.setText(sCallModel.getCalleeName()+"");
        sCallBinding.calleeContectET.setText(sCallModel.getCalleeNumber());
        sCallBinding.dtTV.setText(AppUtils.getDTin_YYMMDD_time(sCallModel.getsCallTime()));
        sCallBinding.askBCallSwitch.setChecked(sCallModel.getAskBeforeCall());
        sCallBinding.speakerSwitch.setChecked(sCallModel.getAllowSpeakerOn());

    }

    private void setUpScheduleCall() {
        Intent i = new Intent(this, ScheduleCallService.class);


    }

    private void addSCallFun() throws ExecutionException, InterruptedException {
       SCallModel scm = addSCallVM.insertSCall();
        while (scm == null){}
        Log.e(TAG, "addSCallFun: clicked first " + scm.getAskBeforeCall() );
        AppUtils.setSCallToAlaramManager(this,scm);
       finish();
    }

    private void updateSCallFun() throws ExecutionException, InterruptedException {
        Boolean isUpdated = addSCallVM.update(toBeUpdateId);
        while (isUpdated == null){}
        AppUtils.setSCallToAlaramManager(this,addSCallVM.sCallModel);
        Toast.makeText(this, "UPDATED", Toast.LENGTH_SHORT).show();
        finish();
    }


    private boolean isAllOkFun() {
        Boolean isAllOk = false ;
        addSCallVM.sCallModel.setCalleeName(sCallBinding.calleeNameET.getText().toString().trim().length() == 0 ? "UNKNOWN" : sCallBinding.calleeNameET.getText().toString());
        if(sCallBinding.calleeContectET.getText().toString().length() > 3 ){
            addSCallVM.sCallModel.setCalleeNumber(sCallBinding.calleeContectET.getText().toString().trim()+"");
        }else{
            Toast.makeText(this, "Contact Number is Invalid", Toast.LENGTH_SHORT).show();
        }
        if(addSCallVM.sCallModel.getsCallTime() != null){
            if(addSCallVM.sCallModel.getsCallTime().after(Calendar.getInstance().getTime()) == true){
                isAllOk = true ;
            }else{
                Toast.makeText(this, "Incorrect Date and Time", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Please Pick Date and Time", Toast.LENGTH_SHORT).show();
        }
         addSCallVM.sCallModel.setAllowSpeakerOn(sCallBinding.speakerSwitch.isChecked() ? true : false );
         addSCallVM.sCallModel.setAskBeforeCall(sCallBinding.askBCallSwitch.isChecked() ? true : false );
         Long l = new Long(System.currentTimeMillis());
         l.intValue();
         addSCallVM.sCallModel.setId((int) System.currentTimeMillis());
         addSCallVM.sCallModel.setSCallDone(false);
        return isAllOk ;
    }

    private void pickDTFun() {
        new SingleDateAndTimePickerDialog.Builder(this  )
               .bottomSheet()
                .curved()
                .minutesStep(1)
                .mustBeOnFuture()
                .displayListener(picker -> {
                    // Retrieve the SingleDateAndTimePicker
                })
                .title("Pick Date and Time")
                .listener(date -> {
                    addSCallVM.sCallModel.setsCallTime(date);
                    sCallBinding.dtTV.setText(AppUtils.getDTin_YYMMDD_time(date));
                }).display();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + " ,, " + resultCode + " ");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT: {            // Get the URI that points to the selected contact
                    Uri contactUri = data.getData();
                    // We only need the NUMBER column, because there will be only one row in the result
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

                    String[] segments = contactUri.toString().split("/");
                    String id = segments[segments.length - 1];

                    // Perform the query on the contact to get the NUMBER column
                    // We don't need a selection or sort order (there's only one result for the given URI)
                    // CAUTION: The query() method should be called from a separate thread to avoid blocking
                    // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                    // Consider using CursorLoader to perform the query.
                    Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        int cid = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                        String contactid = cursor.getString(cid);

                        if (contactid.equals(id)) {
                            // Retrieve the phone number from the NUMBER column
                            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            String number = cursor.getString(column);

                            // Retrieve the contact name from the DISPLAY_NAME column
                            int column_name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                            String name = cursor.getString(column_name);

                            // Do something with the phone number...
                            Toast.makeText(this, "I added the Contact: \n" + name + " " + number, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, name + "," + number);
                            sCallBinding.calleeNameET.setText(name);
                            sCallBinding.calleeContectET.setText(number);
                            break;
                        }


                        cursor.moveToNext();
                    }

                    cursor.close();
                }
                case REQ_PICK_AUDIO: {
                    Log.e(TAG, "onActivityResult: AUDIO URI " + data.getData().toString() );

                         addSCallVM.sCallModel.setAlarmAudioUri(data.getData().toString());
                    File audioFile = new File(data.getDataString());
                    sCallBinding.AaudioNameTV.setText(audioFile.getName());

                }
            }
        } else {
            Log.e(TAG, "Not able to pick contact");
            Toast.makeText(this, "Not able to pick contact", Toast.LENGTH_SHORT).show();
        }
    }

}