package com.example.callscheduler.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.example.callscheduler.Models.MainViewModel;
import com.example.callscheduler.SCallModel;
import com.example.callscheduler.SCallsAdapter;
import com.example.callscheduler.ScheduleCallReciver;
import com.example.callscheduler.ScheduleCallService;
import com.example.callscheduler.databinding.ActivityMainBinding;

import java.util.concurrent.ExecutionException;

import static com.example.callscheduler.AppUtils.KEY_SCALL_OBJ;

public class MainActivity extends AppCompatActivity {


    MainViewModel mainViewModel ;
    ActivityMainBinding mainBinding ;
    SCallsAdapter sCallsAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());



        mainViewModel = new ViewModelProvider(this,new MyViewModelFactory(getApplication())).get(MainViewModel.class);
        mainBinding.openAddSCallActivityBtn.setOnClickListener(v -> {
            // stating ScallActivity to Add or edit SCall
         startActivity(new Intent(MainActivity.this, AddSCallActivity.class));

       // Just Testing of ScheduleCallService
//            Intent serviceIntent = new Intent(this, ScheduleCallService.class);
//            Intent serviceIntent = new Intent(this, ScheduleCallReciver.class);
//
//            SCallModel sCallModel = new SCallModel() ;
//            sCallModel.setCalleeNumber("123456");
//            sCallModel.setCalleeName("abcc");
//            sCallModel.setAskBeforeCall(false);
//            serviceIntent.putExtra(KEY_SCALL_OBJ, sCallModel);
//
//            ContextCompat.startForegroundService(this,serviceIntent);
//            sendBroadcast(serviceIntent);

        });
        sCallsAdapter = new SCallsAdapter() ;
        try {
            mainViewModel.getNumberOfSCalls().observe(this, sCallModels -> {

                sCallsAdapter.submitList(sCallModels);
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        mainBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.recyclerView.setAdapter(sCallsAdapter);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            mainViewModel.getNumberOfSCalls().observe(this, sCallModels -> {

                sCallsAdapter.submitList(sCallModels);
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public static class MyViewModelFactory implements ViewModelProvider.Factory {
        private Application mApplication;
        private String mParam;
        public MyViewModelFactory(Application application) {
            mApplication = application;
        }
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new MainViewModel(mApplication);
        }
    }

}