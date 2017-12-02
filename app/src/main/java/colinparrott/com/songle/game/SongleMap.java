package colinparrott.com.songle.game;

import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import colinparrott.com.songle.R;
import colinparrott.com.songle.game.obj.GameStateKey;
import colinparrott.com.songle.game.obj.Song;
import colinparrott.com.songle.game.obj.SongleMarkerInfo;
import colinparrott.com.songle.game.obj.WordImportance;
import colinparrott.com.songle.menu.Difficulty;
import colinparrott.com.songle.storage.UserPrefsManager;


/**
 * Class that handles the map once a game is created
 */

public class SongleMap
{
    /**
     * Song this map represents
     */
    private Song song;

    /**
     * List of SongleMarkerInfos that represent each lyric and their positions to attach to actual markers
     */
    private ArrayList<SongleMarkerInfo> markerInfos;

    /**
     * Map activity that created this instance
     */
    private MapsActivity mapActivity;

    /**
     * Map object of the map activity to manipulate
     */
    private GoogleMap map;

    /**
     * List of markers representing each lyric
     */
    private ArrayList<Marker> markers;

    /**
     * For debugging. An indicator marker that shows the closest marker to the user
     */
    private Marker indicator;

    /**
     * Difficulty of this map
     */
    private Difficulty difficulty;

    /**
     * How close the user has to be to a marker in order to consider that word collected
     */
    private static final double CAPTURE_DISTANCE = 15;

    /**
     * List of words the user has found so far
     */
    private ArrayList<SongleMarkerInfo> foundWords;

    /**
     * Deal with persistent storage
     */
    private UserPrefsManager prefsManager;

    /**
     * Debugging tag
     */
    private final String TAG = "SongleMap";

    /**
     * Know whether we're playing a completely new game instance or one from a previous time
     */
    private boolean resumedGame;


    /**
     * Constructor when creating a new game
     * @param song Song to start new instance on
     * @param markerInfos SongleMarkerInfos to attach to markers on the map
     * @param map Map object to modify
     * @param mapActivity MapActivity that created this SongleMap instance
     */
    public SongleMap(Song song, ArrayList<SongleMarkerInfo> markerInfos, GoogleMap map, Difficulty difficulty, MapsActivity mapActivity)
    {
        this.song = song;
        this.markerInfos = markerInfos;
        this.map = map;
        this.difficulty = difficulty;
        this.mapActivity = mapActivity;
        this.resumedGame = false;
    }

    /**
     * Constructor when resuming a previous game instance
     * @param song Song from a previous instance to resume with
     * @param markerInfos SongleMarkerInfos from a previous game instance
     * @param map Map object to modify
     * @param mapActivity MapActivity that created this SongleMap instance
     */
    public SongleMap(Song song, ArrayList<SongleMarkerInfo> markerInfos, GoogleMap map, Difficulty difficulty, MapsActivity mapActivity, ArrayList<SongleMarkerInfo> foundWords)
    {
        this.song = song;
        this.markerInfos = markerInfos;
        this.map = map;
        this.difficulty = difficulty;
        this.mapActivity = mapActivity;
        this.foundWords = foundWords;
        this.resumedGame = true;
    }

    /**
     * Initialise lists and use supplied SongleMarkerInfos to create markers
     * and place them on the map
     */
    public void Initialise()
    {
        markers = new ArrayList<>();

        // Initialise foundWords list if we're creating a new game (it was null as the resume constructor was not used)
        if(foundWords == null)
        {
            foundWords = new ArrayList<>();
        }

        for(SongleMarkerInfo info : markerInfos)
        {
            // Only markers to map if they haven't been found before (needed for when a game is resumed)
            if(!markerInFoundWords(info))
            {
                Marker m = map.addMarker(new MarkerOptions()
                        .position(info.getLatLng())
                        .title(formatDescription(info.getImportance()))
                        .icon(BitmapDescriptorFactory.fromResource(determineMarkerIcon(info.getImportance()))));

                // Add lyric's information to marker
                m.setTag(info);
                markers.add(m);
            }
        }

       // mapActivity.updateFoundWordsView();
        prefsManager = new UserPrefsManager(mapActivity.getApplicationContext());
        mapActivity.updateRemainingText(markers.size());

    }

    /**
     * This is called by MapsActivity every time the player's location changes.
     * It essentially acts as the game's tick function.
     * @param playerPos New longitude and latitude of player
     */
    public void update(LatLng playerPos)
    {
        Iterator<Marker> iterMarkers = markers.iterator();

        // Loop through markers and if distance from user to marker is less than or equal
        // to CAPTURE_DISTANCE then remove and marker, show lyric and add to list of found words
        while(iterMarkers.hasNext())
        {
            Marker m = iterMarkers.next();
            double dist = SphericalUtil.computeDistanceBetween(playerPos, m.getPosition());

            if(dist <= CAPTURE_DISTANCE)
            {
                // Add to found words
                SongleMarkerInfo info = (SongleMarkerInfo) m.getTag();
                foundWords.add(info);
                Log.d(TAG, "FOUND WORD: " + info.getLyric());

                // Display word in a toast
                toast(info.getLyric());

                // Remove marker from map and Iterator
                iterMarkers.remove();
                m.remove();


                // Update remaining text and update found words view
                mapActivity.updateRemainingText(markers.size());
                mapActivity.updateFoundWordsView();
            }
        }
    }

