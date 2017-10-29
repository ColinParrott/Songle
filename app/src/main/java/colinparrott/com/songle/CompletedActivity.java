package colinparrott.com.songle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import colinparrott.com.songle.downloaders.DownloadXmlTask;
import colinparrott.com.songle.obj.Song;
import colinparrott.com.songle.parsers.SongsXmlParser;
import colinparrott.com.songle.storage.UserPrefsManager;

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
                    songs = sortSongs(songs);
                }
                catch (XmlPullParserException | IOException e)
                {
                    System.out.println("[CompletedActivity] Error occurred parsing songs.");
                }

                if (songs != null)
                {
                    SongArrayAdapter listAdapter = new SongArrayAdapter(CompletedActivity.super.getApplicationContext(), R.layout.list_row, songs);
                    listView.setAdapter(listAdapter);

                    TextView completedNum = findViewById(R.id.txtViewCompleted);

                    UserPrefsManager userPrefsManager = new UserPrefsManager(getSharedPreferences("userDetails", Context.MODE_PRIVATE));
                    int[] songNums = userPrefsManager.getCompletedNumbersInt();


                    completedNum.setVisibility(View.VISIBLE);
                    completedNum.setText(songNums.length + "/" + songs.size() + " Completed");

                    ((ProgressBar) findViewById(R.id.progBarCompleted)).setVisibility(View.GONE);
                }

            }
        });

        downloadXmlTask.execute(MainActivity.URL_SONGS_XML);

    }

    private List<Song> sortSongs(List<Song> songs)
    {
        Collections.sort(songs, new Comparator<Song>()
        {
            @Override
            public int compare(Song o1, Song o2)
            {
                return o1.getNumber() < o2.getNumber() ? -1 : 1;
            }
        });

        return songs;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

}
