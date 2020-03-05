package com.ayan.truckersapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ayan.truckersapp.directionhelpers.models.Schedule;
import com.ayan.truckersapp.directionhelpers.models.ScheduleList;
import com.ayan.truckersapp.network.RetrofitService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Schedules extends AppCompatActivity {
    RecyclerView recyclerView;
    ScheduleAdapter scheduleAdapter;
    ArrayList<Schedule> schedules = new ArrayList<>();
    TextView info_textvw;
    ProgressBar pbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);

        setTitle("Schedules");
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        info_textvw = findViewById(R.id.info);
        pbar = findViewById(R.id.pbar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        recyclerView =  findViewById(R.id.schedule_list);
        initRecyclerVIew();
        new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL);

        final RetrofitService apiService = retrofit.create(RetrofitService.class);

        pbar.setVisibility(View.VISIBLE);
        Call<ScheduleList> call = apiService.get_current_schedule(id);
        call.enqueue(new Callback<ScheduleList>() {
            @Override
            public void onResponse(Call<ScheduleList> call, Response<ScheduleList> response) {
                pbar.setVisibility(View.GONE);
                if(response.body()!=null && response.body().getSchedules().size()!=0) {
                    schedules.addAll(response.body().getSchedules());

                    scheduleAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(scheduleAdapter);
                }
                else{
                    info_textvw.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ScheduleList> call, Throwable t) {
                pbar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Falied to load Schedules", Toast.LENGTH_LONG).show();
            }
        });


    }
    private void initRecyclerVIew() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        scheduleAdapter = new ScheduleAdapter(schedules);
        recyclerView.setAdapter(scheduleAdapter);
    }
}
