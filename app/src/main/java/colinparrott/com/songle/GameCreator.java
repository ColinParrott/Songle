package colinparrott.com.songle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Random;

import colinparrott.com.songle.xml.Song;

/**
 * Class called by MainActivity deals with creating a new game
 */

public class GameCreator
{

    /**
     * Local storage used to store persistent data for the app
     */
    private SharedPreferences userPrefs;

    /**
     * Object to handle with reading/writing to local storage
     */
    private UserPrefsManager userPrefsManager;

    /**
     * Context that called this, so we can load the MapsActivity
     */
    private Context mainContext;


    /**
     * Identifier to use with the Intent for passing the selected song number to the MapsActivity
     */
    public static final String SONG_NUM_MSG = "com.songle.gamecreator.SONGNUM";

    public GameCreator(Context context, SharedPreferences userPrefs)
    {
        this.userPrefs = userPrefs;
        this.mainContext = context;
        userPrefsManager = new UserPrefsManager(userPrefs);
    }

    /**
     * Chooses a song, and calls a method to load the MapActivity
     * @param songList List of songs retrieved online
     */
    public void createGame(List<Song> songList)
    {
        Song chosenSong = chooseSong(songList);
        System.out.println("Chosen: " + chosenSong.getNumber() + " " + chosenSong.getTitle());
        loadMapActivity(chosenSong.getNumber());
    }

    /**
     * Randomly Chooses a song user has not completed
     * @param songs List of songs retrieved online
     * @return Chosen song object
     */
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

    /**
     * Load MapActivity and pass chosen song
     * @param songNum Number of chosen song
     */
    private void loadMapActivity(int songNum)
    {
        Intent intent = new Intent(mainContext, MapsActivity.class);
        intent.putExtra(SONG_NUM_MSG, songNum);
        mainContext.startActivity(intent);
    }


}
