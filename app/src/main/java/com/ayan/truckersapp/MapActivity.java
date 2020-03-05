package com.ayan.truckersapp;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayan.truckersapp.utils.CompletedOrdersAdapter;
import com.ayan.truckersapp.utils.DataTransferInterface;
import com.ayan.truckersapp.utils.MyLocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ayan.truckersapp.directionhelpers.FetchURL;
import com.ayan.truckersapp.directionhelpers.TaskLoadedCallback;
import com.ayan.truckersapp.directionhelpers.models.Order;
import com.ayan.truckersapp.directionhelpers.models.OrderList;
import com.ayan.truckersapp.network.RetrofitService;
import com.ayan.truckersapp.utils.MyItemTouchHelper;
import com.ayan.truckersapp.utils.VerticalSpacingItemDecorator;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, TaskLoadedCallback,
        OrdersAdapter.OnOrderListener,
        FloatingActionButton.OnClickListener,
        DataTransferInterface {
    private GoogleMap mMap;
    private MarkerOptions place1, place2, place3, place4;
    //    ArrayList<LatLng> waypoint_places = new ArrayList<>();
    ArrayList<Pair<String, LatLng>> waypoint_places = new ArrayList<>();
    ArrayList<LatLng> waypoints = new ArrayList<>();
    Button getDirection, getList;
    private Polyline currentPolyline;
    RecyclerView recyclerView, completedRecyclerView;
    ArrayList<Order> orders = new ArrayList<>();
    ArrayList<Order> completed_orders = new ArrayList<>();
    OrdersAdapter ordersAdapter;
    CompletedOrdersAdapter completedOrdersAdapter;

    LinearLayout list_layout, map_layout;
    FloatingActionButton fab;

    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient fusedLocationProviderClient;
    RetrofitService apiService;
    LocationManager locationManager;

    RxPermissions rxPermissions;
    TextView info_textvw;
    ScrollView root;

    ProgressBar pbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        rxPermissions = new RxPermissions(this);

        getDirection = findViewById(R.id.btnGetDirection);
        pbar = findViewById(R.id.pbar);

        list_layout = findViewById(R.id.list_layout);
        map_layout = findViewById(R.id.map_layout);
        fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
//                checkLocationPermission();
//                statusCheck();

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Location Permission already granted
                        statusCheck();
                        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);

                        map_layout.setVisibility(View.VISIBLE);
                        list_layout.setVisibility(View.INVISIBLE);
                        fab.setVisibility(View.INVISIBLE);
                        waypoint_places.clear();
                        for (Order o : orders) {
                            waypoint_places.add(new Pair<>(o.getName(), new LatLng(
                                            Double.parseDouble(o.getLat()),
                                            Double.parseDouble(o.getLng()))
                                    )
                            );
                        }

                        if (waypoint_places.size() >= 1) {
                            for (Pair<String, LatLng> val : waypoint_places.subList(1, waypoint_places.size())) {
                                mMap.addMarker(new MarkerOptions().position(val.second).title(val.first));
                            }
                            Marker next_mark = mMap.addMarker(
                                    new MarkerOptions().position(waypoint_places.get(0).second).title(waypoint_places.get(0).first));
                            next_mark.showInfoWindow();
                            setMarkerBounce(next_mark);
                        }
                        place2 = new MarkerOptions().position(waypoint_places.get(waypoint_places.size() - 1).second);
                        waypoint_places.remove(waypoint_places.size() - 1);


                        // get the last know location from your location manager.
                        Location location = getLastKnownLocation();
                        place1 = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
                        pbar.setVisibility(View.VISIBLE);
                        new FetchURL(MapActivity.this).execute(getUrl(place1.getPosition(), waypoint_places, place2.getPosition(), "driving"), "driving");

                    } else {
                        //Request Location Permission
                        Toast.makeText(getApplicationContext(), "Please allow Location Permission", Toast.LENGTH_SHORT).show();
                        checkLocationPermission();
                    }
                }

            }
        });

