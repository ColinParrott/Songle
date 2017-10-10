package colinparrott.com.songle;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import colinparrott.com.songle.xml.Song;

/**
 * Created by Colin on 10/10/2017.
 */

public class GameCreator
{

    private SharedPreferences userPrefs;
    private UserPrefsManager userPrefsManager;

    public GameCreator(SharedPreferences userPrefs)
    {
        this.userPrefs = userPrefs;
        userPrefsManager = new UserPrefsManager(userPrefs);
    }

    public void createGame(List<Song> songList)
    {

        for(Song s : songList)
        {
            System.out.println(s.getNumber() + "\t" + s.getTitle() + "\t" + s.getArtist() + "\t" + s.getLink() + "\n");
        }

        Song chosenSong = chooseSong(songList);
        System.out.println("Chosen: " + chosenSong.getTitle());

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



}
