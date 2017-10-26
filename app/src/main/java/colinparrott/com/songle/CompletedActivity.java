package colinparrott.com.songle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import colinparrott.com.songle.downloaders.DownloadXmlTask;
import colinparrott.com.songle.obj.Song;
import colinparrott.com.songle.parsers.SongsXmlParser;

/**
 * Created by s1546623 on 26/10/17.
 */

public class CompletedActivity extends Activity
{
    private ListView listView;
    private ArrayList<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);

        listView = (ListView) findViewById(R.id.lstCompleted);

        DownloadXmlTask downloadXmlTask = new DownloadXmlTask(new DownloadXmlTask.TaskListener() {
            @Override
            public void onFinished(String result)
            {
                SongsXmlParser songsXmlParser = new SongsXmlParser();
                try
                {
                    songs = (ArrayList<Song>) songsXmlParser.parse(IOUtils.toInputStream(result));
                }
                catch (XmlPullParserException | IOException e)
                {
                    System.out.println("[CompletedActivity] Error occurred parsing songs.");
                }

                if (songs != null)
                {
                    for(Song s : songs)
                    {
                        // TODO
                    }
                }

            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

}
