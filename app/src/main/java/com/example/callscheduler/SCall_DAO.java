package com.example.callscheduler;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SCall_DAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertScheduleCall(SCallModel sCallModel);

    @Delete
     void deleteScheduleCall(SCallModel sCallModel);

    @Query("SELECT * FROM SCallModelsTable ORDER BY isSCallDone ASC")
    LiveData<List<SCallModel>> getAllScheduleCalls() ;

    @Query("SELECT * FROM SCallModelsTable WHERE id=:id")
    SCallModel getSCallById(long id);

    @Update
     void updateScheduleCall(SCallModel sCallModel);



}
