package com.example.locationsfinder.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.locationsfinder.Activities.MainActivity;
import com.example.locationsfinder.Models.Location;
import com.example.locationsfinder.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddLocationFragment extends Fragment {
    @Nullable

    private DatabaseReference databaseReference;
    public static int size;

    // components
    private EditText locationName_EDT;
    private EditText locationCity_EDT;
    private EditText locationRating_EDT;
    private EditText imageURL;
    private EditText openingHour_EDT;
    private EditText closingHour_EDT;
    private EditText longtitude;
    private EditText latitude;

    private TextView timeFormat_lbl;

    // check boxes
    private CheckBox required4x4_CB;
    private CheckBox waterActivity_CB;
    private CheckBox closeParking_CB;
    private CheckBox alwaysOpen_CB;

    // submit Button
    private Button submit_BTN;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_location_fragment,container,false);

        findView(view);
        initViews();

        return  view;

    }

    private void initViews() {

        alwaysOpen_CB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alwaysOpen_CB.isChecked())
                {
                    openingHour_EDT.setVisibility(View.INVISIBLE);
                    closingHour_EDT.setVisibility(View.INVISIBLE);
                    timeFormat_lbl.setVisibility(View.INVISIBLE);
                } else {
                    openingHour_EDT.setVisibility(View.VISIBLE);
                    closingHour_EDT.setVisibility(View.VISIBLE);
                    timeFormat_lbl.setVisibility(View.VISIBLE);
                }
            }
        });
            submit_BTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!validInput())
                        Toast.makeText(v.getContext(), "Please check numbers range is valid", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(v.getContext(), "Location add successfully, Thank you!", Toast.LENGTH_SHORT).show();

                }

                });

    }
    // check input from user and location if valid
    private boolean validInput() {
        try {
            int openingHour;
            int closingHour;
            double lats = Double.parseDouble(latitude.getText().toString());
            double longs = Double.parseDouble(longtitude.getText().toString());
            float rating = Integer.parseInt(locationRating_EDT.getText().toString());

            if (rating > 100 || rating < 0)
                return false;

            if (!alwaysOpen_CB.isChecked()) {

                openingHour = Integer.parseInt(openingHour_EDT.getText().toString());
                closingHour = Integer.parseInt(closingHour_EDT.getText().toString());

                if (openingHour < 0 || openingHour > 24)
                    return false;

                if (closingHour < 0 || closingHour > 24)
                    return false;
            }else {
                openingHour = 0;
                closingHour = 0;
            }

            Location location = new Location(locationName_EDT.getText().toString(),
                            lats , longs , rating, closingHour, openingHour, locationCity_EDT.getText().toString()
                            , waterActivity_CB.isChecked()
                            , required4x4_CB.isChecked(), closeParking_CB.isChecked()
                            , imageURL.getText().toString());
            MainActivity mainActivity = (MainActivity) getActivity();
            databaseReference = FirebaseDatabase.getInstance().getReference("Locations");
            //databaseReference.child(location.getName()).setValue(location);
            databaseReference.child(String.valueOf(mainActivity.getLocationList().size())).setValue(location);
            mainActivity.getLocationAdapter().notifyDataSetChanged();

            return true;


        } catch (NumberFormatException e) {
            Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  false;
    }

    private void findView(View view) {

        locationName_EDT = view.findViewById(R.id.locationName_EDT);
        locationCity_EDT = view.findViewById(R.id.locationCity_EDT);
        locationRating_EDT= view.findViewById(R.id.locationRating_EDT);
        imageURL = view.findViewById(R.id.image_url_EDT);
        openingHour_EDT = view.findViewById(R.id.openingHours_EDT);
        closingHour_EDT = view.findViewById(R.id.closingHours_EDT);
        longtitude = view.findViewById(R.id.longs_EDT);
        latitude = view.findViewById(R.id.lats_EDT);
        required4x4_CB = view.findViewById(R.id.is4x4_CB);
        waterActivity_CB = view.findViewById(R.id.waterActivity_CB);
        closeParking_CB = view.findViewById(R.id.hasCloseParking_CB);
        alwaysOpen_CB = view.findViewById(R.id.alwaysOpen_checkBox);

        timeFormat_lbl = view.findViewById(R.id.timeFormat);

        submit_BTN = view.findViewById(R.id.submitForm_BTN);
    }


}
