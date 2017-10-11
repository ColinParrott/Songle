package colinparrott.com.songle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Random;

import colinparrott.com.songle.xml.Song;

/**
 * Created by Colin on 10/10/2017.
 */

public class GameCreator
{

    private SharedPreferences userPrefs;
    private UserPrefsManager userPrefsManager;

    private Context mainContext;


    public static final String SONG_NUM_MSG = "com.songle.gamecreator.SONGNUM";

    public GameCreator(Context context, SharedPreferences userPrefs)
    {
        this.userPrefs = userPrefs;
        this.mainContext = context;
        userPrefsManager = new UserPrefsManager(userPrefs);
    }

    public void createGame(List<Song> songList)
    {

//        for(Song s : songList)
//        {
//            System.out.println(s.getNumber() + "\t" + s.getTitle() + "\t" + s.getArtist() + "\t" + s.getLink() + "\n");
//        }

        Song chosenSong = chooseSong(songList);
        System.out.println("Chosen: " + chosenSong.getNumber() + " " + chosenSong.getTitle());
        loadMapActivity(chosenSong.getNumber());

    }

    private Song chooseSong(List<Song> songs)
    {
        Song song = null;
        int[] completedNumbers = userPrefsManager.getCompletedNumbers();


        if(completedNumbers != null)
        {
            // remove completed songs from list and select new song from remaining ones
        }
        else
        {
            return songs.get(new Random().nextInt(songs.size()));
        }

        return song;
    }

    //
    private void loadMapActivity(int songNum)
    {
        Intent intent = new Intent(mainContext, MapsActivity.class);
        intent.putExtra(SONG_NUM_MSG, songNum);
        mainContext.startActivity(intent);
    }


}
