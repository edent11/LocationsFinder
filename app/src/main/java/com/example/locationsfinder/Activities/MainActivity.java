package com.example.locationsfinder.Activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationsfinder.Adapters.FavoriteLocationAdapter;
import com.example.locationsfinder.Adapters.LocationAdapter;
import com.example.locationsfinder.Fragments.AddLocationFragment;
import com.example.locationsfinder.Fragments.LocationsFragment;
import com.example.locationsfinder.Fragments.MapsFragment;
import com.example.locationsfinder.Fragments.MyFavLocationsFragment;

import com.example.locationsfinder.Models.Location;

import com.example.locationsfinder.Models.User;
import com.example.locationsfinder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigationView;

    private static final String TAG = "MainActivity";
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    public TextView headLine;
    public User currentUser;

    public Set<Location> favoriteLocationList;
    public FavoriteLocationAdapter favoriteLocationAdapter;
    public LocationAdapter locationAdapter;
    public Set<Location> locationList;


    private LocationRequest locationRequest;
    public double lats;
    public double myLats;
    public double longs;
    public double myLongs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        favoriteLocationList = new HashSet<Location>();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        navigationView = findViewById(R.id.bottom_nav_view);
        headLine = (TextView) findViewById(R.id.SignInText);




        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) > 0) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    if(locationResult != null && locationResult.getLocations().size() > 0){

                                        int index = locationResult.getLocations().size() - 1;
                                        lats = locationResult.getLocations().get(index).getLatitude();
                                        longs = locationResult.getLocations().get(index).getLongitude();


                                    }


                                }
                            }, Looper.getMainLooper());


                } else {

                    turnOnPS();

                }

            } else {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 225);

            }

        }



        Fragment locations_fragment = new LocationsFragment();



        getSupportFragmentManager().beginTransaction().replace(R.id.main_Container, locations_fragment).commit();

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                Fragment fragment = null;
                switch (id) {
                    case R.id.locationsFragment:
                        fragment = new LocationsFragment();
                        break;

                    case R.id.myFavLocationsFragment:
                        fragment = new MyFavLocationsFragment();
                        break;

                    case R.id.myTripsFragment:
                        fragment = new AddLocationFragment();
                        break;

                    case R.id.mapsFragment:
                        fragment = new MapsFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.main_Container, fragment).commit();
                return true;
            }
        });
    }


    private void updateUser(String user_email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //If email exists then toast shows else store the data on new key
                    User user = (data.getValue(User.class));

                    //Log.d(TAG, "Value is: " + user.getEmail() +" "+ email);
                    Log.d(TAG, "gffdg");

                    if (user.getEmail().compareTo(user_email) == 0) {

                        Log.d("ya","ya");
                        currentUser = new User(user);
                        if(currentUser.getFirstName() != null) {
                            headLine.setText("Hi " + currentUser.getFirstName() + " " + currentUser.getLastName());
                        }


                        for (DataSnapshot data2 : data.child("Locations").getChildren()) {

                            Location l = data2.getValue(Location.class);
                            favoriteLocationList.add(l);

                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = database.getReference("Users").child(user_id).child("Locations");
        Log.d(TAG, "Value is: " + user_id);
    }

    public void setLocationList(Set<Location> locationList) {
        this.locationList = locationList;
    }

    public Set<Location> getLocationList() {
        return locationList;
    }

    public void setFavoriteLocationList(Set<Location> favoriteLocationList) {
        this.favoriteLocationList = favoriteLocationList;
    }

    public Set<Location> getFavoriteLocationList() {
        return favoriteLocationList;
    }

    public FavoriteLocationAdapter getFavoriteLocationAdapter() {
        return favoriteLocationAdapter;
    }

    public void setFavoriteLocationAdapter(FavoriteLocationAdapter favoriteLocationAdapter) {
        this.favoriteLocationAdapter = favoriteLocationAdapter;
    }

    public int getLocationIndex(Location l) {
        int index = 0;
        Iterator<Location> iterator = favoriteLocationList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(l))
                return index;
            index++;
        }
        return index;
    }

    public double getLats() {
        return lats;
    }

    public double getLongs() {
        return longs;
    }

    public void setLongs(double longs) {
        this.longs = longs;
    }

    public void setLats(double lats) {
        this.lats = lats;
    }

    public double getMyLats() {
        return myLats;
    }

    public double getMyLongs() {
        return myLongs;
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) getSystemService((Context.LOCATION_SERVICE));

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    public void setLocationAdapter(LocationAdapter locationAdapter) {
        this.locationAdapter = locationAdapter;
    }

    public LocationAdapter getLocationAdapter() {
        return locationAdapter;
    }

    private void turnOnPS() {



        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });


    }

    public void setMyLats(double myLats) {
        this.myLats = myLats;
    }

    public void setMyLongs(double myLongs) {
        this.myLongs = myLongs;
    }
}







