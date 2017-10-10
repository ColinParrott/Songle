package colinparrott.com.songle;

// This class is for reading and writing data about the user's progression in the game
// such as the list of songs they've guessed

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Set;

class UserPrefsManager
{

    private SharedPreferences sharedPrefs;

    UserPrefsManager(SharedPreferences sharedPrefs)
    {
        this.sharedPrefs = sharedPrefs;
    }

    void addCompletedSong(int number)
    {

    }

    int[] getCompletedNumbers()
    {
        Set<String> completedSongSet = sharedPrefs.getStringSet("completed_songs", null);

        if(completedSongSet != null)
        {
            int[] completed = new int[completedSongSet.size()];
            ArrayList<String> completedSongList = new ArrayList<String>(completedSongSet);

            for (int i = 0; i < completed.length; i++) {
                completed[i] = Integer.parseInt(completedSongList.get(i));
            }

            return completed;
        }

        return null;
    }
}
