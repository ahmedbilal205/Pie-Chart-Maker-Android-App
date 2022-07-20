package com.anbdevelopers.piechartgenerator;

import java.util.ArrayList;

public class DataListApi {
    private ArrayList<DataList> dataListArrayListApi=new ArrayList<>();
    public static DataListApi instance;

    public static DataListApi getInstance()
    {
        if (instance == null)
            instance = new DataListApi();
        return instance;
    }

    public DataListApi(){}
    public void addElement(DataList dl){
        dataListArrayListApi.add(dl);
    }
    public void removeElement(int position){
        dataListArrayListApi.remove(position);
    }
    public ArrayList<DataList> getdataListApi(){
        return dataListArrayListApi;
   }
    public DataList getElement(int position){
        return dataListArrayListApi.get(position);
   }
}
