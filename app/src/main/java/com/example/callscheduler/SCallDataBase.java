package com.example.callscheduler;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;

@Database(entities = {SCallModel.class},version = 1)
public abstract class SCallDataBase extends RoomDatabase {

    public abstract SCall_DAO SCall_DAO() ;
    private  static SCallDataBase   INSTANCE ;

    public static synchronized SCallDataBase getInstance(Context context){
        if(null == INSTANCE){
            Log.e("TAG_DB", "getInstance: NEW CREATE INSTANCE"  );
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),SCallDataBase.class,"SCallDataBase")
                    .fallbackToDestructiveMigration()
                   /* .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Log.e("TAG_DB", "PopulateDbAsyncTask: populating " );

                            new PopulateDbAsyncTask(INSTANCE).execute();
                        }
                        @Override
                        public void onOpen(@NonNull SupportSQLiteDatabase db) {
                            super.onOpen(db);
                            Log.d("ONOPEN","Database has been opened.");
                        }
                    })*/
                    .build();
        }
        Log.e("TAG_DB", "getInstance:  INSTANCE"  );

        return INSTANCE;
    }

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private SCall_DAO scDao;
        private PopulateDbAsyncTask(SCallDataBase db) {
            Log.e("TAG_DB", "PopulateDbAsyncTask: populating " );
            scDao = db.SCall_DAO();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            SCallModel sc1 = new SCallModel();
            sc1.setCalleeName("abc");
            sc1.setCalleeNumber("0123456789");
            sc1.setsCallTime(new Date(System.currentTimeMillis()));
            scDao.insertScheduleCall(sc1);
            scDao.insertScheduleCall(sc1);
            scDao.insertScheduleCall(sc1);
            scDao.insertScheduleCall(sc1);
            return null;
        }
    }

}
