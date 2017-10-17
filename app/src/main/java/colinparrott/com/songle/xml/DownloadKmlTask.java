package colinparrott.com.songle.xml;


import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * AsyncTask to download KML file and return a String
 */

public class DownloadKmlTask extends AsyncTask<String, Void, String>
{

    /**
     * Tag for debugging
     */
    private static final String TAG = "DownloadKmlTask";


    @Override
    protected String doInBackground(String... urls)
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
    protected void onPostExecute(String result)
    {
        //System.out.println("Download complete\n" + result);
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException
    {
//        StringBuilder result = new StringBuilder();
//        List<Song> songList = null;


        try(InputStream stream = downloadUrl(urlString))
        {
            // Convert stream to String and return it
            return IOUtils.toString(stream, "utf-8");
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
