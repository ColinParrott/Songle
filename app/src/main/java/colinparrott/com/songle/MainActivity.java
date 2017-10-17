package colinparrott.com.songle;

import android.Manifest;
import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import colinparrott.com.songle.downloaders.DownloadXmlTask;
import colinparrott.com.songle.obj.Song;

public class MainActivity extends Activity
{

    /**
     * Play button object
     */
    private Button playButton;

    /**
     * ProgressBar object
      */
    private ProgressBar progressBar;

    /**
     * TextView to click when permission requests have been disabled by user
     */
    private TextView permissionsLink;

    /**
     * If user has hit play yet
     */
    private boolean triedToPlay = false;

    /**
     * Tag for debugging
     */
    private static final String TAG = "MainActivity";

    /**
     * Url to songs xml file
     */
    private static final String URL_SONGS_XML = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml";

    /**
     * Key used to store data in persistent storage
     */
    private static final String PREFS_NAME = "userDetails";

    /**
     * Object to deal with setting up a new game
     */
    private static GameCreator gameCreator;

    /**
     *
     */
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        progressBar = (ProgressBar) findViewById(R.id.progBar);

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

    @Override
    protected void onResume()
    {
        progressBar.setVisibility(View.INVISIBLE);
        super.onResume();
    }

    /**
     * Checks if we have location permission and internet connection before continuing.
     * Once satisfied uses GameCreator to continue.
     */
    private void setupGame()
    {
        // Ask for location permissions if not already granted
        if (!haveLocationPermission())
        {
            Log.w(TAG, "Location permission NOT granted. Asking for permission and displaying settings text link.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            // Check internet
            if(haveInternet())
            {
                List<Song> songs = null;

                progressBar.setVisibility(View.VISIBLE);

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


    }

    /**
     * Shows warning toast
     * @param msg Text to display
     */
    private void showWarningMessage(String msg)
    {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }


    /**
     * Override permission request callback to seamlessly continue to load the xml data and start a new game
     * without the user having to press 'Play' again
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        System.out.println("[onRequestPermissionsResult]:\t" + permissions[0]);

        // Only if location permission was granted
        if(permissions[0].equals("android.permission.ACCESS_FINE_LOCATION"))
        {
            System.out.println("GRANT RESULTS: " + grantResults[0]);
            if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED)
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


    /**
     * Checks if we location permission
     * @return True if permission granted; false otherwise
     */
    private boolean haveLocationPermission()
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Checks if we have internet access (adapated from official Android docs)
     * @return True if connection available; false otherwise
     */
    private boolean haveInternet()
    {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
