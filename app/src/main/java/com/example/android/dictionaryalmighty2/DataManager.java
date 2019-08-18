package com.example.android.dictionaryalmighty2;

import android.support.v7.app.AppCompatActivity;


/*以下功能廢除不使用了*/


public class DataManager extends AppCompatActivity {
    private static DataManager dataManager;
    private String imageUrl;

    public static DataManager getInstance(){

        if(dataManager == null){
            dataManager = new DataManager();
        }
        return dataManager;
    }

    private DataManager(){}

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
}
