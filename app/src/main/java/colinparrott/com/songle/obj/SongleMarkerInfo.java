package colinparrott.com.songle.obj;

import com.google.android.gms.maps.model.LatLng;



/**
 * A class that holds information about a lyric inside a marker. This object should be used in conjunction
 * with Marker's addTag() method
 */

public class SongleMarkerInfo
{
    /**
     * The lyric string
     */
    private String lyric;

    /**
     * LyricPointer that shows where the lyric occurs in the song
     */
    private LyricPointer lyricPointer;

    /**
     * The importance of the lyric
     */
    private WordImportance importance;

    /**
     * The latitude and longitude of the lyric retrieved from the KML map it was extracted from
     */
    private LatLng point;

    public SongleMarkerInfo(String lyric, LyricPointer lyricPointer, WordImportance wordImportance, LatLng point)
    {
        this.lyric = lyric;
        this.lyricPointer = lyricPointer;
        this.importance = wordImportance;
        this.point = point;
    }

    /**
     * Gets the lyric text
     * @return Lyric string
     */
    public String getLyric()
    {
        return lyric;
    }

    /**
     * Gets an object to locate lyric in song
     * @return LyricPointer pointing to lyric's position in song
     */
    public LyricPointer getLyricPointer()
    {
        return lyricPointer;
    }

    /**
     * Gets the importance of the lyric (how much it helps in identifying the song)
     * @return Importance of the lyric
     */
    public WordImportance getImportance()
    {
        return importance;
    }

    /**
     * Position of the lyric on the map
     * @return Latitude and longitude of lyric
     */
    public LatLng getLatLng()
    {
        return point;
    }
}
