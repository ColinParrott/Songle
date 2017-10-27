package colinparrott.com.songle.parsers;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import colinparrott.com.songle.downloaders.DownloadLyricsTask;
import colinparrott.com.songle.obj.LyricPointer;
import colinparrott.com.songle.obj.SongleMarkerInfo;
import colinparrott.com.songle.obj.WordImportance;
import colinparrott.com.songle.downloaders.DownloadKmlTask;

/**
 * Class that downloads and parses KML files
 */

public class SongleKmlParser
{
    /**
     * Base url of all KML map files
     */
    private static final String KML_URL_BASE = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/";

    /**
     * Tag for debugging
     */
    private static final String TAG = "SongKmlParser";

    /**
     * Creates list of SongleMarkerInfos after downloading and parsing a KML map file
     * @param map Map of MapsActivity
     * @param context Context of MapsActivity
     * @param songNum Song number to download/parse map for
     * @param mapDifficulty Difficulty (1-5) of map to download
     * @return List of SongleMarkerInfos representing each Placemark in the KML file
     */
    public ArrayList<SongleMarkerInfo> parse(GoogleMap map, Context context, int songNum, int mapDifficulty)
    {

        ArrayList<SongleMarkerInfo> markerInfos = new ArrayList<>();

        // Format number to at least 2 digits for use in URL
        String songNumString = formatNumber(songNum);
        Log.d(TAG, "parse(" + songNumString + ", " + mapDifficulty + ")");

        // Create URL to KML file for specified song and difficulty
        String kmlUrl = KML_URL_BASE + songNumString + "/map" + mapDifficulty + ".kml";
       // System.out.println(kmlUrl);

        // Attributes to extract from each Placemark
        String lyric;
        WordImportance wordImportance;
        LyricPointer lyricPointer;
        LatLng point;


        try
        {
            // Download KML as string and convert it to in InputStream
            String kmlString = new DownloadKmlTask().execute(kmlUrl).get();
            InputStream stream = new ByteArrayInputStream(kmlString.getBytes(StandardCharsets.UTF_8.name()));

            // Below is a "hacky" way to access the Placemarks in the KML file. We place the markers
            // on the map only so they can be parsed using KmlLayer's built in methods. We remove the KmlLayer
            // once the we've extracted the information from the Placemarks

            // Add KmlLayer to map so we can parse it
            KmlLayer layer = new KmlLayer(map, stream, context.getApplicationContext());
            layer.addLayerToMap();

            // Store lyrics of song in string
            String lyricsStream = new DownloadLyricsTask().execute(songNumString).get();


            // Loop through each placemark and extract the relevant information
            for (KmlContainer containers : layer.getContainers())
            {
                for(KmlPlacemark p : containers.getPlacemarks())
                {
                    // Extract lyric position and convert to LyricPointer object
                    String strLyricPointer = p.getProperty("name");
                    lyricPointer = createLyricPointer(strLyricPointer);

                    // Extract importance of lyric specified by this Placemark
                    String strImportance = p.getProperty("description");
                    wordImportance = determineWordImportance(strImportance);


                    // Extract latitude and longitude object
                    point = (LatLng) p.getGeometry().getGeometryObject();

                    // Get lyric Placemarker refers to
                    lyric = LyricsParser.getLyric(lyricsStream, lyricPointer);

                    //  System.out.println(strLyricPointer + "\t" + strDescription + "\t" + "(" + point.getGeometryObject().longitude + "," + point.getGeometryObject().latitude + ")\t" + lyric);

                    // Create SongleMakerInfo object with extracted information for current Placemark and add it to the list
                    SongleMarkerInfo info = new SongleMarkerInfo(lyric, lyricPointer, wordImportance, point);
                    markerInfos.add(info);
                }
            }

            // Remove KmlLayer from map
            layer.removeLayerFromMap();

            return markerInfos;
        }
        catch (InterruptedException | ExecutionException | XmlPullParserException | IOException e)
        {
            e.printStackTrace();
        }

        // Return null if something goes wrong
        return null;
    }

    /**
     * Creates LyricPointer object from string pointer
     * @param s The location string
     * @return LyricPointer object from the string
     */
    private LyricPointer createLyricPointer(String s)
    {
        String parts[] = s.split(":");
        return new LyricPointer(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    /**
     * Gets a WordImportance enum from the strings used in the KML files
     * @param s Kml importance string
     * @return WordImportance enum
     */
    private WordImportance determineWordImportance(String s)
    {
        switch (s)
        {
            case "unclassified":
                return WordImportance.UNCLASSIFIED;
            case "boring":
                return WordImportance.BORING;
            case "notboring":
                return WordImportance.NOT_BORING;
            case "interesting":
                return WordImportance.INTERESTING;
            case "veryinteresting":
                return WordImportance.VERY_INTERESTING;
            default:
                return WordImportance.UNCLASSIFIED;
        }
    }

    /**
     * Pads ints to at least a two digit string by adding a 0 in front if needed
     * @param num Number to format
     * @return Number in string format, with a 0 in front if single digit
     */
    public static String formatNumber(int num)
    {
        String numString = String.valueOf(num);

        if(num <= 9)
        {
            numString = "0" + num;
        }

        return numString;
    }

}
