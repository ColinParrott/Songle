package colinparrott.com.songle.menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import colinparrott.com.songle.game.MapsActivity;
import colinparrott.com.songle.game.obj.Song;
import colinparrott.com.songle.storage.UserPrefsManager;

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
    public static final String SONG_MSG = "com.songle.gamecreator.SONG";

    /**
     * Identifier to use with Intent for passing difficulty to MapsActivity
     */
    public static final String DIFFICULTY_MSG = "com.songle.gamecreator.DIFFICULTY";

    /**
     * Debugging tag
     */
    private static final String TAG = "GameCreator";

    /**
     * For testing purposes
     */
    private Song chosenSong;

    public GameCreator(Context context)
    {
        this.userPrefs = userPrefs;
        this.mainContext = context;
        userPrefsManager = new UserPrefsManager(context);
    }

    /**
     * Chooses a song, and calls a method to load the MapActivity
     * @param songList List of songs retrieved online
     */
    public void createGame(List<Song> songList, Difficulty difficulty)
    {
        Log.d(TAG, "createGame()");
        Song chosenSong = chooseSong(songList);
        Log.d(TAG, "Chosen: " + chosenSong.getNumber() + " " + chosenSong.getTitle());
        loadMapActivity(chosenSong, difficulty);
    }

    /**
     * Randomly Chooses a song user has not completed
     * @param songs List of songs retrieved online
     * @return Chosen song object
     */
    private Song chooseSong(List<Song> songs)
    {
        songs = new ArrayList<Song>(songs);
        int[] completedNumbers = userPrefsManager.getCompletedNumbersInt();
        Log.d(TAG, "chooseSong()");

        ArrayList<Song> tempSongs = new ArrayList<>();

        // If user has completed at least one song and user hasn't completed every song then
        // remove completed songs from list
        if(completedNumbers.length > 0 && completedNumbers.length < songs.size())
        {

            System.out.println("COMPLETED AMOUNT: " + completedNumbers.length);

            // Adds songs not in completed list to a new temporary list
            for(int i = 0; i < songs.size(); i++)
            {
                boolean completed = false;

                for(int j = 0; j < completedNumbers.length; j++)
                {
                    if(songs.get(i).getNumber() == completedNumbers[j])
                    {
                        System.out.println("REMOVE: " + songs.get(i).getTitle());
                        completed = true;
                        break;
                    }
                }

                // Add uncompleted song to new list
                if(!completed)
                {
                    tempSongs.add(songs.get(i));
                    System.out.println("ADD: " + songs.get(i).getNumber() + "\t" + songs.get(i).getArtist() + "\t" + songs.get(i).getTitle());
                }
            }

            // Copy temp list into list we'll return
            songs = (ArrayList<Song>) tempSongs.clone();

            // Debugging
            System.out.println("LEFT SONGS TO CHOOSE FROM");
            for(Song s : songs)
            {
                System.out.println(s.getNumber() + "\t" + s.getArtist() + "\t" + s.getTitle());
            }
        }

        // Returns a random song from uncompleted song list (or from all songs if user has completed EVERY song)
        chosenSong = songs.get(new Random().nextInt(songs.size()));
        return chosenSong;
    }

    /**
     * Load MapActivity and pass chosen song
     * @param song Chosen song
     */
    private void loadMapActivity(Song song, Difficulty difficulty)
    {
        // Pack song object and difficulty into an Intent and pass it to the MapsActivity
        Intent intent = new Intent(mainContext, MapsActivity.class);
        intent.putExtra(SONG_MSG, song);
        intent.putExtra(DIFFICULTY_MSG, difficulty);
        // TODO: MAKE VARIABLE FOR KEY BELOW
        intent.putExtra("resume_game", false);
        mainContext.startActivity(intent);
    }

    /**
     * Testing method for getting chosen Song
     * @return Chosen song
     */
    @VisibleForTesting
    public Song getChosenSong()
    {
        return chosenSong;
    }


}
