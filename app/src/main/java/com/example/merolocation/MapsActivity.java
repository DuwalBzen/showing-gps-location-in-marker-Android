
package com.example.merolocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.merolocation.directionhelpers.FetchURL;
import com.example.merolocation.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mnavigationMenu;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private FloatingActionButton fab1_id;
    private Polyline currentPolyline;
    private Double myLatitude;
    private Double myLongitude;
    private Double myDesLatitude;
    private Double myDesLongitude;
    private final int locationRequestCode = 0;
    private LocationManager mlocationManager;
    private LocationListener mLocationListener;
    private GoogleMap mMap;
    private String FirstZoomLocation = "Yes";


    ArrayList<LatLngModel> lat_lng = new ArrayList<>();
    private List<Marker> originMarkers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpToolbar();
        mnavigationMenu=findViewById(R.id.navigation_menu_id);
        mnavigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.exit_id:
                        Toast.makeText(getApplicationContext(),"on construction hundai",Toast.LENGTH_SHORT).show();

                        default:
                            return false;
                }

            }
        });


        ParkingLocationLatLong();

        fab1_id = findViewById(R.id.fab1_id);
        fab1_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendTo_multipleMarkerActivity = new Intent(getApplicationContext()
                        , MultipleMarkerActivity.class);
                startActivity(sendTo_multipleMarkerActivity);
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        addingParkingLocationMarker();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                myDesLatitude = marker.getPosition().latitude;
                myDesLongitude = marker.getPosition().longitude;

                MarkerDetailedBottomSheet bottomSheet = new MarkerDetailedBottomSheet(marker.getTitle());
                bottomSheet.show(getSupportFragmentManager(), "detail");
                return false;
            }
        });


        mlocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (FirstZoomLocation == "Yes") {

                    myLatitude = location.getLatitude();
                    myLongitude = location.getLongitude();


                    Log.d("myloc", "Latitude " + myLatitude + " Longitude " + myLongitude);
                    mMap.clear();

                    addingParkingLocationMarker();

                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    LatLng myLoc = new LatLng(myLatitude, myLongitude);
                    mMap.addMarker(new MarkerOptions().position(myLoc).title("My current location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 16));
                    FirstZoomLocation = "No";

                } else {
                    myLatitude = location.getLatitude();
                    myLongitude = location.getLongitude();
                    Log.d("myloc", "Latitude " + myLatitude + " Longitude " + myLongitude);
                    //mMap.clear();
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    LatLng myLoc = new LatLng(myLatitude, myLongitude);
                    mMap.addMarker(new MarkerOptions().position(myLoc).title("My current location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
                }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Toast.makeText(getApplicationContext(), "onStatusChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(getApplicationContext(), "onProviderEnabled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(getApplicationContext(), "onProviderDisabled", Toast.LENGTH_SHORT).show();

                call();

            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
            } else {
                mlocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            }
        }

    }

    private void addingParkingLocationMarker() {
        for (int i = 0; i < lat_lng.size(); i++) {

            LatLng cordinates = new LatLng(lat_lng.get(i).getLat(), lat_lng.get(i).getLng());
//            Bitmap icon = BitmapFactory.decodeResource(MapsActivity.this,
//                    R.drawable.parking);
            MarkerOptions options = new MarkerOptions()
                    .position(cordinates)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(lat_lng.get(i).getName());
            mMap.addMarker(options);
        }
    }

    private void call() {
        if (!mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(this)
                    .setTitle("For a better experience , trun on the device location which use google location service.")  // GPS not found
                    // .setMessage("Please enable gps") // Want to enable?
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    call();
                                }
                            }, 5000);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mlocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            }
        }
    }


    private void ParkingLocationLatLong() {
        LatLngModel mLatLng = new LatLngModel();
        mLatLng.setId(1);
        mLatLng.setLat(27.671768);
        mLatLng.setLng(85.312334);
        mLatLng.setName("Prera Business Center Jawalakhel");
        lat_lng.add(mLatLng);


        mLatLng = new LatLngModel();
        mLatLng.setId(2);
        mLatLng.setLat(27.673839);
        mLatLng.setLng(85.314006);
        mLatLng.setName("Norkhang Complex Jawalakhel");
        lat_lng.add(mLatLng);


        mLatLng = new LatLngModel();
        mLatLng.setId(4);
        mLatLng.setLat(27.673384);
        mLatLng.setLng(85.312163);
        mLatLng.setName("Jawalakhel Zoo");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(5);
        mLatLng.setLat(27.673239);
        mLatLng.setLng(85.313355);
        mLatLng.setName("Jawalakhel Chowk");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(6);
        mLatLng.setLat(27.672369);
        mLatLng.setLng(85.314907);
        mLatLng.setName("Jawalakhel Yuwa Club Parking");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(7);
        mLatLng.setLat(27.672391);
        mLatLng.setLng(85.315681);
        mLatLng.setName("City Walk Parking");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(8);
        mLatLng.setLat(27.675854);
        mLatLng.setLng(85.312797);
        mLatLng.setName("Jhamsikhel Roadside Parking");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(9);
        mLatLng.setLat(27.676833);
        mLatLng.setLng(85.317039);
        mLatLng.setName("Labim Mall Parking");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(10);
        mLatLng.setLat(27.678103);
        mLatLng.setLng(85.321245);
        mLatLng.setName("Patan Dhoka Parking");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(11);
        mLatLng.setLat(27.686100);
        mLatLng.setLng(85.316628);
        mLatLng.setName("Kupondole Parking");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(12);
        mLatLng.setLat(27.691320);
        mLatLng.setLng(85.316688);
        mLatLng.setName("Blue Star Complex Parking");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(13);
        mLatLng.setLat(27.692172);
        mLatLng.setLng(85.314893);
        mLatLng.setName("Bhadrakali Public Parking");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(14);
        mLatLng.setLat(27.694294);
        mLatLng.setLng(85.313956);
        mLatLng.setName("World Trade Centre Parking");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(15);
        mLatLng.setLat(27.695241);
        mLatLng.setLng(85.310084);
        mLatLng.setName("Motorcycle Trading Workshop and Parking");
        lat_lng.add(mLatLng);
    }

    public void getDirection() {
        MarkerOptions orign = new MarkerOptions().position(new LatLng(myLatitude, myLongitude)).title("Location 1");
        MarkerOptions destination = new MarkerOptions().position(new LatLng(myDesLatitude, myDesLatitude)).title("Location 2");
        new FetchURL(MapsActivity.this).execute(getUrl(orign.getPosition(), destination.getPosition(), "driving"), "driving");

    }

    public String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }


    private void setUpToolbar() {

        mDrawerLayout = findViewById(R.id.drawableLayout_id);
        mToolbar = findViewById(R.id.toolbar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);




        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger_icon);

    }

}