//        place1 = new MarkerOptions().position(new LatLng(27.658143, 85.3199503)).title("Location 1");
//        place2 = new MarkerOptions().position(new LatLng(27.666491, 85.3208583)).title("Location 2");

        Intent intent = getIntent();
        final String trucker_id = intent.getStringExtra("id");

        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        info_textvw = findViewById(R.id.info);
        root = findViewById(R.id.root_scroller);

        recyclerView = findViewById(R.id.order_list);
        completedRecyclerView = findViewById(R.id.completed_order_list);
        initRecyclerVIew();
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        recyclerView.addItemDecoration(itemDecorator);
        completedRecyclerView.addItemDecoration(itemDecorator);
        apiService = retrofit.create(RetrofitService.class);

        ItemTouchHelper.Callback callback = new MyItemTouchHelper(ordersAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        ordersAdapter.setTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        Location location = getLastKnownLocation();
        place1 = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));

        pbar.setVisibility(View.VISIBLE);
        Call<OrderList> call = apiService.get_schedule_orders(trucker_id, place1.getPosition().latitude, place1.getPosition().longitude);
        call.enqueue(new Callback<OrderList>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<OrderList> call, Response<OrderList> response) {
                pbar.setVisibility(View.GONE);
                ArrayList<Order> os = new ArrayList<Order>();

                if (response.body() == null) {
                    root.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                    info_textvw.setVisibility(View.VISIBLE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                    os.addAll(response.body().getOrderList());

                    orders.clear();
                    completed_orders.clear();
                    for (Order o : os) {
                        if (o.getStatus().equals("delivered"))
                            completed_orders.add(o);
                        else
                            orders.add(o);
                    }
//                orders.addAll(response.body().getOrderList());
                    Log.d("Orders", orders.size() + "");
                    ordersAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(ordersAdapter);

                    completedOrdersAdapter.notifyDataSetChanged();
                    completedRecyclerView.setAdapter(completedOrdersAdapter);
                    for (Order o : orders) {
                        waypoint_places.add(new Pair<>(o.getName(), new LatLng(
                                        Double.parseDouble(o.getLat()),
                                        Double.parseDouble(o.getLng()))
                                )
                        );
                    }
//                place1 = new MarkerOptions().position(waypoint_places.get(0).second);
//                new FetchURL(MapActivity.this).execute(getUrl(waypoint_places.get(0).second, waypoint_places, waypoint_places.get(0).second, "driving"), "driving");
                    mapFragment.getMapAsync(MapActivity.this);
                }
            }

            @Override
            public void onFailure(Call<OrderList> call, Throwable t) {
                pbar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "ERRORR", Toast.LENGTH_LONG).show();
            }

        });

        getDirection.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                for (Order o : orders) {
                    waypoint_places.add(new Pair<>(o.getName(), new LatLng(
                                    Double.parseDouble(o.getLat()),
                                    Double.parseDouble(o.getLng()))
                            )
                    );
                }
                place2 = new MarkerOptions().position(waypoint_places.get(waypoint_places.size() - 1).second);
                waypoint_places.remove(waypoint_places.size() - 1);

                new FetchURL(MapActivity.this).execute(getUrl(place1.getPosition(), waypoint_places, place2.getPosition(), "driving"), "driving");

//                fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(getApplicationContext(), "Getting Routes", Toast.LENGTH_SHORT).show();
//                        new FetchURL(MapActivity.this).execute(getUrl(place1.getPosition(), waypoint_places, place2.getPosition(), "driving"), "driving");
//
//                    }
//                });
//                mMap.setMyLocationEnabled(true);


//                new FetchURL(MapActivity.this).execute(getUrl(place1.getPosition(),waypoint_places,place2.getPosition(), "driving"), "driving");

            }
        });
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
                statusCheck();
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }

//        if(waypoint_places.size()>=1) {
//            for (Pair<String, LatLng> val : waypoint_places.subList(1, waypoint_places.size() - 1)) {
//                mMap.addMarker(new MarkerOptions().position(val.second)).setTitle(val.first);
//            }
//            setMarkerBounce(mMap.addMarker(new MarkerOptions().position(waypoint_places.get(0).second)));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(waypoint_places.get(0).second, 11));
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getUrl(LatLng origin, ArrayList<Pair<String, LatLng>> waypoints, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        StringBuilder parameters = new StringBuilder();
        parameters.append(str_origin);

        StringJoiner joiner = new StringJoiner("via:");
        joiner.add("&waypoints=");
        for (Pair<String, LatLng> i : waypoints) {
            StringBuilder val = new StringBuilder();
            val.append(i.second.latitude + "," + i.second.longitude);

            if (waypoints.indexOf(i) != waypoints.size() - 1)
                val.append("|");
            joiner.add(val);
        }
        String joinedString = joiner.toString();

        parameters.append(joinedString);

        // Destination of route
        String str_dest = "&destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        parameters.append("&" + str_dest + "&" + mode);
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);


        Log.d("FETCHURL", url);
        return url;
