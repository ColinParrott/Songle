package colinparrott.com.songle.kml;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import colinparrott.com.songle.maps.LyricPointer;
import colinparrott.com.songle.maps.SongleMarkerInfo;
import colinparrott.com.songle.maps.WordImportance;
import colinparrott.com.songle.xml.DownloadKmlTask;

/**
 * Created by Colin on 11/10/2017.
 */

public class SongleKmlParser
{
    private static final String KML_URL_BASE = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/";
    private static final String TAG = "SongKmlParser";

    public ArrayList<SongleMarkerInfo> parse(GoogleMap map, Context context, int songNum, int mapDifficulty)
    {
        ArrayList<SongleMarkerInfo> markerInfos = new ArrayList<>();

        String songNumString = formatNumber(songNum);
        Log.d(TAG, "parse(" + songNumString + ", " + mapDifficulty + ")");
        String kmlUrl = KML_URL_BASE + songNumString + "/map" + mapDifficulty + ".kml";
        System.out.println(kmlUrl);

        String lyric;
        String strLyricPointer;
        String strDescription;
        KmlPoint point;



        try
        {
            String kmlString = new DownloadKmlTask().execute(kmlUrl).get();
            InputStream stream = new ByteArrayInputStream(kmlString.getBytes(StandardCharsets.UTF_8.name()));
            KmlLayer layer = new KmlLayer(map, stream, context.getApplicationContext());
            layer.addLayerToMap();
            LyricPointer lyricPointer;

            String lyricsStream = new DownloadLyricsTask().execute(songNumString).get();

            for (KmlContainer containers : layer.getContainers())
            {
                for(KmlPlacemark p : containers.getPlacemarks())
                {
                    strLyricPointer = p.getProperty("name");
                    strDescription = p.getProperty("description");
                    point = (KmlPoint) p.getGeometry();
                    lyricPointer = createLyricPointer(strLyricPointer);
                    lyric = LyricsParser.getLyric(lyricsStream, lyricPointer);

                  //  System.out.println(strLyricPointer + "\t" + strDescription + "\t" + "(" + point.getGeometryObject().longitude + "," + point.getGeometryObject().latitude + ")\t" + lyric);

                    SongleMarkerInfo info = new SongleMarkerInfo(lyric, lyricPointer, determineWordImportance(strDescription), point.getGeometryObject());
                    markerInfos.add(info);
                }
            }

            layer.removeLayerFromMap();
            return markerInfos;
        }
        catch (InterruptedException | ExecutionException | XmlPullParserException | IOException e)
        {
            e.printStackTrace();
        }


        return null;
    }

    private LyricPointer createLyricPointer(String s)
    {
        String parts[] = s.split(":");
        return new LyricPointer(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    private WordImportance determineWordImportance(String s)
    {
        switch (s) {
            case "unclassified":
                return WordImportance.UNCLASSIFIED;
            case "boring":
                return WordImportance.BORING;
            case "notboring":
                return WordImportance.NOTBORING;
            case "interesting":
                return WordImportance.INTERESTING;
            case "veryinteresting":
                return WordImportance.VERYINTERESTING;
            default:
                return WordImportance.UNCLASSIFIED;
        }
    }

    // Converts int to string and adds a "0" in front if number is single digit
    private String formatNumber(int num)
    {
        String numString = String.valueOf(num);

        if(num <= 9)
        {
            numString = "0" + num;
        }

        return numString;
    }

}
