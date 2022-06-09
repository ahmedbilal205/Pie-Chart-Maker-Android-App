package com.anbdevelopers.piechartgenerator;

import android.app.Application;

import java.util.ArrayList;

public class dataListApi extends Application {
    private ArrayList<dataList> dataListArrayListApi=new ArrayList<>();
    public static dataListApi instance;

    public static dataListApi getInstance()
    {
        if (instance == null)
            instance = new dataListApi();
        return instance;

    }

    public dataListApi(){}
    public void addElement(dataList dl){
        dataListArrayListApi.add(dl);
    }
    public void removeElement(int position){
        dataListArrayListApi.remove(position);
    }


   public ArrayList<dataList> getdataListApi(){
        return dataListArrayListApi;
   }
   public dataList getElement(int position){
        return dataListArrayListApi.get(position);
   }
}
