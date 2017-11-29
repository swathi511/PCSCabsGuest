package com.hjsoft.guestbooktaxi.model;

/**
 * Created by hjsoft on 29/12/16.
 */
public class PlaceAutoComplete {

    public CharSequence placeId;
    public CharSequence description;

    public PlaceAutoComplete(CharSequence placeId, CharSequence description) {
        this.placeId = placeId;
        this.description = description;
    }

    @Override
    public String toString() {
        return description.toString();
    }

    public CharSequence getPlaceId() {
        return placeId;
    }

    public void setPlaceId(CharSequence placeId) {
        this.placeId = placeId;
    }

    public CharSequence getDescription() {
        return description;
    }

    public void setDescription(CharSequence description) {
        this.description = description;
    }
}
