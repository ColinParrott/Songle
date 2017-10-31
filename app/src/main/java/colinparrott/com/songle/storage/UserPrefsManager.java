package colinparrott.com.songle.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is for reading and writing data about the user's progression in the game
 * such as the list of songs they've guessed
 */


public class UserPrefsManager
{

    /**
     * SharedPreferences object used to read/write data
     */
    private SharedPreferences sharedPrefs;

    /**
     * Context which called for access
     */
    private Context context;

    /**
     * Key used for storing list of completed songs
     */
    private static final String COMPLETED_SONGS_KEY = "completed_songs";

    /**
     * The key used for storing all data
     */
    private static final String USER_DETAILS_KEY = "userDetails";

    public UserPrefsManager(Context context)
    {
        this.sharedPrefs = context.getSharedPreferences(USER_DETAILS_KEY, Context.MODE_PRIVATE);
        this.context = context;
    }

    /**
     * Adds song to list of completed songs in local storage via SharedPreferences
     * @param number Number of completed song
     */
    public void addCompletedSong(int number)
    {
        Set<String> completedSongSet = sharedPrefs.getStringSet(COMPLETED_SONGS_KEY, null);

        // Adds song new to set (creates set if there wasn't one before)
        if(completedSongSet != null)
        {
            completedSongSet.add(String.valueOf(number));
        }
        else
        {
            completedSongSet = new HashSet<>();
            completedSongSet.add(String.valueOf(number));
        }

        // Store updated set into storage
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putStringSet(COMPLETED_SONGS_KEY, completedSongSet);
        editor.clear();
        editor.commit();


        // Debugging ************
        System.out.println("Added song " + number + " to completed list");
        System.out.println("Completed list: ");

        Set<String> test = sharedPrefs.getStringSet(COMPLETED_SONGS_KEY, null);

        if(test != null)
        for(String s : test)
        {
            System.out.println(s);
        }
        else
        {
            System.out.println("[ERROR] SHARED PREFS 'completed_songs' NULL WHEN IT SHOULD NOT BE");
        }
        // **********************

    }

    /**
     * Gets list of completed song numbers from storage
     * @return List of song numbers user has completed, null if user has completed none
     */
    public int[] getCompletedNumbersInt()
    {
        Set<String> completedSongSet = sharedPrefs.getStringSet(COMPLETED_SONGS_KEY, null);

        if(completedSongSet != null)
        {
            int[] ints = new int[completedSongSet.size()];
            String[] songArray = completedSongSet.toArray(new String[completedSongSet.size()]);

            for (int i = 0; i < songArray.length; i++)
            {
                ints[i] = Integer.parseInt(songArray[i]);
            }

            return ints;
        }

        // If "completed_songs" key is not in storage, return empty array as
        // this means user has not completed a song
        return new int[] {};
    }

    /**
     * Gets the completed numbers in their stored format a Set of Strings
     * @return Set of completed numbers as strings
     */
    public Set<String> getCompletedNumbersString()
    {
        return sharedPrefs.getStringSet(COMPLETED_SONGS_KEY, null);
    }
}
