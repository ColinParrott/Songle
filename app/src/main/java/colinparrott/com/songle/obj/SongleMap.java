package colinparrott.com.songle.obj;

import android.support.annotation.VisibleForTesting;
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

import java.util.ArrayList;
import java.util.Iterator;

import colinparrott.com.songle.MapsActivity;
import colinparrott.com.songle.R;
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


    public SongleMap(Song song, ArrayList<SongleMarkerInfo> wordMarkers, GoogleMap map, MapsActivity mapActivity)
    {
        this.song = song;
        this.markerInfos = wordMarkers;
        this.map = map;
        this.mapActivity = mapActivity;
    }

    /**
     * Initialise lists and use supplied SongleMarkerInfos to create markers
     * and place them on the map
     */
    public void Initialise()
    {
        markers = new ArrayList<>();
        foundWords = new ArrayList<>();
        prefsManager = new UserPrefsManager(mapActivity.getApplicationContext());

        for(SongleMarkerInfo info : markerInfos)
        {
            Marker m = map.addMarker(new MarkerOptions()
                    .position(info.getLatLng())
                    .title(formatDescription(info.getImportance()))
                    .icon(BitmapDescriptorFactory.fromResource(determineMarkerIcon(info.getImportance()))));

            // Add lyric's information to marker
            m.setTag(info);

            markers.add(m);
        }

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
                SongleMarkerInfo info = (SongleMarkerInfo) m.getTag();
                System.out.println("FOUND WORD: " + info.getLyric());
                toast(info.getLyric());
                iterMarkers.remove();
                m.remove();
                foundWords.add(info);

                mapActivity.updateRemainingText(markers.size());
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
        // return true;

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

}
