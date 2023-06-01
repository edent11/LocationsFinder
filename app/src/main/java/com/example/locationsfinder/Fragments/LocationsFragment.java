package com.example.locationsfinder.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.audiofx.Equalizer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.locationsfinder.Activities.MainActivity;
import com.example.locationsfinder.Adapters.LocationAdapter;
import com.example.locationsfinder.Listeners.itemClickRV;
import com.example.locationsfinder.Models.Location;
import com.example.locationsfinder.Models.MySingleton;
import com.example.locationsfinder.Models.User;
import com.example.locationsfinder.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.android.gms.location.LocationRequest;

public class LocationsFragment extends Fragment implements itemClickRV {

    private RecyclerView home_fragment;
    private LocationAdapter locationAdapter;
    private DatabaseReference databaseReference;
    private Set<Location> locationArrayList;
    public MaterialToolbar toolbar_main;
    private FusedLocationProviderClient client;
    private double myLats;
    private double myLong;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
        }
        else {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }


        // get array list
        MainActivity mainActivity = (MainActivity) getActivity();
        locationArrayList = new HashSet<Location>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Locations");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Location location = dataSnapshot.getValue(Location.class);
                    locationArrayList.add(location);
                }
                locationAdapter.notifyDataSetChanged();


                mainActivity.setLocationList(locationArrayList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        MySingleton var = MySingleton.getInstance();
        User currentUser = var.getUser();

        locationAdapter = new LocationAdapter(this.getActivity(), locationArrayList, this);
        mainActivity.setLocationAdapter(locationAdapter);



        View view = inflater.inflate(R.layout.fragment_locations, container, false);
        home_fragment = view.findViewById(R.id.home_RV_main);


        home_fragment.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        home_fragment.setHasFixedSize(true);
        home_fragment.setItemAnimator(new DefaultItemAnimator());
        home_fragment.setAdapter(locationAdapter);

        if(currentUser.getFirstName() != null)
            mainActivity.headLine.setText("Hi " + currentUser.getFirstName() +" "+ currentUser.getLastName());

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED))
            getCurrentLocation();
        else {
            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);


            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d("sdf", "haval");
                return;
            }
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task<android.location.Location> task) {

                    android.location.Location location = task.getResult();
                    if (location != null) {

                        myLats = location.getLatitude();
                        myLong = location.getLongitude();
                        ((MainActivity)getActivity()).setMyLats(myLats);
                        ((MainActivity)getActivity()).setMyLongs(myLong);
                        Log.d("sdf", String.valueOf(myLong));
                    } else {

                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(1000);
                        locationRequest.setNumUpdates(1);

                        LocationCallback locationCallBack = new LocationCallback() {

                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                android.location.Location location1 = locationResult.getLastLocation();
                                myLats = location1.getLatitude();
                                myLong = location1.getLongitude();

                            }
                        };

                        client.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper());

                    }


                }
            });
//        }else{
//            Log.d("sdf", "String.valueOf(myLong)");
//           startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//
//        }



    }


    @Override
    public void onItemClicked(Location location) {

        Toast.makeText(this.getContext(), "It's time to go favorites", Toast.LENGTH_SHORT).show();
    }

    public Set<Location> getLocationArrayList() {
        return locationArrayList;
    }
}