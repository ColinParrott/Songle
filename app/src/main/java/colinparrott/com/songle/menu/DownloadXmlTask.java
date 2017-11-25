package colinparrott.com.songle.menu;


import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask to download Songs.xml, calls class to parse it and returns a list of Song objects.
 * Callback implementation adapted from: https://stackoverflow.com/questions/26202568/android-pass-function-reference-to-asynctask
 */

public class DownloadXmlTask extends AsyncTask<String, Void, String>
{

    private static final String TAG = "DownloadXmlTask";
    private String fileName;

    public interface TaskListener
    {
        public void onFinished(String result);
    }

    private final TaskListener taskListener;

    public DownloadXmlTask(TaskListener listener)
    {
        this.taskListener = listener;
    }


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
        super.onPostExecute(result);

        if(this.taskListener != null)
        {
            this.taskListener.onFinished(result);
        }
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
