package com.example.merolocation;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MultipleMarkerActivity extends FragmentActivity implements OnMapReadyCallback  {
    Button getDirection;
    private GoogleMap mMap;
    private Polyline currentPolyline;

    private LatLng tokyo = new LatLng(35.6804, 139.7690);
    private LatLng shenzhen = new LatLng(22.5431, 114.0579);
    private LatLng moscow = new LatLng(55.7558, 37.6173);
    private LatLng thailand = new LatLng(15.8700, 100.9925);

    private MarkerOptions place1, place2;

    private Marker tokyoMarker;
    private Marker shenzenMarker;
    private Marker moscowMarker;
    private Marker thailandMarker;
    private List<Marker> MarkerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_marker);
        MarkerList=new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        getDirection = findViewById(R.id.btnGetDirection);

        place1 = new MarkerOptions().position(new LatLng(27.658143, 85.3199503)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(27.667491, 85.3208583)).title("Location 2");


        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(place1);
        mMap.addMarker(place2);

        /*tokyoMarker= mMap.addMarker(new MarkerOptions().position(tokyo).title("Traveling Tokyo"));
        MarkerList.add(tokyoMarker);
        shenzenMarker= mMap.addMarker(new MarkerOptions().position(shenzhen).title("Traveling Shenzen"));
        MarkerList.add(shenzenMarker);
        moscowMarker= mMap.addMarker(new MarkerOptions().position(moscow).title("Traveling Moscow"));
        MarkerList.add(moscowMarker);
        thailandMarker= mMap.addMarker(new MarkerOptions().position(thailand).title("Traveling Thailand").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        MarkerList.add(moscowMarker);

        for(Marker marker:MarkerList){
            LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            mMap.addMarker(new MarkerOptions().position(latLng));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        }*/


       /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MarkerDetailedBottomSheet bottomSheet=new MarkerDetailedBottomSheet(marker.getTitle() );
                bottomSheet.show(getSupportFragmentManager(),"detail");
                return false;
            }
        });*/



    }


}
