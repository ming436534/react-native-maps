package com.airbnb.android.react.maps;

import com.google.android.gms.maps.model.BitmapDescriptor;

public interface AirIconManagerCallbacks {
    void onBitmapDescriptorReady(BitmapDescriptor d, String uri);
}
