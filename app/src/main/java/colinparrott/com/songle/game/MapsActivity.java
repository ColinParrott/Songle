package colinparrott.com.songle.game;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import colinparrott.com.songle.R;
import colinparrott.com.songle.game.obj.FoundWordsArrayAdapter;
import colinparrott.com.songle.game.obj.GameStateKey;
import colinparrott.com.songle.game.obj.Song;
import colinparrott.com.songle.game.obj.SongleMarkerInfo;
import colinparrott.com.songle.game.parsers.SongleKmlParser;
import colinparrott.com.songle.menu.Difficulty;
import colinparrott.com.songle.menu.GameCreator;
import colinparrott.com.songle.menu.MainActivity;
import colinparrott.com.songle.storage.UserPrefsManager;

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
     * Code for accessing location permission
     */
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

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
    private final double defaultLat = 55.9444f;

    /**
     * Default longitude to load map at
     */
    private final double defaultLong = -3.18884f;

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

    /**
     * Button pressed to view collected words
     */
    private Button viewWordsButton;

    /**
     * Spinner used to sort collected words list
     */
    private Spinner sortSpinner;

    /**
     * TextView that shows remaining words(markers) left to collect
     */
    private TextView remainingText;

    /**
     * View that displays found words
     */
    private View wordsPrompt;

    /**
     * Have we returned to a game instance user was in before
     */
    private boolean resumedGame;

    /**
     * Object for accessing stored data
     */
    private UserPrefsManager prefsManager;

    /**
     * To be used with GSON library when storing/retrieving serialised lists for markerInfos and foundWords
     */
    private static Type gsonListType = new TypeToken<List<SongleMarkerInfo>>(){}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Lock to portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Get intent telling us if this activity should load a previously played instance
        Intent intent = getIntent();
        resumedGame = intent.getBooleanExtra("resume_game", false);

        // Get song from GameCreator if it's a fresh game
        if(!resumedGame)
        {
            // Get song object passed from GameCreator
            song = (Song) intent.getSerializableExtra(GameCreator.SONG_MSG);
        }

        // Change boolean in storage that tracks if a game is in-progress
        prefsManager = new UserPrefsManager(this);
        prefsManager.setGameInProgress(true);

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

        viewWordsButton = findViewById(R.id.btn_ViewWords);
        viewWordsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("VIEW WORDS BUTTON PRESSED");
                onViewWordsButtonPressed();
            }
        });



        remainingText = findViewById(R.id.txt_Remaining);

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

    /**
     * Set up the spinner for selecting the sort method when viewing found words
     */
    private void setupSpinner()
    {

        List<String> categories = new ArrayList<String>();

        categories.add(getString(R.string.txt_SortAlphabetical));
        categories.add(getString(R.string.txt_SortImportance));
        categories.add(getString(R.string.txt_SortSongOccurs));

        // Set spinner's contents
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.songle_spinner_item, categories);
        sortSpinner.setAdapter(dataAdapter);
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

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause()");

        // Save current game state
        saveGameState();
    }

    /**
     * Save the necessary information for reloading this map instance in storage
     */
    private void saveGameState()
    {
        if(songleMap != null)
        {
            UserPrefsManager prefsManager = new UserPrefsManager(this);
            prefsManager.saveObject(GameStateKey.MARKER_INFOS.name(), songleMap.getMarkerInfos(), gsonListType);
            prefsManager.saveObject(GameStateKey.SONG.name(), song,  Song.class);
            prefsManager.saveObject(GameStateKey.FOUND_WORDS.name(), songleMap.getFoundWords(), gsonListType);
            prefsManager.saveObject(GameStateKey.DIFFICULTY.name(), songleMap.getDifficulty(), Difficulty.class);
        }
    }

    /**
     * Displays a dialog telling user their guess was correct along with
     * the song's title, artist and a clickable YouTube link.
     */
    private void onCorrectGuess()
    {

        // Change in-progress boolean in storage to prevent us from trying to reload this map instance after it has been completed
        prefsManager.setGameInProgress(false);

        // Create and display prompt
        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
        View prompt = layoutInflater.inflate(R.layout.dialog_guess_success, null);
        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(MapsActivity.this, R.style.AlertDialogTheme);
        promptBuilder.setView(prompt);

        promptBuilder.setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        loadMenu();
                    }
                });
        promptBuilder.create().show();


        // Set song information on prompt
        ((TextView) prompt.findViewById(R.id.textViewTitle)).setText(song.getTitle());
        ((TextView) prompt.findViewById(R.id.textViewArtist)).setText(song.getArtist());
        TextView linkText = prompt.findViewById(R.id.textViewURL);
        linkText.setText(song.getLink());

        // Makes YouTube link clickable and opens it in YouTube app / browser depending on user's preferences
        linkText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(song.getLink()));
                startActivity(browserIntent);
            }
        });

    }

    /**
     * Loads main menu activity
     */
    private void loadMenu()
    {
        // Add intent so that MainActivity knows it was started by this activity
        Intent goToMenu = new Intent(getApplicationContext(), MainActivity.class);
        goToMenu.putExtra("calling_activity", "MapsActivity");
        startActivity(goToMenu);
    }

    /**
     * Displays dialog showing words user has found
     */
    private void onViewWordsButtonPressed()
    {
        // Inflate dialog
        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
        wordsPrompt = layoutInflater.inflate(R.layout.dialog_foundwords, null);
        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(MapsActivity.this, R.style.AlertDialogTheme);
        promptBuilder.setView(wordsPrompt);

        sortSpinner = (Spinner) wordsPrompt.findViewById(R.id.spinnerSortWords);

        AlertDialog dialog = promptBuilder.create();
        dialog.show();

        final sortCategory sort = sortCategory.ALPHABETICAL;
        final View prompt = wordsPrompt;

        // Change listener for sorting spinner
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        populateListView(prompt, sortCategory.ALPHABETICAL);
                        Log.d(TAG, "Sorting found words ALPHABETICALLY");
                        break;
                    case 1:
                        populateListView(prompt, sortCategory.IMPORTANCE);
                        Log.d(TAG, "Sorting found words by IMPORTANCE");
                        break;
                    case 2:
                        populateListView(prompt, sortCategory.SONG_ORDER);
                        Log.d(TAG, "Sorting found words by SONG_ORDER");
                        break;
                    default:
                        populateListView(prompt, sortCategory.ALPHABETICAL);
                        Log.w(TAG, "Sorting spinner somehow didn't return one of the 3 expected values: 0,1,2");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        setupSpinner();
        populateListView(wordsPrompt, sortCategory.ALPHABETICAL);
    }

    /**
     * Updates list view showing found words
     * @param prompt View to update
     * @param category Category to sort words by
     */
    private void populateListView(View prompt, sortCategory category)
    {
        Log.w(TAG, "populateListView() - updating found words view");

        FoundWordsArrayAdapter listAdapter = new FoundWordsArrayAdapter(this, R.layout.words_list_row, sortFoundWords(songleMap.getFoundWords(), category));
        ListView listView = (ListView) prompt.findViewById(R.id.lstFound);

        // Set ListView to use updated listAdapter
        listView.setAdapter(listAdapter);
    }

    /**
     * Get list of SongleMarkerInfos sorted by the specified category
     * @param words Words (markers) to sort
     * @param cat Category to sort by
     * @return List of sorted SongleMarkerInfos
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public List<SongleMarkerInfo> sortFoundWords(List<SongleMarkerInfo> words, final sortCategory cat)
    {
        Log.d(TAG, "SORTING WORD LIST BY: " + cat.name());

        Collections.sort(words, new Comparator<SongleMarkerInfo>()
        {
            @Override
            public int compare(SongleMarkerInfo o1, SongleMarkerInfo o2)
            {
                if(cat == sortCategory.ALPHABETICAL)
                {
                    return o1.getLyric().toLowerCase().compareTo(o2.getLyric().toLowerCase());
                }
                else if(cat == sortCategory.IMPORTANCE)
                {
                    // Reverse order to get most important first
                    return o1.getImportance().compareTo(o2.getImportance()) * -1;
                }
                // Sort by line number, if there's a tie then sort by word number
                else
                {
                    if(o1.getLyricPointer().getLineNumber() > o2.getLyricPointer().getLineNumber())
                    {
                        return 1;
                    }
                    else if(o1.getLyricPointer().getLineNumber() < o2.getLyricPointer().getLineNumber())
                    {
                        return -1;
                    }
                    else
                    {
                        if(o1.getLyricPointer().getWordNumber() > o2.getLyricPointer().getWordNumber())
                        {
                            return 1;
                        }
                        else if(o1.getLyricPointer().getWordNumber() < o2.getLyricPointer().getWordNumber())
                        {
                            return -1;
                        }
                        else
                        {
                            return 0;
                        }
                    }
                }
            }
        });

        return words;
    }

    /**
     * Displays dialog with an input field where user can enter their guess
     * for the current song.
     */
    private void onGuessButtonPressed()
    {
        Log.d(TAG, "onGuessButtonPressed()");

        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
        final View prompt = layoutInflater.inflate(R.layout.dialog_guess, null);
        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(MapsActivity.this, R.style.AlertDialogTheme);
        promptBuilder.setView(prompt);

        final EditText editText = (EditText) prompt.findViewById(R.id.edit_guess);

        promptBuilder.setPositiveButton("Guess", null);
        promptBuilder.setNegativeButton("Cancel", null);

        final AlertDialog alertDialog = promptBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(final DialogInterface dialog)
            {
                Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                positive.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String userGuess = editText.getText().toString();
                        boolean correct = songleMap.handleGuess(userGuess);

                        Log.d(TAG, "User guessed: " + userGuess);

                        if(correct)
                        {
                            Log.d(TAG, "CORRECT guess");

                            // Hides keyboard if guess correct before dialog pops up (keyboard stays up behind dialog if this is not done)
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                            alertDialog.dismiss();
                            onCorrectGuess();
                        }
                        else
                        {
                            Log.d(TAG, "INCORRECT guess");

                            // Change dialog title to "incorrect"
                            TextView statusText = prompt.findViewById(R.id.textView);
                            statusText.setText(R.string.txt_Incorrect);
                            statusText.setTextColor(getResources().getColor(R.color.colorError));

                            // Shake "incorrect" text
                            Animation a = AnimationUtils.loadAnimation(MapsActivity.super.getApplicationContext(), R.anim.shake);
                            statusText.startAnimation(a);

                            // Vibrational feedback
                            vibrateDevice(50L);

                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    /**
     * Vibrates device if it has one
     * @param duration Duration of vibration
     */
    private void vibrateDevice(long duration)
    {
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(duration);
    }



    // Override back button to ask user if they want to save and quit, wipe progress and quit or resume
    @Override
    public void onBackPressed()
    {
        Log.d(TAG, "Back pressed");

        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
        final View prompt = layoutInflater.inflate(R.layout.dialog_quit_map, null);
        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(MapsActivity.this, R.style.AlertDialogTheme);
        promptBuilder.setView(prompt);

        final Button saveButton = (Button) prompt.findViewById(R.id.btnSave);
        final Button resetButton = (Button) prompt.findViewById(R.id.btnReset);
        final Button resumeButton = (Button) prompt.findViewById(R.id.btnResume);

        final AlertDialog alertDialog = promptBuilder.create();
        final Activity mapsActivity = this;
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            // Quit (leaving in-progress boolean unchanged (true) so that we resume this instance next time)
            @Override
            public void onShow(final DialogInterface dialog)
            {
                saveButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                        MapsActivity.super.onBackPressed();
                    }
                });

                // Display reset confirmation dialog
                resetButton.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        displayResetConfirmationDialog(alertDialog);
                    }
                });

                // Close dialog
                resumeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();

    }

    /**
     * Dialog that pop ups to make sure the user actually wants to reset their progress
     * @param parentDialog Pass parent dialog so we can close it
     */
    private void displayResetConfirmationDialog(final AlertDialog parentDialog)
    {
        // Create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle(getString(R.string.txt_ResetTitle));
        builder.setMessage(getString(R.string.txt_ResetWarning));

        // Add listener to positive button ("yes" to reset)
        builder.setPositiveButton(getString(R.string.txt_YesReset), new DialogInterface.OnClickListener()
        {
           // Set in-progress boolean to false to stop us from trying to load a saved game from storage next time
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                UserPrefsManager u = new UserPrefsManager(MapsActivity.super.getApplicationContext());
                u.setGameInProgress(false);
                parentDialog.dismiss();
                MapsActivity.super.onBackPressed();
            }
        });

        // No button simply closes the dialog
        builder.setNegativeButton(getString(R.string.txt_NoReset), null);

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

        // Show user location if we have permission; if not ask for it
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            mMap.setMyLocationEnabled(true);
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Move map camera to default position close to University buildings where KML maps are based around
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(defaultLat, defaultLong), 18f));

        // Unwrap difficulty and load map with it
        Intent i = getIntent();
        Difficulty d = (Difficulty) i.getSerializableExtra(GameCreator.DIFFICULTY_MSG);
        loadGameMapData(d);

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

        // Show dialog forcing user to go back to main menu since we've hit an unrecoverable error
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setCancelable(false);
        builder.setTitle("An unresolvable error has occurred");
        builder.setMessage("You will be returned to the main menu.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                MapsActivity.super.onBackPressed();
            }
        });

        builder.show();
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
    private void loadGameMapData(Difficulty diff)
    {
        // Download and parse KML map if we're creating a new game instance
        if(!resumedGame)
        {
            SongleKmlParser parser = new SongleKmlParser();
            ArrayList<SongleMarkerInfo> markerInfos = parser.parse(mMap, this, song.getNumber(), diff.ordinal() + 1);
            songleMap = new SongleMap(song, markerInfos, mMap, diff, this);
        }
        // Retrieve stored game data if we're resuming a previous game instance
        else
        {
            ArrayList<SongleMarkerInfo> markerInfos = prefsManager.retrieveObject(GameStateKey.MARKER_INFOS.name(), gsonListType);
            ArrayList<SongleMarkerInfo> foundWords = prefsManager.retrieveObject(GameStateKey.FOUND_WORDS.name(), gsonListType);
            song = prefsManager.retrieveObject(GameStateKey.SONG.name(), Song.class);
            Difficulty d = prefsManager.retrieveObject(GameStateKey.DIFFICULTY.name(), Difficulty.class);
            songleMap = new SongleMap(song, markerInfos, mMap, d, this, foundWords);
            Log.d(TAG, "Restoring SongleMap with song: " + song.getNumber() + "\t" + song.getTitle());
        }


        songleMap.Initialise();
    }

    /**
     * Updates the text showing how many words(markers) are left to find
     * @param i Number of markers left
     */
    public void updateRemainingText(int i)
    {
        remainingText.setText(String.valueOf(i) + " words left");
    }

    /**
     * Updates found words list
     */
    public void updateFoundWordsView()
    {

        if(sortSpinner != null && wordsPrompt.isShown())
        {
            populateListView(wordsPrompt, sortCategory.values()[sortSpinner.getSelectedItemPosition()]);
        }
    }

    /**
     * Categories to sort found words by
     */
    @VisibleForTesting
    public enum sortCategory
    {
        ALPHABETICAL,
        IMPORTANCE,
        SONG_ORDER
    }

    @VisibleForTesting
    public SongleMap getSongleMap()
    {
        return songleMap;
    }


}
