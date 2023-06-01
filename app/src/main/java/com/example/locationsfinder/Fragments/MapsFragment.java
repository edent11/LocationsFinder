package com.example.locationsfinder.Fragments;

//import static com.example.locationsfinder.Activities.MainActivity.lats;
//import static com.example.locationsfinder.Activities.MainActivity.longs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.locationsfinder.Models.Location;
import com.example.locationsfinder.Activities.MainActivity;
import com.example.locationsfinder.Models.Location;
import com.example.locationsfinder.Models.MySingleton;
import com.example.locationsfinder.Models.User;
import com.example.locationsfinder.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class MapsFragment extends Fragment {

    private DatabaseReference databaseReference;
    private Set<Location> favoriteLocationsList2;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng home;
            MainActivity mainActivity = (MainActivity) getActivity();

            if (mainActivity.getLats() != 0){
                home = new LatLng(mainActivity.getLats(), mainActivity.getLongs());
            } else {
                 home = new LatLng(32.069, 34.829);
            }

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(home));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));

            if(mainActivity.getMyLats() != 0){
                MarkerOptions markerOptions= new MarkerOptions();
                LatLng latlng = new LatLng(mainActivity.getMyLats(), mainActivity.getMyLongs());
                googleMap.addMarker(markerOptions.position(latlng)
                        .title("ME")
                ).showInfoWindow();
            }




            // get array list
            MySingleton var = MySingleton.getInstance();
            User currentUser = var.getUser();
            favoriteLocationsList2 = new HashSet<Location>();



            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUserId()).child("Locations");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Location location = dataSnapshot.getValue(Location.class);
                        favoriteLocationsList2.add(location);
                        MarkerOptions markerOptions= new MarkerOptions();
                        LatLng latlng = new LatLng(location.getLats(), location.getLongs());
                        googleMap.addMarker(markerOptions.position(latlng)
                                .title(location.getName())
                        ).showInfoWindow();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }


    }

    public void setFavoriteLocationsList(Set<Location> favoriteLocationsList) {
        this.favoriteLocationsList2 = favoriteLocationsList;
    }


}