package me.veganbuddy.veganbuddy.actors;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by abhishek on 10/12/17.
 */

public class MyPlace {
    public String placeId;
    public String placeName;
    public String placeAddress;
    public LatLng location;

    public float likelihood;

    public MyPlace() {

    }

    public MyPlace(Place thisPlace) {
        placeId = thisPlace.getId();
        placeName = thisPlace.getName().toString();
        placeAddress = thisPlace.getAddress().toString();
        location = thisPlace.getLatLng();
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public float getLikelihood() {
        return likelihood;
    }

    public void setLikelihood(float likelihood) {
        this.likelihood = likelihood;
    }
}
