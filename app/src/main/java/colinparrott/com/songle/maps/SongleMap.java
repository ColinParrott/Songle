package colinparrott.com.songle.maps;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.graphics.ColorUtils;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
    private Marker indicator;

    private static final double CAPTURE_DISTANCE = 15;

    private ArrayList<String> foundWords;

    public SongleMap(int songNum, ArrayList<SongleMarkerInfo> wordMarkers, GoogleMap map, Activity mapActivity)
    {
        this.songNum = songNum;
        this.markerInfos = wordMarkers;
        this.map = map;
        this.mapActivity = mapActivity;
    }

    public void Initialise()
    {
        markers = new ArrayList<>();
        foundWords = new ArrayList<>();

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

    public void update(LatLng playerPos)
    {
        Iterator<Marker> iterMarkers = markers.iterator();

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

    private void toast(String s)
    {
        Toast t = Toast.makeText(mapActivity.getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }

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
