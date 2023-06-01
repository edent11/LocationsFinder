package com.example.locationsfinder.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.locationsfinder.Activities.MainActivity;
import com.example.locationsfinder.Listeners.itemClickRV;
import com.example.locationsfinder.Models.Location;
import com.example.locationsfinder.Models.MySingleton;
import com.example.locationsfinder.Models.User;
import com.example.locationsfinder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Set;

public class FavoriteLocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Set<Location> locations;
    private static Activity activity;
    private double userLocation[] = new double[2];
    private itemClickRV listener;


    public FavoriteLocationAdapter(Activity activity, Set<Location> locations, itemClickRV listener) {
        this.activity = activity;
        this.locations = locations;
        this.listener = listener;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_location_list_item, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        LocationViewHolder locationViewHolder = (LocationViewHolder) holder;
        Location location = getItem(position);

        locationViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(getItem(position));
            }
        });

        setUserLocation();
        locationViewHolder.name_lbl.setText(location.getName());
        locationViewHolder.Distance_lbl.setText(String.format("%.1f", getDistance(location)) + " km");
        locationViewHolder.ratingBar.setRating(location.getRating() / 20);
        locationViewHolder.city.setText(location.getCity());

        setOpeningHours(locationViewHolder, location);
        setAttributes(locationViewHolder, location);

        Glide.with(activity)
                .load(location.getImageURI())
                .into(locationViewHolder.locationImg_IMG);


    }



    public void setOpeningHours(LocationViewHolder locationViewHolder, Location location) {
        String text;
        if (location.getClosingHour() == 0)
            text = "OPEN 24/7";
        else if (location.getClosingHour() == -1) {
            text = "CLOSED PERMANENTLY";
        } else {
            text = location.getOpeningHour() + ":00 - " + location.getClosingHour() + ":00";
        }
        locationViewHolder.openingHours.setText(text);
    }

    private void setUserLocation() {
        userLocation[0] = 32.06934987311593;
        userLocation[1] = 34.82944411814634;
    }


    private double getDistance(Location location) {
        return distance(userLocation[0], location.getLats(), userLocation[1], location.getLongs());
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point
     *
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;


        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    // handle location attributes
    private void setAttributes(LocationViewHolder locationViewHolder, Location location) {
        int flag = 0;
        if (location.isHasCloseParking()) {
            locationViewHolder.attributes_lbl.setText("Close parking ");
            flag = 1;
        }
        if (location.isWithWater()) {
            if (flag == 1) {
                CharSequence text = locationViewHolder.attributes_lbl.getText() + "• Water activity";
                locationViewHolder.attributes_lbl.setText(text);
            } else
                locationViewHolder.attributes_lbl.setText("Water activity ");
            flag = 1;
        }
        if (location.isIs4x4()) {
            if (flag == 1) {
                CharSequence text = locationViewHolder.attributes_lbl.getText() + "• 4x4 required";
                locationViewHolder.attributes_lbl.setText(text);
            } else
                locationViewHolder.attributes_lbl.setText(" 4x4 required");

        }
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    private Location getItem(int position) {
        ArrayList<Location> list = new ArrayList<Location>(locations);
        return list.get(position);
    }

    private static Location getItems(int position) {
        ArrayList<Location> list = new ArrayList<Location>();
        return list.get(position);
    }

    private static Location getItemsAt(Set<Location> favoriteLocations, int position) {
        ArrayList<Location> list = new ArrayList<Location>(favoriteLocations);
        return list.get(position);
    }




    public static class LocationViewHolder extends RecyclerView.ViewHolder {

        private RatingBar ratingBar;
        private MaterialTextView city;
        private MaterialTextView openingHours;
        private AppCompatImageView locationImg_IMG;
        private MaterialTextView attributes_lbl;
        private MaterialTextView name_lbl;
        private MaterialTextView Distance_lbl;
        private ImageButton btn_favorite;
        private ImageButton favorite_delete_button;;
        private Button trip_button;;
        private CardView cardView;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            locationImg_IMG = itemView.findViewById(R.id.locationImg_IMG);
            attributes_lbl = itemView.findViewById(R.id.attributes_lbl);
            name_lbl = itemView.findViewById(R.id.name_lbl);
            Distance_lbl = itemView.findViewById(R.id.distance_lbl);
            city = itemView.findViewById(R.id.city_lbl);
            openingHours = itemView.findViewById(R.id.opening_hours_lbl);
            cardView = itemView.findViewById(R.id.listItem_cardView);
            btn_favorite = (ImageButton) itemView.findViewById(R.id.favorite_btn);
            favorite_delete_button = (ImageButton) itemView.findViewById(R.id.favorite_delete_button);
            trip_button = (Button) itemView.findViewById(R.id.trip_btn);




            favorite_delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MainActivity mainActivity = (MainActivity) activity;
                    Set<Location> favLocationList = mainActivity.getFavoriteLocationList();
                    Log.d("Reg", String.valueOf((mainActivity).getLocationList().size()));


                    int position = getAdapterPosition();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Users");
                    MySingleton var = MySingleton.getInstance();
                    User user = var.getUser();


                    Log.d("Reg", String.valueOf((mainActivity).getLocationList().size()));
                    Log.d("fav", String.valueOf((mainActivity).getFavoriteLocationList().size()));
                    if (favLocationList.remove(getItemsAt(mainActivity.getFavoriteLocationList(), position))) {
                        myRef.child(user.getUserId()).child("Locations").child(String.valueOf(position)).removeValue();

//                        if(favLocationList.size() == 1)
//                            no_info_text.setText(view.GONE);

                        mainActivity.favoriteLocationAdapter.notifyDataSetChanged();
                        Toast.makeText(mainActivity, "Successfully deleted", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            trip_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
                    MainActivity mainActivity = (MainActivity) activity;
                    Set<Location> favLocationList = mainActivity.getFavoriteLocationList();
                    int position = getAdapterPosition();
                    Location location = getItemsAt(favLocationList, position);

                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    intent.putExtra(CalendarContract.Events.TITLE, location.getName().toString());
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location.getCity().toString());
                    intent.putExtra(CalendarContract.Events.ALL_DAY, true);
                    intent.putExtra(Intent.EXTRA_EMAIL, account.getEmail());



                        view.getContext().startActivity(intent);



                }
            });







        }
    }

}




