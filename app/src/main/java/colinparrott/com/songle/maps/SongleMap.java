package colinparrott.com.songle.maps;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import colinparrott.com.songle.MainActivity;
import colinparrott.com.songle.MapsActivity;
import colinparrott.com.songle.R;


/**
 * Created by Colin on 11/10/2017.
 */

public class SongleMap
{
    private int songNum;
    private ArrayList<SongleMarkerInfo> markerInfos;
    private Activity mapActivity;
    private GoogleMap map;

    private ArrayList<Marker> markers;

    public SongleMap(int songNum, ArrayList<SongleMarkerInfo> wordMarkers, GoogleMap map, Activity mapActivity)
    {
        this.songNum = songNum;
        this.markerInfos = wordMarkers;
        this.map = map;
        this.mapActivity = mapActivity;
    }

    public void Initialise()
    {
        markers = new ArrayList<Marker>();

        for(SongleMarkerInfo info : markerInfos)
        {
            Marker m = map.addMarker(new MarkerOptions()
                    .position(info.getLatLng())
                    .title(mapActivity.getString(formatDescription(info.getImportance())))
                    .icon(BitmapDescriptorFactory.fromResource(determineMarkerIcon(info.getImportance()))));

            m.setTag(info);

            markers.add(m);
        }
    }





    private int determineMarkerIcon(WordImportance importance)
    {
        switch (importance)
        {
            case UNCLASSIFIED:
                return R.mipmap.marker_unclassified;
            case BORING:
                return R.mipmap.marker_boring;
            case NOTBORING:
                return R.mipmap.marker_notboring;
            case INTERESTING:
                return R.mipmap.marker_interesting;
            case VERYINTERESTING:
                return R.mipmap.marker_veryinteresting;
            default:
                return R.mipmap.marker_unclassified;
        }
    }

    private int formatDescription(WordImportance desc)
    {
        switch (desc)
        {
            case UNCLASSIFIED:
                return R.string.desc_unclassified;
            case BORING:
                return R.string.desc_boring;
            case NOTBORING:
                return R.string.desc_notboring;
            case INTERESTING:
                return R.string.desc_interesting;
            case VERYINTERESTING:
                return R.string.desc_veryinteresting;
            default:
                return R.string.desc_unclassified;
        }
    }

    public int getSongNum()
    {
        return songNum;
    }

    public ArrayList<SongleMarkerInfo> getMarkerInfos()
    {
        return markerInfos;
    }

    public GoogleMap getMap()
    {
        return map;
    }

}
