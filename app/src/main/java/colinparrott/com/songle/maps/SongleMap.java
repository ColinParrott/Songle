package colinparrott.com.songle.maps;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import java.util.ArrayList;
import java.util.Iterator;
import colinparrott.com.songle.R;


/**
 * Class that handles the map once a game is created
 */

public class SongleMap
{
    /**
     * Song number this map represents
     */
    private int songNum;

    /**
     * List of SongleMarkerInfos that represent each lyric and their positions to attach to actual markers
     */
    private ArrayList<SongleMarkerInfo> markerInfos;

    /**
     * Map activity that created this instance
     */
    private Activity mapActivity;

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
    private ArrayList<String> foundWords;

    public SongleMap(int songNum, ArrayList<SongleMarkerInfo> wordMarkers, GoogleMap map, Activity mapActivity)
    {
        this.songNum = songNum;
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
                foundWords.add(info.getLyric());
            }
        }
    }

    /**
     * Placeholder method for displaying messages (i.e. captured words)
     * @param s Text to display
     */
    private void toast(String s)
    {
        Toast t = Toast.makeText(mapActivity.getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
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
    private int determineMarkerIcon(WordImportance importance)
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

}
