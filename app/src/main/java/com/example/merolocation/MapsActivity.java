
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
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.merolocation.Adapter.VechileAdapter;
import com.example.merolocation.Model.vechicelSpinner_listModel;
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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback , RoutingListener {

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mnavigationMenu;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private Spinner vechileSpinner;
    private ArrayList<vechicelSpinner_listModel> vechileCategory;
    private VechileAdapter mAdapter;
    private FloatingActionButton locationFab;
    private Double myLatitude;
    private Double myLongitude;
    private final int locationRequestCode = 0;
    private LocationManager mlocationManager;
    private LocationListener mLocationListener;
    private GoogleMap mMap;
    private String FirstZoomLocation = "Yes";
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private Boolean isGPs = false;


    ArrayList<LatLngModel> lat_lng = new ArrayList<>();
    private List<Marker> originMarkers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        polylines = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setUpToolbar();
        vechileSpinner = findViewById(R.id.select_vechiles_id);
        initList();
        mAdapter = new VechileAdapter(this, vechileCategory);


        vechileSpinner.setAdapter(mAdapter);

        vechileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {




                vechicelSpinner_listModel clickedItem = (vechicelSpinner_listModel) parent.getItemAtPosition(position);
                String clickedVechilesName = clickedItem.getmVehcileName();
                Toast.makeText(MapsActivity.this, clickedVechilesName + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    mnavigationMenu =findViewById(R.id.navigation_menu_id);
        mnavigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()

    {
        @Override
        public boolean onNavigationItemSelected (@NonNull MenuItem menuItem){
        switch (menuItem.getItemId()) {
            case R.id.exit_id:
                Toast.makeText(getApplicationContext(), "on construction hundai", Toast.LENGTH_SHORT).show();

            default:
                return false;
        }

    }
    });



        ParkingLocationLatLong();

        locationFab = findViewById(R.id.fab_id);
        locationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myLatitude!=null & myLongitude!=null) {

                    LatLng currentLocation = new LatLng(myLatitude, myLongitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
                }

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
                showRoutePath(marker.getPosition().latitude,marker.getPosition().longitude);

               /* MarkerDetailedBottomSheet bottomSheet = new MarkerDetailedBottomSheet(marker.getTitle(),marker.getPosition().latitude,marker.getPosition().longitude);
                bottomSheet.show(getSupportFragmentManager(), "detail");*/
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
                    mMap.clear();
                    addingParkingLocationMarker();
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
                isGPs=true;

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(getApplicationContext(), "onProviderDisabled", Toast.LENGTH_SHORT).show();
                isGPs=false;

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

                            if(!isGPs){
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        call();
                                    }
                                }, 5000);
                            }
                        }
                    })
                    .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!isGPs) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        call();
                                    }
                                }, 5000);
                            }
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


    private void setUpToolbar() {

        mDrawerLayout = findViewById(R.id.drawableLayout_id);
        mToolbar = findViewById(R.id.toolbar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger_icon);

    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initList() {
        vechileCategory = new ArrayList<>();
        vechileCategory.add(new vechicelSpinner_listModel("Select", R.drawable.select_ic));
        vechileCategory.add(new vechicelSpinner_listModel("Bike", R.drawable.car_ic));
        vechileCategory.add(new vechicelSpinner_listModel("Car", R.drawable.motorcycle_ic));
        vechileCategory.add(new vechicelSpinner_listModel("Cycle", R.drawable.bicycle_ic));
    }

    public void showRoutePath(Double lat,Double lon){
        Toast.makeText(getApplicationContext(),String.valueOf(lat) + "nn" +String.valueOf(lon) +String.valueOf(myLatitude) +"nn" +String.valueOf(myLongitude),Toast.LENGTH_LONG).show();

        Routing routing = new Routing.Builder().key("AIzaSyC4LSlREEMHiOUvmdB3QX8HNsTYxdren2k")
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(myLatitude,myLongitude), new LatLng(lat,lon))
                .build();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int i) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int j = 0; j <route.size(); j++) {

            //In case of more than 5 alternative routes
            int colorIndex = j % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + j * 3);
            polyOptions.addAll(route.get(j).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRoutingCancelled() {

    }

    public  void erasePolines(){
        for(Polyline line:polylines){
            line.remove();
        }
        polylines.clear();
    }
}
