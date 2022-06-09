package com.anbdevelopers.piechartgenerator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.common.base.Joiner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Boolean showPercentage=false;
    photoFragment photoFragment;
    ImageView imageView ;
    EditText titleEditText;
    String finalUrl;
    Button chkBtn;
    ImageButton addEntry;
    RecyclerView recyclerView;
    recyclerViewAdapter recyclerViewAdapter;
    String graphURL;
    ArrayList<String> allLabelArr=new ArrayList<>();
    ArrayList<String> allLabelArrNoPer = new ArrayList<>();
    ArrayList<String> allValueArr=new ArrayList<>();
    ArrayList<String> allColorCodeArr = new ArrayList<>();
    final dataListApi dataListApiObject= com.anbdevelopers.piechartgenerator.dataListApi.getInstance();
    //AdView adView;



    @Override
    public void onBackPressed() {
        if (photoFragment!=null)
        {
            if (photoFragment.isVisible())
            {
                getSupportFragmentManager().beginTransaction().remove(photoFragment).commit();
            }else super.onBackPressed();
        }
        else super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addEntry=findViewById(R.id.add_entry);

        //ad code starts here
        AdView mAdView;
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
         //MediationTestSuite.launch(MainActivity.this);
        imageView=findViewById(R.id.imageView);
        imageView.setClickable(true);
        titleEditText=findViewById(R.id.titleEditText);


        //Adding empty and default blue color
        dataListApiObject.addElement(new dataList("","","51ace3"));

        recyclerView=findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter=new recyclerViewAdapter(MainActivity.this,dataListApiObject.getdataListApi());
        recyclerView.setAdapter(recyclerViewAdapter);

        addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dataListApiObject.addElement(new dataList("","","51ace3"));
                //recyclerViewAdapter.notifyItemInserted(dataListApiObject.getdataListApi().size());
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
       // mainDataListArr=dataListApiObject.getdataListApi();

        chkBtn=findViewById(R.id.refreshBtn);
        chkBtn.setOnClickListener(view -> refreshChart());

        imageView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("urlkey",finalUrl);
            photoFragment=new photoFragment();
            photoFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.photoFragment,photoFragment)
                    .commit();
//                    Toast.makeText(MainActivity.this,
//                            "The favorite list would appear on clicking this icon",
//                            Toast.LENGTH_LONG).show();

        });
    }

    private void refreshChart() {
        allValueArr.clear();
        allLabelArr.clear();
        allColorCodeArr.clear();
        allLabelArrNoPer.clear();
        for (int i=0;i<dataListApiObject.getdataListApi().size();i++){

            if (!dataListApiObject.getdataListApi().get(i).labelText.isEmpty()
                    &&!dataListApiObject.getdataListApi().get(i).value.isEmpty()
                    &&!dataListApiObject.getdataListApi().get(i).colorCode.isEmpty()){

            allLabelArr.add(dataListApiObject.getdataListApi().get(i).labelText+"\\n"+dataListApiObject.getdataListApi().get(i).value+"%");
            allLabelArrNoPer.add(dataListApiObject.getdataListApi().get(i).labelText);
            allValueArr.add(dataListApiObject.getdataListApi().get(i).value);
            allColorCodeArr.add(dataListApiObject.getdataListApi().get(i).colorCode);}
            else {
                Toast.makeText(MainActivity.this, "Empty fields not allowed\nFill or delete empty slots to get correct output", Toast.LENGTH_LONG).show();
            }
        }
        recyclerViewAdapter.notifyDataSetChanged();

        finalUrl = makefinalUrl();
        Log.d("finalUrl", "onClick: "+finalUrl);
        Picasso.get()
                .load(finalUrl)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.failed)
                .into(imageView);
    }

    private String makefinalUrl() {
        String title;
        String values;
        String labels;
        String labelsNoPer;
        String colorcodes;
        String lablesTop="m";
        ArrayList<String> lablestopArr=new ArrayList<>();

        title=titleEditText.getText().toString();
        //for percentage
        labels=Joiner.on("|").skipNulls().join(allLabelArr);
        //for charts without percentage
        labelsNoPer=Joiner.on("|").skipNulls().join(allLabelArrNoPer);

        colorcodes=Joiner.on("|").skipNulls().join(allColorCodeArr);
        values= Joiner.on(",").skipNulls().join(allValueArr);

        for (int i=0;i<allLabelArr.size();i++){
            lablestopArr.add(allLabelArr.get(i).replace("\\n"," "));
        }
           lablesTop=Joiner.on("|").join(lablestopArr);

//        Log.d("allValues", "Values "+values);
//        Log.d("allValues", "makefinalUrl: "+labels);
//        Log.d("allValues", "makefinalUrl: "+colorcodes);


//        Log.d("allValues", "check sb "+values);
//                for (int i=0;i<allValueArr.size();i++){
//                    Log.d("allValues", "check arr: "+allValueArr.get(i));
//        }


        String graphUrlNoPercent="https://image-charts.com/chart?" +
                "cht=p" +
                "&chd=t:" +values+
                "&chco=" +colorcodes+
                "&chs=900x720" +
                "&chl=" +labelsNoPer+
                "&chdl=" +labelsNoPer+
                "&chdls=000000,20"+
                "&chlps=font.size,27"+
                "&chtt="+title+
                "&chts=000000,28";

        graphURL="https://image-charts.com/chart?" +
                "cht=p" +
                "&chd=t:" +values+
                "&chco=" +colorcodes+
                "&chs=900x720" +
                "&chl=" +labels+
                "&chdl=" +lablesTop+
                "&chdls=000000,20"+
                "&chlps=font.size,27"+
                "&chtt="+title+
                "&chts=000000,28";

        //Log.d("allValues", "makefinalUrl: "+graphURL);

        if (showPercentage){
            return graphURL;
        }else {return graphUrlNoPercent;}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.home_menu,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_phone:
                if (item.isChecked()) {
                    item.setChecked(false);
                    showPercentage=false;
                } else {
                    item.setChecked(true);
                    showPercentage=true;
                }
                break;}
        return super.onOptionsItemSelected(item);
    }
}