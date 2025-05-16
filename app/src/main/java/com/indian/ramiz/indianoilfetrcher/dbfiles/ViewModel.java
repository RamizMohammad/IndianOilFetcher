package com.indian.ramiz.indianoilfetrcher.dbfiles;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ViewModel extends AndroidViewModel {

    private final Repo repo;
    public MutableLiveData<String>  uriInput = new MutableLiveData<>();
    public MutableLiveData<String> routeInput = new MutableLiveData<>();
    private final LiveData<EntityPojo> urlLiveData;

    public ViewModel(@NonNull Application application){
        super(application);
        repo = new Repo(application);
        urlLiveData = repo.getUrl();
    }

    public LiveData<EntityPojo> getUrlLiveData(){
        return urlLiveData;
    }

    public void saveUrl(){
        String url = uriInput.getValue();
        String route = routeInput.getValue();

        if(url != null && !url.isEmpty()){
            try{
                repo.saveUrl(url, route);
                Toast.makeText(getApplication(), "Data Saved", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Toast.makeText(getApplication(), "Data not Saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void savUrlDirect(String Url, String route){
        if(Url != null && !Url.isEmpty()){
            try{
                repo.saveUrl(Url, route);
                Toast.makeText(getApplication(), "Data Saved", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Toast.makeText(getApplication(), "Data not Saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
