package com.airbnb.android.react.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.UUID;

public class AirClusterItem implements ClusterItem {
    private final String mID;
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;

    public AirClusterItem(String id, double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mID = id;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}