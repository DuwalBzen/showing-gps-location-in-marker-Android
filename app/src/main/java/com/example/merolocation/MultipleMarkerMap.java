package com.example.merolocation;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MultipleMarkerMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng tokyo = new LatLng(35.6804, 139.7690);
    private LatLng shenzhen = new LatLng(22.5431, 114.0579);
    private LatLng moscow = new LatLng(55.7558, 37.6173);

    private Marker tokyoMarker;
    private Marker shenzhenMarker;
    private Marker moscowMarker;
    private ArrayList<Marker>   markerArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_marker_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        markerArrayList = new ArrayList<>();

        tokyoMarker = mMap.addMarker(new MarkerOptions()
                .position(tokyo)
                .title("Tokyo travel")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

         markerArrayList.add(tokyoMarker);

        shenzhenMarker = mMap.addMarker(new MarkerOptions()
                .position(shenzhen)
                .title("shenzen travel")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        );
        markerArrayList.add(shenzhenMarker);

        moscowMarker = mMap.addMarker(new MarkerOptions()
                .position(moscow)
                .title("moscow travel")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        markerArrayList.add(moscowMarker);

        for (Marker marker : markerArrayList) {
            LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4));
        }
    }
}
