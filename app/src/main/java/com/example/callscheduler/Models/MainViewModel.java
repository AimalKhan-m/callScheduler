package com.example.callscheduler.Models;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.loader.content.AsyncTaskLoader;

import com.example.callscheduler.Repository;
import com.example.callscheduler.SCallDataBase;
import com.example.callscheduler.SCallModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {

      private Repository repository ;
      LiveData<List<SCallModel>> numberOfSCalls ;

    public MainViewModel(@NonNull Application application) {

        super(application);

        repository = new Repository(application);




    }

    public LiveData<List<SCallModel>> getNumberOfSCalls() throws ExecutionException, InterruptedException {
        return repository.getAllSCalls();
    }


}
