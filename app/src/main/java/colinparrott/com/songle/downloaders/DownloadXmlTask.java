package colinparrott.com.songle.downloaders;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask to download Songs.xml, calls class to parse it and returns a list of Song objects
 */

public class DownloadXmlTask extends AsyncTask<String, Void, String>
{

    private static final String TAG = "DownloadXmlTask";
    private String fileName;
    private Context context;


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

        String result = null;

        try(InputStream stream = downloadUrl(urlString))
        {
            result = IOUtils.toString(stream);
        }

        return result;
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
