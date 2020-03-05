package com.ayan.truckersapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.ayan.truckersapp.directionhelpers.models.Trucker;
import com.ayan.truckersapp.directionhelpers.models.TruckerList;
import com.ayan.truckersapp.network.RetrofitService;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WelcomeTrucker extends AppCompatActivity {
    RecyclerView recyclerView;
    TruckersAdapter truckersAdapter;
    ArrayList<Trucker> truckers = new ArrayList<>();
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_trucker);
        setTitle("Truckers");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final RxPermissions rxPermissions = new RxPermissions(this);

        Toast.makeText(getApplicationContext(), "Truckers", Toast.LENGTH_LONG).show();
//        Retrofit retrofit = ApiClient.getClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        recyclerView =  findViewById(R.id.trucker_list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initRecyclerVIew();

        final RetrofitService apiService = retrofit.create(RetrofitService.class);

//        Button gettrucker = findViewById(R.id.gettruckers);

        Call<TruckerList> call = apiService.get_all_truckers();
        call.enqueue(new Callback<TruckerList>() {
            @Override
            public void onResponse(Call<TruckerList> call, Response<TruckerList> response) {
                truckers.addAll(response.body().getTruckers());

//                        recyclerView.setAdapter(new TruckersAdapter(getApplicationContext(), R.id.truckers_layout, truckers));
//                        truckersAdapter = new TruckersAdapter(truckers, this);
                truckersAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(truckersAdapter);

            }

            @Override
            public void onFailure(Call<TruckerList> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Falied to load Truckers", Toast.LENGTH_LONG).show();
            }
        });

        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if(granted){
                        statusCheck();
                    }
                });
    }


    public void statusCheck() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        buildAlertMessageNoGps();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void initRecyclerVIew() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        truckersAdapter = new TruckersAdapter(truckers);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(truckersAdapter);
    }

//    @Override
//    public void onTruckerCLick(int position) {
//        Toast.makeText(getApplicationContext(),position,Toast.LENGTH_LONG).show();
//    }
}
