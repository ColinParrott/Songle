package colinparrott.com.songle.xml;


import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * AsyncTask to download Songs.xml, calls class to parse it and returns a list of Song objects
 */

public class DownloadXmlTask extends AsyncTask<String, Void, List<Song>>
{

    private static final String TAG = "DownloadXmlTask";

    @Override
    protected List<Song> doInBackground(String... urls)
    {
        try
        {
            return loadXmlFromNetwork(urls[0]);
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

    @Override
    protected void onPostExecute(List<Song> result)
    {
        //System.out.println("Download complete\n" + result);
    }

    private List<Song> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException
    {
        List<Song> songList = null;


        try(InputStream stream = downloadUrl(urlString))
        {
            // Parse songs from InputStream
            SongsXmlParser xmlParser = new SongsXmlParser();
            songList = xmlParser.parse(stream);
        }

        return songList;
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