//
//        return "https://maps.googleapis.com/maps/api/directions/json? origin=Brooklyn&destination=Queens &mode=transit"+
//                "&key="+getString(R.string.google_maps_key);
    }

    @Override
    public void onTaskDone(Object... values) {
//        Log.d("TASK DONE", currentPolyline.toString());
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        pbar.setVisibility(View.GONE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place1.getPosition(), 15f));

    }


    @Override
    public void onOrderClick(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Order Completion")
                .setMessage("Are you sure to mark the order as completed?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        orders.remove(recyclerView.findViewHolderForAdapterPosition(position));

                        Call<Order> call = apiService.update_order_status(String.valueOf(orders.get(position).getId()));
                        call.enqueue(new Callback<Order>() {
                            @Override
                            public void onResponse(Call<Order> call, Response<Order> response) {
                                completed_orders.add(orders.get(position));
                                orders.remove(orders.get(position));

                                ordersAdapter.notifyDataSetChanged();
                                completedOrdersAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<Order> call, Throwable t) {
                                Log.d("ERROR", t.toString());
                            }
                        });
//                        updateOrderList();
//                        Toast.makeText(getApplicationContext(),"Order delivered "+orders.get(position).getName(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onClick(View view) {
//        Toast.makeText(getApplicationContext(), "Floaat", Toast.LENGTH_SHORT).show();
        map_layout.setVisibility(View.VISIBLE);
        list_layout.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {
        if (map_layout.getVisibility() == View.VISIBLE) {
            map_layout.setVisibility(View.INVISIBLE);
            list_layout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
//            if (currentPolyline != null) {
//                currentPolyline.remove();
//                currentPolyline = null;
//            }
            mMap.clear();
        } else if (list_layout.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        }
    }

    private void initRecyclerVIew() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        completedRecyclerView.setLayoutManager(linearLayoutManager2);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());

        recyclerView.addItemDecoration(dividerItemDecoration);
        completedRecyclerView.addItemDecoration(dividerItemDecoration);

        ordersAdapter = new OrdersAdapter(getApplicationContext(), R.id.list_layout, orders, this, this);
        completedOrdersAdapter = new CompletedOrdersAdapter(getApplicationContext(), R.id.list_layout, completed_orders);

        recyclerView.setAdapter(ordersAdapter);
        completedRecyclerView.setAdapter(completedOrdersAdapter);
    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
//                Timer timer = new Timer();
//                TimerTask updateProfile = new CustomTimerTask(MapActivity.this);
//                timer.scheduleAtFixedRate(updateProfile, 10,5000);
//                mCurrLocationMarker = mMap.addMarker(markerOptions);

                place1 = markerOptions;
//                place2 = new MarkerOptions().position(waypoint_places.get(waypoint_places.size()-1).second);
//                waypoint_places.remove(waypoint_places.size()-1);
//                new FetchURL(MapActivity.this).execute(getUrl(place1.getPosition(),waypoint_places,place2.getPosition(), "driving"), "driving");
//                mMap.addPolyline(new PolylineOptions()
//                        .add(markerOptions.getPosition(), waypoint_places.get(0).second).clickable(true).color(Color.RED));
                //move map camera
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
            }
        }
    };

    private void setMarkerBounce(final Marker marker) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + t);

                if (t > 0.0) {
                    handler.postDelayed(this, 16);
                } else {
                    setMarkerBounce(marker);
                }
            }
        });
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        statusCheck();
                        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void setValues(ArrayList<Order> al) {
        orders.clear();
        orders = al;
    }

    class CustomTimerTask extends TimerTask {
        private Context context;
        private Handler mHandler = new Handler();

        // Write Custom Constructor to pass Context
        public CustomTimerTask(Context con) {
            this.context = con;
        }

        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            final long start = SystemClock.uptimeMillis();
                            final long duration = 1500;

                            final Interpolator interpolator = new BounceInterpolator();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    long elapsed = SystemClock.uptimeMillis() - start;
                                    float t = Math.max(
                                            1 - interpolator.getInterpolation((float) elapsed
                                                    / duration), 0);
                                    mCurrLocationMarker.setAnchor(0.5f, 0.5f + 2 * t);

                                    if (t > 0.0) {
                                        // Post again 16ms later.
                                        handler.postDelayed(this, 16);
                                    }
                                }
                            });
                        }
                    });
                }
            }).start();

        }

    }

}


