package com.indian.ramiz.indianoilfetrcher.dbfiles;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoFile {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void Insert(EntityPojo pojo);

    @Query("SELECT * FROM data WHERE id=1")
    LiveData<EntityPojo> urlData();
}