    /**
     * Placeholder method for displaying messages (i.e. captured words)
     * @param s Text to display
     */
    private void toast(String s)
    {
        LayoutInflater inflater = mapActivity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.word_toast, (ViewGroup) mapActivity.findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(s);

        Toast toast = new Toast(mapActivity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, -750);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Debugging method that updates the location of the indicator marker
     * @param m New marker to place
     */
    private void placeIndicator(Marker m)
    {
        if(indicator != null)
        {
            indicator.remove();
        }

        indicator = map.addMarker(new MarkerOptions()
                .position(m.getPosition())
                .title("CLOSEST")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .zIndex(1f));
    }


    /**
     * Gets an int pointer to the image of the marker icon for a specified word importance
     * @param importance Importance of the word
     * @return Int pointer to marker icon of respective word importance
     */
    public static int determineMarkerIcon(WordImportance importance)
    {
        switch (importance)
        {
            case UNCLASSIFIED:
                return R.mipmap.marker_unclassified;
            case BORING:
                return R.mipmap.marker_boring;
            case NOT_BORING:
                return R.mipmap.marker_notboring;
            case INTERESTING:
                return R.mipmap.marker_interesting;
            case VERY_INTERESTING:
                return R.mipmap.marker_veryinteresting;
            default:
                return R.mipmap.marker_unclassified;
        }
    }

    public ArrayList<SongleMarkerInfo> getFoundWords()
    {
        return foundWords;
    }

    /**
     * Gets the word importance in a clean, readable string
     * @param desc Importance of word
     * @return Readable word importance string
     */
    private String formatDescription(WordImportance desc)
    {
        switch (desc)
        {
            case UNCLASSIFIED:
                return mapActivity.getString(R.string.desc_unclassified);
            case BORING:
                return mapActivity.getString(R.string.desc_boring);
            case NOT_BORING:
                return mapActivity.getString(R.string.desc_notboring);
            case INTERESTING:
                return mapActivity.getString(R.string.desc_interesting);
            case VERY_INTERESTING:
                return mapActivity.getString(R.string.desc_veryinteresting);
            default:

                return mapActivity.getString(R.string.desc_unclassified);
        }
    }

    /**
     * Handles user's guess and updates stored completed list if correct AND returns whether guess was deemed correct or not
     * @param songGuessed String user guessed
     * @return True if guess correct, false otherwise
     */
    public boolean handleGuess(String songGuessed)
    {
        boolean correct = guessCorrect(songGuessed);

        System.out.println("USER GUESSED: " + songGuessed);
        System.out.println("GUESS CORRECT: " + correct);

        if(correct)
        {
            prefsManager.addCompletedSong(song.getNumber());
        }

        return correct;
    }

    /**
     * Checks if a string is a close enough match to the actual song title
     * @param guess String to check
     * @return True if deemed a good match, false otherwise
     */
    private boolean guessCorrect(String guess)
    {

        String actualSong = normaliseString(song.getTitle());
        guess = normaliseString(guess);

        return guess.equals(actualSong);

    }

    /**
     * Save the necessary information for reloading this map instance in storage
     */
    public void saveGameState(boolean leavingGame, long timeOfOnResume, Type markerInfosListType)
    {
        UserPrefsManager prefsManager = new UserPrefsManager(mapActivity);
        prefsManager.saveObject(GameStateKey.SONG.name(), song,  Song.class);
        prefsManager.saveObject(GameStateKey.MARKER_INFOS.name(), getMarkerInfos(), markerInfosListType);
        prefsManager.saveObject(GameStateKey.FOUND_WORDS.name(), getFoundWords(), markerInfosListType);
        prefsManager.saveObject(GameStateKey.DIFFICULTY.name(), getDifficulty(), Difficulty.class);

        if(!leavingGame)
        {
            Long timePlayed = prefsManager.retrieveObject(GameStateKey.TIME_PLAYED.name(), long.class);

            if (timePlayed == null) {
                timePlayed = 0L;
            }

            long newTimePlayed = timePlayed + (System.currentTimeMillis() - timeOfOnResume);

            prefsManager.saveObject(GameStateKey.TIME_PLAYED.name(), newTimePlayed, long.class);
            prefsManager.saveObject(GameStateKey.TIME_OF_LAST_SAVE.name(), System.currentTimeMillis(), long.class);
            Log.d(TAG, "Time played: " + newTimePlayed + "ms.");
        }
    }

    private boolean markerInFoundWords(SongleMarkerInfo info)
    {
        for(SongleMarkerInfo i : foundWords)
        {
            if(i.getLyricPointer().getLineNumber() == info.getLyricPointer().getLineNumber() && i.getLyricPointer().getWordNumber() == info.getLyricPointer().getWordNumber())
            {
                return  true;
            }
        }

        return false;
    }

    /**
     * Removes leading and trailing whitespace, punctuation and spaces from Strings
     * @param s String to format
     * @return Normalised string for lenient comparisons
     */
    @VisibleForTesting
    public static String normaliseString(String s)
    {
        return s.replaceAll("\\(.*?\\)","") // Remove text between parentheses and the parentheses themselves (https://stackoverflow.com/questions/5636048/regular-expression-to-replace-content-between-parentheses)
                .replaceAll("\\p{P}", "") // Remove punctuation
                .replace(" ", "") // Remove all spaces
                .toLowerCase() // Make lowercase
                .trim(); // Trim should be unneeded but no harm
    }

    /**
     * Get difficulty of this map
     * @return The difficulty
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @VisibleForTesting
    public ArrayList<Marker> getMarkers()
    {
        return markers;
    }

    @VisibleForTesting
    public Song getSong()
    {
        return song;
    }

    public ArrayList<SongleMarkerInfo> getMarkerInfos() {
        return markerInfos;
    }


}
