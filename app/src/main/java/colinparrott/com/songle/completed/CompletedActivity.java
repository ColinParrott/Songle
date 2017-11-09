package colinparrott.com.songle.completed;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import colinparrott.com.songle.R;
import colinparrott.com.songle.game.obj.Song;
import colinparrott.com.songle.menu.DownloadXmlTask;
import colinparrott.com.songle.menu.MainActivity;
import colinparrott.com.songle.menu.SongsXmlParser;
import colinparrott.com.songle.storage.UserPrefsManager;

/**
 * Screen for showing user the songs their progress in terms of how many songs they have guessed correctly
 */

public class CompletedActivity extends Activity
{
    /**
     * ListView for displaying songs
     */
    private ListView listView;

    /**
     * List of all songs
     */
    private List<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);

        listView = (ListView) findViewById(R.id.lstCompleted);

        final Context thisContext = getApplicationContext();

        // When Download of songs.xml is done, onFinished is called.
        DownloadXmlTask downloadXmlTask = new DownloadXmlTask(new DownloadXmlTask.TaskListener() {
            @Override
            public void onFinished(String result)
            {
                System.out.println("[onFinished]");
                // Get latest list of songs
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
                    // Set custom ArrayAdapter for list
                    SongArrayAdapter listAdapter = new SongArrayAdapter(CompletedActivity.super.getApplicationContext(), R.layout.list_row, songs);
                    listView.setAdapter(listAdapter);

                    TextView completedNum = findViewById(R.id.txtViewCompleted);

                    UserPrefsManager userPrefsManager = new UserPrefsManager(thisContext);
                    int[] songNums = userPrefsManager.getCompletedNumbersInt();

                    // Display number of songs completed
                    completedNum.setVisibility(View.VISIBLE);
                    completedNum.setText(songNums.length + "/" + songs.size() + " Completed");

                    // Hide progress spinner
                    ((ProgressBar) findViewById(R.id.progBarCompleted)).setVisibility(View.GONE);
                }

            }
        });

        // Execute task
        downloadXmlTask.execute(MainActivity.URL_SONGS_XML);

    }

    /**
     * Sorts songs by their assigned number in songs.xml (ascending)
     * @param songs List of songs to sort
     * @return Songs sorted by number (ascending order)
     */
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
