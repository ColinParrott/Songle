package colinparrott.com.songle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import colinparrott.com.songle.xml.DownloadXmlTask;
import colinparrott.com.songle.xml.Song;

public class MainActivity extends AppCompatActivity
{

    private Button playButton;
    private TextView permissionsLink;
    private boolean triedToPlay = false;
    private static final String TAG = "MainActivity";

    private static final String URL_SONGS_XML = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml";
    private static final String PREFS_NAME = "userDetails";

    private static GameCreator gameCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        if (!haveLocationPermission())
        {
            Log.w(TAG, "Location permission NOT granted. Asking for permission and displaying settings text link.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Check internet
        if(haveInternet())
        {
            List<Song> songs = null;

            try
            {
                songs = new DownloadXmlTask().execute(URL_SONGS_XML).get();
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }

            if(songs != null)
            {
                Log.d(TAG, "Successfully retrieved and parsed songs.xml");

                gameCreator = new GameCreator(this, getSharedPreferences(PREFS_NAME, MODE_PRIVATE));
                gameCreator.createGame(songs);
            }

        }
        else
        {
            showWarningMessage("Error. Cannot play without internet connection!");
        }


    }

    private void showWarningMessage(String msg)
    {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    // Override permission request callback to seamlessly continue to load the xml data and start a new game
    // without the user having to press 'Play' again
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        System.out.println("[onRequestPermissionsResult]:\t" + permissions[0]);

        // Only if location permission was granted
        if(permissions[0].equals("android.permission.ACCESS_FINE_LOCATION"))
        {
            if (requestCode == 1)
            {
                setupGame();
            }
            else
            {
                permissionsLink.setVisibility(View.VISIBLE);
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
