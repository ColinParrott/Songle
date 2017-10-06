package colinparrott.com.songle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    private Button playButton;
    private TextView permissionsLink;
    private boolean triedToPlay = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get play button
        playButton = (Button) findViewById(R.id.btn_Play);

        // Begin new game setup on play button click
        playButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                triedToPlay = true;
                Log.d(TAG, "Play button clicked");
                setupGame();
            }
        });


        permissionsLink = (TextView) findViewById(R.id.txt_WarningPerms);

        // On click take user to Songle's app settings page to allow them to enable permissions
        permissionsLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Permissions text clicked.");

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    private void setupGame()
    {
        // Ask for location permissions if not already granted
        if (haveLocationPermission())
        {
            loadMapActivity();
        }
        else
        {
            Log.w(TAG, "Location permission NOT granted. Asking for permission and displaying settings text link.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            if(haveLocationPermission())
            {
                loadMapActivity();
            }
            else
            {
                permissionsLink.setVisibility(View.VISIBLE);
            }
        }
    }

    //
    private void loadMapActivity()
    {
        if(haveInternet())
        {
            Log.d(TAG, "Switching to maps activity");
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast toast = Toast.makeText(this, "Error. Cannot play without internet connection!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    // Returns true if app has been given Android location tracking permission; false otherwise
    private boolean haveLocationPermission()
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // Returns true if device has internet access; false otherwise
    // Code adapted from official Android docs
    private boolean haveInternet()
    {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
