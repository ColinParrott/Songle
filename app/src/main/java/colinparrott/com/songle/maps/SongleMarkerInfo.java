package colinparrott.com.songle.maps;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Colin on 11/10/2017.
 */

public class SongleMarkerInfo
{
    private String lyric;
    private LyricPointer lyricPointer;
    private WordImportance importance;
    private LatLng point;

    public SongleMarkerInfo(String lyric, LyricPointer lyricPointer, WordImportance wordImportance, LatLng point)
    {
        this.lyric = lyric;
        this.lyricPointer = lyricPointer;
        this.importance = wordImportance;
        this.point = point;
    }


    public String getLyric()
    {
        return lyric;
    }

    public LyricPointer getLyricPointer()
    {
        return lyricPointer;
    }

    public WordImportance getImportance()
    {
        return importance;
    }

    public LatLng getLatLng()
    {
        return point;
    }
}
