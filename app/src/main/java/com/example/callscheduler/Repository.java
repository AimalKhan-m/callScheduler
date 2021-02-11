package com.example.callscheduler;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Repository {
    SCallDataBase sCallDataBase;

    public Repository(Application application) {
        sCallDataBase = SCallDataBase.getInstance(application);
    }

    public  Repository(Context context) {
        sCallDataBase = SCallDataBase.getInstance(context);

    }


    public SCallModel insertSCall(SCallModel sCallModel) throws ExecutionException, InterruptedException {
          return new GetByIdAsyncTask().execute(new InsertSCallModelAsyncTask().execute(sCallModel).get()).get();
    }
    public Boolean updateSCall(SCallModel sCallModel) throws ExecutionException, InterruptedException {
        return new UpdateSCallModelAsyncTask().execute(sCallModel).get();
    }
    public SCallModel getSCallById(long l) throws ExecutionException, InterruptedException {
        return  new GetByIdAsyncTask().execute(l).get();
    }

    public void deleteSCallById(SCallModel sCallModel){
        new DeleteByIdAsyncTask().execute(sCallModel);
    }

    public LiveData<List<SCallModel>> getAllSCalls() throws ExecutionException, InterruptedException {
        return new GetAllAsyncTask().execute().get();
    }



    //  Performing RoomQUries Asynchronously

    public class InsertSCallModelAsyncTask extends AsyncTask<SCallModel, Void, Long> {

        @Override
        protected Long doInBackground(SCallModel... sCallModels) {
            return sCallDataBase.SCall_DAO().insertScheduleCall(sCallModels[0]);

        }
    }

    public class UpdateSCallModelAsyncTask extends AsyncTask<SCallModel, Void, Boolean> {

        @Override
        protected Boolean doInBackground(SCallModel... sCallModels) {
            sCallDataBase.SCall_DAO().updateScheduleCall(sCallModels[0]);
            Log.e("TAG_UPDATE_CALL", "doInBackground: " +  sCallModels[0].getCalleeName());
            return true;

        }
    }

    public class GetByIdAsyncTask extends AsyncTask<Long, Void, SCallModel> {
        @Override
        protected SCallModel doInBackground(Long... longs) {
            return sCallDataBase.SCall_DAO().getSCallById(longs[0]);
        }
    }

    public class DeleteByIdAsyncTask extends AsyncTask<SCallModel, Void, Void> {
        @Override
        protected Void doInBackground(SCallModel... sCallModels) {
            sCallDataBase.SCall_DAO().deleteScheduleCall(sCallModels[0]);
            return null;
        }
    }

    public class GetAllAsyncTask extends AsyncTask<Void, Void, LiveData<List<SCallModel>>> {
        @Override
        protected LiveData<List<SCallModel>> doInBackground(Void... voids) {
            return sCallDataBase.SCall_DAO().getAllScheduleCalls();
        }
    }


}
