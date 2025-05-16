package com.indian.ramiz.indianoilfetrcher.dbfiles;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.concurrent.Executors;

public class Repo {

    private DaoFile daoFile;
    private final LiveData<EntityPojo> liveData;

    public Repo(Application application){
        UrlDatabase database = UrlDatabase.getDB(application);
        daoFile = database.urlDao();
        liveData = daoFile.urlData();
    }

    public LiveData<EntityPojo> getUrl(){
        return liveData;
    }

    public void saveUrl(String url, String route){
        Executors.newSingleThreadExecutor()
                .execute(() -> {
                    daoFile.Insert(
                            new EntityPojo(1,url, route)
                    );
                });
    }
}
