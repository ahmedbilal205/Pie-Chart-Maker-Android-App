package com.anbdevelopers.piechartgenerator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.common.base.Joiner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Boolean showPercentage=false;
    PhotoFragment photoFragment;
    ImageView imageView ;
    EditText titleEditText;
    String finalUrl;
    Button chkBtn;
    ImageButton addEntry;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    String graphURL;
    ArrayList<String> allLabelArr=new ArrayList<>();
    ArrayList<String> allLabelArrNoPer = new ArrayList<>();
    ArrayList<String> allValueArr=new ArrayList<>();
    ArrayList<String> allColorCodeArr = new ArrayList<>();
    final DataListApi dataListApiObject= DataListApi.getInstance();

    AdView adView;
    private FrameLayout adContainerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addEntry=findViewById(R.id.add_entry);
        imageView=findViewById(R.id.imageView);
        titleEditText=findViewById(R.id.titleEditText);
        chkBtn=findViewById(R.id.refreshBtn);

        initAds();

        imageView.setClickable(true);
        chkBtn.setOnClickListener(view -> refreshChart());

        //Adding empty and default blue color
        dataListApiObject.addElement(new DataList("","","51ace3"));

        recyclerView=findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter=new RecyclerViewAdapter(MainActivity.this,dataListApiObject.getDataListApi());
        recyclerView.setAdapter(recyclerViewAdapter);

        addEntry.setOnClickListener(view -> {
           dataListApiObject.addElement(new DataList("","","51ace3"));
            recyclerViewAdapter.notifyDataSetChanged();
        });

        imageView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("urlkey",finalUrl);
            photoFragment=new PhotoFragment();
            photoFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.photoFragment,photoFragment)
                    .commit();
        });
    }

    private void initAds() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().
                        setTestDeviceIds(Arrays.asList("880b7af9-f4fb-4aab-af2b-8233e5380503")).build());
        adContainerView = findViewById(R.id.adView);
        adContainerView.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        //MediationTestSuite.launch(MainActivity.this);
    }

    private void loadBanner()
    {
        adView = new AdView(this);
        adView.setAdUnitId(Admob.BANNER_ID);
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
    private void refreshChart() {
        allValueArr.clear();
        allLabelArr.clear();
        allColorCodeArr.clear();
        allLabelArrNoPer.clear();
        for (int i = 0; i<dataListApiObject.getDataListApi().size(); i++){

            if (!dataListApiObject.getDataListApi().get(i).labelText.isEmpty()
                    &&!dataListApiObject.getDataListApi().get(i).value.isEmpty()
                    &&!dataListApiObject.getDataListApi().get(i).colorCode.isEmpty()){

            allLabelArr.add(dataListApiObject.getDataListApi().get(i).labelText+"\\n"+dataListApiObject.getDataListApi().get(i).value+"%");
            allLabelArrNoPer.add(dataListApiObject.getDataListApi().get(i).labelText);
            allValueArr.add(dataListApiObject.getDataListApi().get(i).value);
            allColorCodeArr.add(dataListApiObject.getDataListApi().get(i).colorCode);}
            else {
                Toast.makeText(MainActivity.this, "Empty fields not allowed\nFill or delete empty slots to get correct output", Toast.LENGTH_LONG).show();
            }
        }
        recyclerViewAdapter.notifyDataSetChanged();

        finalUrl = makeFinalUrl();
        Log.d("finalUrl", "onClick: "+finalUrl);
        Picasso.get()
                .load(finalUrl)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.failed)
                .into(imageView);
    }

    private String makeFinalUrl() {
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
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}