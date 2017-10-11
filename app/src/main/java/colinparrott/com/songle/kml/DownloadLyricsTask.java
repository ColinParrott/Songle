package colinparrott.com.songle.kml;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import colinparrott.com.songle.xml.Song;

/**
 * Created by Colin on 11/10/2017.
 */

public class DownloadLyricsTask extends AsyncTask<String, Void, String>
{

    private static final String TAG = "DownloadLyricsTask";
    private static final String BASE_LYRICS_URL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/";

    @Override
    protected String doInBackground(String... nums)
    {
        try
        {
            return loadLyricsFromNetwork(BASE_LYRICS_URL + nums[0] + "/words.txt");
        }
        catch (IOException e)
        {
            Log.e(TAG, "Unable to load content. Check your network connection");
            return null;
        }
        catch (XmlPullParserException e)
        {
            Log.e(TAG, "Error parsing XML");
            return null;
        }
    }

    private String loadLyricsFromNetwork(String urlString) throws XmlPullParserException, IOException
    {
        StringBuilder result = new StringBuilder();


        try(InputStream stream = downloadUrl(urlString))
        {
            String lyricsString = IOUtils.toString(stream, "utf-8");
            return lyricsString;
        }

    }


    private InputStream downloadUrl(String urlString) throws IOException
    {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        conn.connect();
        return conn.getInputStream();
    }
}
