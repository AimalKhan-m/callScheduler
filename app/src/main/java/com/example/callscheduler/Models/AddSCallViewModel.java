package com.example.callscheduler.Models;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.callscheduler.Repository;
import com.example.callscheduler.SCallDataBase;
import com.example.callscheduler.SCallModel;

import java.util.concurrent.ExecutionException;


public class AddSCallViewModel extends AndroidViewModel {

    private  Repository repository ;
    public SCallModel sCallModel = new SCallModel();

    private static final String TAG = "AddSCallViewModel";



    public AddSCallViewModel(@NonNull Application application) {
        super(application);
            repository = new Repository(getApplication());

    }

    public SCallModel insertSCall() throws ExecutionException, InterruptedException {
        Log.e(TAG, "addSCallToDB: CLOCKED " + sCallModel.getAskBeforeCall()  );
        return repository.insertSCall(sCallModel);
    }


    public Boolean update(int id) throws ExecutionException, InterruptedException {
        Log.e(TAG,  sCallModel.getId() + "  ID : addSCallToDB: CLOCKED " + sCallModel.getAskBeforeCall()  );
            sCallModel.setId(id);
        return repository.updateSCall(sCallModel);
    }
}
