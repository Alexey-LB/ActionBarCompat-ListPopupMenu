package com.example.android.actionbarcompat.listpopupmenu;

/**
 * Created by lesa on 15.12.2016.
 */

public class DataHubSingleton {
    private static DataHubSingleton instance;

    private DataHubSingleton(){
    }

    public static DataHubSingleton getInstance(){
        if (null == instance){
            instance = new DataHubSingleton();
        }
        return instance;
    }
}
