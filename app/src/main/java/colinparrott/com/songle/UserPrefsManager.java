package colinparrott.com.songle;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Set;

/**
 * This class is for reading and writing data about the user's progression in the game
 * such as the list of songs they've guessed
 */


class UserPrefsManager
{

    /**
     * SharedPreferences object used to read/write data
     */
    private SharedPreferences sharedPrefs;

    UserPrefsManager(SharedPreferences sharedPrefs)
    {
        this.sharedPrefs = sharedPrefs;
    }

    /**
     * Adds song to list of completed songs in local storage via SharedPreferences
     * @param number Number of completed song
     */
    void addCompletedSong(int number)
    {

    }

    /**
     * Gets list of completed song numbers from storage
     * @return List of song numbers user has completed, null if user has completed none
     */
    int[] getCompletedNumbers()
    {
        Set<String> completedSongSet = sharedPrefs.getStringSet("completed_songs", null);

        if(completedSongSet != null)
        {
            int[] completed = new int[completedSongSet.size()];
            ArrayList<String> completedSongList = new ArrayList<String>(completedSongSet);

            for (int i = 0; i < completed.length; i++)
            {
                completed[i] = Integer.parseInt(completedSongList.get(i));
            }

            return completed;
        }

        // If "completed_songs" key is not in storage, return null as
        // this means user has not completed a song
        return null;
    }
}
