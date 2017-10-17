package colinparrott.com.songle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import colinparrott.com.songle.obj.Song;
import colinparrott.com.songle.parsers.SongleKmlParser;
import colinparrott.com.songle.obj.SongleMap;
import colinparrott.com.songle.obj.SongleMarkerInfo;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    /**
     * Map object for the game
     */
    private GoogleMap mMap;

    /**
     * API client
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     *
     */
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    /**
     * Tracks whether we have been granted location permission or not
     */
    private boolean mLocationPermissionGranted = false;

    /**
     * Whether we've set the user's first ever location
     */
    private boolean setInitialLocation = false;

    /**
     * Tracks user's location
     */
    private Location mLastLocation;

    /**
     * Default latitude to load map at
     */
    private final float defaultLat = 55.9316097f;

    /**
     * Default longitude to load map at
     */
    private final float defaultLong = -3.1247421f;

    /**
     * Tag for debugging
     */
    private static final String TAG = "MapsActivity";

    /**
     * Chosen song for this game instance
     */
    private Song song;

    /**
     * SongleMap object that manipulates the map with the game logic
     */
    private SongleMap songleMap;

    /**
     * Button pressed to guess song
     */
    private Button guessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Get song object passed from GameCreator
        Intent intent = getIntent();
        song = (Song) intent.getSerializableExtra(GameCreator.SONG_MSG);

        guessButton = findViewById(R.id.btn_guess);
        guessButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("GUESS BUTTON PRESSED");
                onGuessButtonPressed();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if (mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }

    private void onGuessButtonPressed()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
        View prompt = layoutInflater.inflate(R.layout.dialog_guess, null);
        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(MapsActivity.this, R.style.AlertDialogTheme);
        promptBuilder.setView(prompt);

        final EditText editText = (EditText) prompt.findViewById(R.id.edit_guess);

        promptBuilder.setCancelable(true)
                .setPositiveButton("Guess", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        songleMap.handleGuess(editText.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", null);

        promptBuilder.create().show();
    }

    // Override back button press to make sure user wants to quit game
    @Override
    public void onBackPressed()
    {
        System.out.println("back pressed");

        // If yes go back by calling super's method if not, close window and do nothing
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Do you want to quit?");
        builder.setMessage("All progress will be lost!");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MapsActivity.super.onBackPressed();
                    }
                });

                builder.setNegativeButton("No", null);
        builder.show();
    }

    protected void createLocationRequest()
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        int permissionsCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionsCheck == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            mMap.setMyLocationEnabled(true);
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(defaultLat, defaultLong), 18f));
        loadGameMapData();

    }

    @Override
    public void onConnected(Bundle connectionHint)
    {
        try
        {
            createLocationRequest();
        }
        catch(java.lang.IllegalStateException ise)
        {
            System.out.println("IllegalStateException thrown [onConnected]");
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onConnectionSuspended(int flag)
    {
        System.out.println(">>>> onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        System.out.println(">>>> onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location current)
    {
        System.out.println("[onLocationChanged] Lat/long now: (" + String.valueOf(current.getLatitude()) + "," + String.valueOf(current.getLongitude()) + ")" );
        mLastLocation = current;

        // If we haven't set the initial location move the camera to the user's location
        if(!setInitialLocation)
        {
            LatLng currentLatLng = new LatLng(current.getLatitude(), current.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f));
            setInitialLocation = true;
        }

        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        // Call songleMap to update map
        songleMap.update(latLng);

    }

    /**
     * Creates a SongleMap object for game logic
     */
    private void loadGameMapData()
    {

        SongleKmlParser parser = new SongleKmlParser();
        ArrayList<SongleMarkerInfo> markerInfos = parser.parse(mMap, this, song.getNumber(), 5);

        songleMap = new SongleMap(song, markerInfos, mMap, this);
        songleMap.Initialise();
    }


}
