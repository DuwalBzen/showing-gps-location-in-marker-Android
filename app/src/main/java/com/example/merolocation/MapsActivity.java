
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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.merolocation.Adapter.VechileAdapter;
import com.example.merolocation.Model.MyItem;
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
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {


    private final String Activity = "MapsActivity";
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mnavigationMenu;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private Spinner vechileSpinner;
    private ArrayList<vechicelSpinner_listModel> vechileCategory;
    private VechileAdapter mAdapter;
    private FloatingActionButton locationFab;
    private EditText searchLocationMenu;
    private String filterVechiles;
    private String searchLocation;
    private Double myLatitude;
    private Double myLongitude;
    private final int locationRequestCode = 0;
    private LocationManager mlocationManager;
    private LocationListener mLocationListener;
    private GoogleMap mMap;
    private Boolean isGPs = false;
    ArrayList<LatLngModel> lat_lng = new ArrayList<>();
    private ClusterManager<MyItem> mClusterManager;
    private MyItem item;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpToolbar();
        initList();
        ParkingLocationLatLong();


        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        vechileSpinner = findViewById(R.id.select_vechiles_id);
        mAdapter = new VechileAdapter(this, vechileCategory);
        searchLocationMenu=findViewById(R.id.searchLocation_id);


        searchLocationMenu.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(getApplicationContext(),"You have search for " + searchLocationMenu.getText(),Toast.LENGTH_SHORT).show();

                    searchLocation= searchLocationMenu.getText().toString();
                    onMapReady(mMap);

                    return true;
                }
                return false;
            }
        });

        vechileSpinner.setAdapter(mAdapter);

        vechileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                vechicelSpinner_listModel clickedItem = (vechicelSpinner_listModel) parent.getItemAtPosition(position);
                String clickedVechilesName = clickedItem.getmVehcileName();
                //Toast.makeText(getApplicationContext(),clickedVechilesName,Toast.LENGTH_SHORT).show();

                    filterVechiles = clickedVechilesName;
                    onMapReady(mMap);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mnavigationMenu = findViewById(R.id.navigation_menu_id);
        mnavigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.exit_id:
                        Toast.makeText(getApplicationContext(), "on construction hundai", Toast.LENGTH_SHORT).show();

                    default:
                        return false;
                }

            }
        });


        locationFab = findViewById(R.id.fab_id);
        locationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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

        setUpClusterer(googleMap);
    }

    private void setUpClusterer(GoogleMap googleMap) {

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(27.671768, 85.312334), 14));
        mClusterManager = new ClusterManager<MyItem>(this, googleMap);
        mClusterManager.setRenderer(new MarkerClusterRenderer(this, googleMap, mClusterManager));
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                MarkerDetailedBottomSheet bottomSheet = new MarkerDetailedBottomSheet(item.getTitle());
                bottomSheet.show(getSupportFragmentManager(), "detail");
                return false;
            }
        });

         if(searchLocation!=null ){

             mMap.clear();
            item=searchParkingLocationMarker(searchLocation);
if(null==item){

                LatLng myLoc = new LatLng(item.getPosition().latitude, item.getPosition().longitude);
                mMap.addMarker(new MarkerOptions().position(myLoc));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));
                Log.d(Activity, filterVechiles);
                }

        }

        if (filterVechiles == "Bike") {

            mMap.clear();
            filterParkingLocationMarker(filterVechiles);
            Log.d(Activity, filterVechiles);

        }
        else if (filterVechiles == "Car") {
            mMap.clear();
            filterParkingLocationMarker(filterVechiles);
            Log.d(Activity, filterVechiles);

        } else if (filterVechiles == "Cycle") {
            mMap.clear();
            filterParkingLocationMarker(filterVechiles);
            Log.d(Activity, filterVechiles);

        }


    }

    private void addingParkingLocationMarker() {

        for (int i = 0; i < lat_lng.size(); i++) {
            MyItem cordinates = new MyItem(lat_lng.get(i).getLat(), lat_lng.get(i).getLng(), lat_lng.get(i).getName(), lat_lng.get(i).getVechileType());
            mClusterManager.addItem(cordinates);
        }

    }

    private MyItem searchParkingLocationMarker(String markerLocation) {

        MyItem cordinates = null;
        for (int i = 0; i < lat_lng.size(); i++) {

            if (lat_lng.get(i).getName().equals(markerLocation)) {

                 cordinates = new MyItem(lat_lng.get(i).getLat(), lat_lng.get(i).getLng(), lat_lng.get(i).getName(), lat_lng.get(i).getVechileType());
                mClusterManager.addItem(cordinates);

            }

        }

        return cordinates;
    }

    private void filterParkingLocationMarker(String filtervehile) {

        for (int i = 0; i < lat_lng.size(); i++) {

            if (lat_lng.get(i).getVechileType().equals(filtervehile)) {

                MyItem cordinates = new MyItem(lat_lng.get(i).getLat(), lat_lng.get(i).getLng(), lat_lng.get(i).getName(), lat_lng.get(i).getVechileType());
                mClusterManager.addItem(cordinates);

            }
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

    private void call() {
        if (!mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(this)
                    .setTitle("For a better experience , trun on the device location which use google location service.")  // GPS not found
                    .setMessage("Please enable gps") // Want to enable?

                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (!isGPs) {
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

    private void ParkingLocationLatLong() {


        LatLngModel mLatLng = new LatLngModel();
        mLatLng.setId(1);
        mLatLng.setLat(27.671768);
        mLatLng.setLng(85.312334);
        mLatLng.setName("Prera Business Center Jawalakhel");
        mLatLng.setVechileType("Car");
        lat_lng.add(mLatLng);


        mLatLng = new LatLngModel();
        mLatLng.setId(2);
        mLatLng.setLat(27.673839);
        mLatLng.setLng(85.314006);
        mLatLng.setName("Norkhang Complex Jawalakhel");
        mLatLng.setVechileType("Car");
        lat_lng.add(mLatLng);


        mLatLng = new LatLngModel();
        mLatLng.setId(4);
        mLatLng.setLat(27.673384);
        mLatLng.setLng(85.312163);
        mLatLng.setName("Jawalakhel Zoo");
        mLatLng.setVechileType("Car");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(5);
        mLatLng.setLat(27.673239);
        mLatLng.setLng(85.313355);
        mLatLng.setName("Jawalakhel Chowk");
        mLatLng.setVechileType("Car");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(6);
        mLatLng.setLat(27.672369);
        mLatLng.setLng(85.314907);
        mLatLng.setName("Jawalakhel Yuwa Club Parking");
        mLatLng.setVechileType("Bike");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(7);
        mLatLng.setLat(27.672391);
        mLatLng.setLng(85.315681);
        mLatLng.setName("City Walk Parking");
        mLatLng.setVechileType("Bike");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(8);
        mLatLng.setLat(27.675854);
        mLatLng.setLng(85.312797);
        mLatLng.setName("Jhamsikhel Roadside Parking");
        mLatLng.setVechileType("Bike");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(9);
        mLatLng.setLat(27.676833);
        mLatLng.setLng(85.317039);
        mLatLng.setName("Labim Mall Parking");
        mLatLng.setVechileType("Bike");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(10);
        mLatLng.setLat(27.678103);
        mLatLng.setLng(85.321245);
        mLatLng.setName("Patan Dhoka Parking");
        mLatLng.setVechileType("Bike");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(11);
        mLatLng.setLat(27.686100);
        mLatLng.setLng(85.316628);
        mLatLng.setName("Kupondole Parking");
        mLatLng.setVechileType("Bike");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(12);
        mLatLng.setLat(27.691320);
        mLatLng.setLng(85.316688);
        mLatLng.setName("Blue Star Complex Parking");
        mLatLng.setVechileType("Cycle");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(13);
        mLatLng.setLat(27.692172);
        mLatLng.setLng(85.314893);
        mLatLng.setName("Bhadrakali Public Parking");
        mLatLng.setVechileType("Cycle");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(14);
        mLatLng.setLat(27.694294);
        mLatLng.setLng(85.313956);
        mLatLng.setName("World Trade Centre Parking");
        mLatLng.setVechileType("Cycle");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(15);
        mLatLng.setLat(27.695241);
        mLatLng.setLng(85.310084);
        mLatLng.setName("Motorcycle Trading Workshop and Parking");
        mLatLng.setVechileType("Cycle");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(16);
        mLatLng.setLat(27.737363);
        mLatLng.setLng(85.333990);
        mLatLng.setName("Java coffee shop");
        mLatLng.setVechileType("Car");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(16);
        mLatLng.setLat(27.718981);
        mLatLng.setLng(85.317541);
        mLatLng.setName("Ambassador Hotel parking");
        mLatLng.setVechileType("Car");
        lat_lng.add(mLatLng);

        mLatLng = new LatLngModel();
        mLatLng.setId(17);
        mLatLng.setLat(27.725446);
        mLatLng.setLng(85.322281);
        mLatLng.setName("Hotel Shangri parking");
        mLatLng.setVechileType("Car");
        lat_lng.add(mLatLng);


        mLatLng = new LatLngModel();
        mLatLng.setId(18);
        mLatLng.setLat(27.730349);
        mLatLng.setLng(85.331402);
        mLatLng.setName("WWF Nepal parking");
        mLatLng.setVechileType("Car");
        lat_lng.add(mLatLng);


        mLatLng = new LatLngModel();
        mLatLng.setId(19);
        mLatLng.setLat(27.725752);
        mLatLng.setLng(85.322523);
        mLatLng.setName("The Millionaire's Club & Casino");
        mLatLng.setVechileType("Car");
        lat_lng.add(mLatLng);


        mLatLng = new LatLngModel();
        mLatLng.setId(20);
        mLatLng.setLat(27.717175);
        mLatLng.setLng(85.331331);
        mLatLng.setName("Club 25 Hours");
        mLatLng.setVechileType("Car");
        lat_lng.add(mLatLng);


        mLatLng = new LatLngModel();
        mLatLng.setId(21);
        mLatLng.setLat(27.683318);
        mLatLng.setLng(85.305849);
        mLatLng.setName("Sanepa Mall");
        mLatLng.setVechileType("Cycle");
        lat_lng.add(mLatLng);


        mLatLng = new LatLngModel();
        mLatLng.setId(21);
        mLatLng.setLat(27.665976);
        mLatLng.setLng(85.319179);
        mLatLng.setName("Mero Mall");
        mLatLng.setVechileType("Cycle");
        lat_lng.add(mLatLng);


        mLatLng = new LatLngModel();
        mLatLng.setId(21);
        mLatLng.setLat(27.716672);
        mLatLng.setLng(85.312236);
        mLatLng.setName("LOD");
        mLatLng.setVechileType("Car");
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

        vechileCategory.add(new vechicelSpinner_listModel("Bike", R.drawable.motorcycle_ic));
        vechileCategory.add(new vechicelSpinner_listModel("Car", R.drawable.car_ic));
        vechileCategory.add(new vechicelSpinner_listModel("Cycle", R.drawable.bicycle_ic));

    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


}
