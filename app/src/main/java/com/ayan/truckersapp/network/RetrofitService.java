package com.ayan.truckersapp.network;

import com.ayan.truckersapp.directionhelpers.models.Order;
import com.ayan.truckersapp.directionhelpers.models.OrderList;
import com.ayan.truckersapp.directionhelpers.models.ScheduleList;
import com.ayan.truckersapp.directionhelpers.models.TruckerList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("deliveries/schedule_orders")
//    Call<OrderList> get_current_schedule(@QueryMap() Map<String, String> queries);
    Call<OrderList> get_schedule_orders(@Query("id") String id,
                                        @Query("lat") Double lat,
                                        @Query("lng") Double lng);

    @GET("truckers/truckers_list")
    Call<TruckerList> get_all_truckers();

    @GET("schedules/current_schedule")
    Call<ScheduleList> get_current_schedule(@Query("id") String id);

    @PATCH("deliveries/update_order_status")
    @FormUrlEncoded
    Call<Order> update_order_status(@Field("order_id") String order_id);
}
