package colinparrott.com.songle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import colinparrott.com.songle.downloaders.DownloadXmlTask;
import colinparrott.com.songle.obj.Difficulty;
import colinparrott.com.songle.obj.Song;
import colinparrott.com.songle.parsers.SongsXmlParser;

public class MainActivity extends Activity
{

    /**
     * Play button object
     */
    private Button playButton;

    /**
     * Completed button object
     */
    private Button completedButton;

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
    public static final String URL_SONGS_XML = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml";

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
    private SeekBar diffBar;

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

        completedButton = (Button) findViewById(R.id.btn_Completed);

        completedButton.setOnClickListener(new View.OnClickListener()
                                           {
                                               @Override
                                               public void onClick(View v)
                                               {
                                                   Log.d(TAG, "Completed button clicked");
                                                   Intent intent = new Intent(MainActivity.super.getApplicationContext(), CompletedActivity.class);
                                                   startActivity(intent);
                                               }
                                           });

        diffBar = (SeekBar) findViewById(R.id.diffSeek);
        diffBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorVeryEasy), PorterDuff.Mode.MULTIPLY);

        ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
        thumb.getPaint().setColor(getResources().getColor(R.color.colorAccent));
        thumb.setIntrinsicHeight(60);
        thumb.setIntrinsicWidth(60);
        diffBar.setThumb(thumb);

        diffBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                updateDifficultyText(getDifficulty(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

    }

    private void updateDifficultyText(Difficulty difficulty)
    {
        TextView txtDiffculty = findViewById(R.id.txt_Difficulty);
        TextView txtDesc = findViewById(R.id.txt_DifficultyDesc);

        switch (difficulty)
        {
            case VERY_EASY:
                txtDiffculty.setText(R.string.txt_VeryEasy);
                txtDiffculty.setTextColor(getResources().getColor(R.color.colorVeryEasy));
                diffBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorVeryEasy), PorterDuff.Mode.MULTIPLY);
                txtDesc.setText(R.string.txt_VeryEasyDesc);
                break;
            case EASY:
                txtDiffculty.setText(R.string.txt_Easy);
                txtDiffculty.setTextColor(getResources().getColor(R.color.colorEasy));
                diffBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorEasy), PorterDuff.Mode.MULTIPLY);
                txtDesc.setText(R.string.txt_EasyDesc);
                break;
            case MODERATE:
                txtDiffculty.setText(R.string.txt_Moderate);
                txtDiffculty.setTextColor(getResources().getColor(R.color.colorModerate));
                diffBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorModerate), PorterDuff.Mode.MULTIPLY);
                txtDesc.setText(R.string.txt_ModerateDesc);
                break;
            case HARD:
                txtDiffculty.setText(R.string.txt_Hard);
                txtDiffculty.setTextColor(getResources().getColor(R.color.colorHard));
                diffBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorHard), PorterDuff.Mode.MULTIPLY);
                txtDesc.setText(R.string.txt_HardDesc);
                break;
            case VERY_HARD:
                txtDiffculty.setText(R.string.txt_VeryHard);
                txtDiffculty.setTextColor(getResources().getColor(R.color.colorVeryHard));
                diffBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorVeryHard), PorterDuff.Mode.MULTIPLY);
                txtDesc.setText(R.string.txt_VeryHardDesc);
                break;
        }
    }

    @Override
    protected void onResume()
    {
        System.out.println("ON RESUME");
        progressBar.setVisibility(View.INVISIBLE);
        super.onResume();
    }

    // Stop user going back into a map after they've completed it
    @Override
    public void onBackPressed()
    {
        String caller = getIntent().getStringExtra("calling_activity");
        System.out.println("CALLER: " + caller);

        if (caller != null) {
            if (!caller.equals("MapsActivity")) {
                super.onBackPressed();
            }
        }
        else {
            super.onBackPressed();
        }
    }

    /**
     * Checks if we have location permission and internet connection before continuing.
     * Once satisfied uses GameCreator to continue.
     *
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
            if (haveInternet())
            {

                Log.d(TAG, "Internet available begin songs.xml download");
                try
                {
                    progressBar.setVisibility(View.VISIBLE);
                    String songsXmlData = new DownloadXmlTask(null).execute(URL_SONGS_XML).get();

                    initialiseGameCreator(songsXmlData, getDifficulty(diffBar.getProgress()));
                }
                catch (InterruptedException | ExecutionException e)
                {
                    System.out.println("[MainActivity.setupGame()] Failed to download songs.xml data");
                }


            }
            else
            {

                // Display snackbar saying there's no internet connection, allowing user to retry
                System.out.println("Display no internet snackbar");
                Snackbar connBar = Snackbar.make(findViewById(R.id.constraint_layout), R.string.txt_NoInternet, Snackbar.LENGTH_INDEFINITE);

                // Change snackbar background colour
                ((View) connBar.getView()).setBackgroundColor(getResources().getColor(R.color.colorPrimaryMedium));


                connBar.setAction(R.string.txt_Retry, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        setupGame();
                    }
                });

                connBar.show();
            }

        }

    }

    private Difficulty getDifficulty(int progress)
    {
        switch (progress)
        {
            case 0:
                return Difficulty.VERY_EASY;
            case 1:
                return Difficulty.EASY;
            case 2:
                return Difficulty.MODERATE;
            case 3:
                return Difficulty.HARD;
            case 4:
                return Difficulty.VERY_HARD;
            default:
                return Difficulty.VERY_EASY;
        }
    }

    private void initialiseGameCreator(String data, Difficulty chosenDifficulty)
    {
        SongsXmlParser xmlParser = new SongsXmlParser();
        List<Song> songs = null;
        InputStream stream = IOUtils.toInputStream(data);


        try
        {
            songs = xmlParser.parse(stream);

            if (songs != null)
            {
                Log.d(TAG, "Successfully retrieved and parsed songs.xml");

                gameCreator = new GameCreator(this, getSharedPreferences(PREFS_NAME, MODE_PRIVATE));
                gameCreator.createGame(songs, chosenDifficulty);
            }

        }
        catch (XmlPullParserException | IOException e)
        {
            e.printStackTrace();
        }

    }


    /**
     * Override permission request callback to seamlessly continue to load the xml data and start a new game
     * without the user having to press 'Play' again
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        System.out.println("[onRequestPermissionsResult]:\t" + permissions[0]);


        if (permissions[0].equals("android.permission.ACCESS_FINE_LOCATION")) {
            // Setup game if user permits
            System.out.println("GRANT RESULTS: " + grantResults[0]);
            if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupGame();
            }
            // Display snackbar with button that takes user to Songle's app settings where they can enable the permission.
            // This is needed on API 26 where the user can permanently deny the location permission by checking the "don't ask again box"
            else {
                System.out.println("Display permission snackbar");
                Snackbar permsBar = Snackbar.make(findViewById(R.id.constraint_layout), R.string.txt_Permissions, Snackbar.LENGTH_INDEFINITE);

                // Change snackbar background colourr
                ((View) permsBar.getView()).setBackgroundColor(getResources().getColor(R.color.colorPrimaryMedium));

                permsBar.setAction(R.string.txt_PermsEnable, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // Takes user to Songle's app settings page
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

                permsBar.show();

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * Checks if we location permission
     *
     * @return True if permission granted; false otherwise
     */
    private boolean haveLocationPermission()
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Checks if we have internet access (adapted from official Android docs)
     *
     * @return True if connection available; false otherwise
     */
    private boolean haveInternet()
    {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
