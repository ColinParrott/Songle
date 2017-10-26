package colinparrott.com.songle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import colinparrott.com.songle.downloaders.DownloadXmlTask;
import colinparrott.com.songle.obj.Song;
import colinparrott.com.songle.parsers.SongsXmlParser;

/**
 * Created by s1546623 on 26/10/17.
 */

public class CompletedActivity extends Activity
{
    private ListView listView;
    private List<Song> songs;

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
                System.out.println("[onFinished]");
                SongsXmlParser songsXmlParser = new SongsXmlParser();
                try
                {
                    songs = songsXmlParser.parse(IOUtils.toInputStream(result));
                }
                catch (XmlPullParserException | IOException e)
                {
                    System.out.println("[CompletedActivity] Error occurred parsing songs.");
                }

                if (songs != null)
                {
                    SongArrayAdapter listAdapter = new SongArrayAdapter(CompletedActivity.super.getApplicationContext(), R.layout.list_row, songs);

                    listView.setAdapter(listAdapter);
                }

            }
        });

        downloadXmlTask.execute(MainActivity.URL_SONGS_XML);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

}
