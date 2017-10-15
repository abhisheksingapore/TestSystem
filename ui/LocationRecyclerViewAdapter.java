package me.veganbuddy.veganbuddy.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import me.veganbuddy.veganbuddy.R;

/**
 * Created by abhishek on 20/9/17.
 */

public class LocationRecyclerViewAdapter
        extends RecyclerView.Adapter <LocationRecyclerViewAdapter.LocationViewHolder> {

    private static List<String> LocationList;

    public LocationRecyclerViewAdapter(ArrayList tempList) {
        LocationList = tempList;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_item, parent, false);
        return new LocationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder location, int position) {
        location.locationTV.setText(LocationList.get(position));
    }

    @Override
    public int getItemCount() {
        return LocationList.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        public TextView locationTV;
        public LocationViewHolder(View itemView) {
            super(itemView);
            locationTV = itemView.findViewById(R.id.li_location);
        }
    }
}
