package com.example.locationsfinder.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationsfinder.Activities.MainActivity;
import com.example.locationsfinder.Adapters.FavoriteLocationAdapter;
import com.example.locationsfinder.Listeners.itemClickRV;
import com.example.locationsfinder.Models.Location;
import com.example.locationsfinder.Models.MySingleton;
import com.example.locationsfinder.Models.User;
import com.example.locationsfinder.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class MyFavLocationsFragment extends Fragment implements itemClickRV {


    private User currentUser;
    private RecyclerView home_fragment;
    private FavoriteLocationAdapter favoriteLocationAdapter;
    private DatabaseReference databaseReference;
    private Set<Location> favoriteLocationList;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriteLocationAdapter = new FavoriteLocationAdapter(this.getActivity(), favoriteLocationList,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_fav_locations, container, false);
        ImageButton favorite_delete_button = (ImageButton)view.findViewById(R.id.favorite_delete_button);
                MainActivity mainActivity = (MainActivity) getActivity();

        TextView no_info_text = (TextView) view.findViewById(R.id.no_info_text);
        MySingleton var = MySingleton.getInstance();
        currentUser = var.getUser();





        // get array list
        favoriteLocationList = mainActivity.getFavoriteLocationList() ;

        favoriteLocationAdapter = new FavoriteLocationAdapter(this.getActivity(), favoriteLocationList,this);



        mainActivity.setFavoriteLocationAdapter(favoriteLocationAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUserId()).child("Locations");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Location location = (Location) dataSnapshot.getValue(Location.class);
                    favoriteLocationList.add(location);
                    no_info_text.setVisibility(view.GONE);

                }


                favoriteLocationAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });









        home_fragment = view.findViewById(R.id.favorite_RV_main);


        home_fragment.setLayoutManager(new LinearLayoutManager(this.getContext(),LinearLayoutManager.VERTICAL,false));
        home_fragment.setHasFixedSize(true);
        home_fragment.setItemAnimator(new DefaultItemAnimator());
        home_fragment.setAdapter(favoriteLocationAdapter);

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Users");
//        mAuth = FirebaseAuth.getInstance();






//
//
//
//
        return view;
    }

    @Override
    public void onItemClicked(Location location) {
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.setLats(location.getLats());
        mainActivity.setLongs(location.getLongs());


        MapsFragment mapsFragment = new MapsFragment();
        getParentFragmentManager().beginTransaction().replace(R.id.main_Container, mapsFragment).commit();

    }
}
