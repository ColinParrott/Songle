package colinparrott.com.songle.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;
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
     * Key used for checking in-progress state
     */
    private static final String GAME_IN_PROGRESS_KEY = "in_progress";


    /**
     * The key used for storing all data
     */
    private static final String USER_DETAILS_KEY = "userDetails";

    /**
     * Debugging tag
     */
    private static final String TAG = "UserPrefsManager";

    public UserPrefsManager(Context context)
    {
        this.context = context;
        this.sharedPrefs = context.getSharedPreferences(USER_DETAILS_KEY, Context.MODE_PRIVATE);

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

    /**
     * Sets the last game in-progress value
     * @param inProgress is a game in-progress
     */
    public void setGameInProgress(boolean inProgress)
    {
        Log.d(TAG, "SET GAME IN PROGRESS: " + inProgress);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(GAME_IN_PROGRESS_KEY, inProgress);
        editor.commit();
    }

    /**
     * Save an object in storage as a string through serialization (GSON library)
     * @param key Key to store object in
     * @param obj The actual object to store
     * @param t The type of the object
     */
    public void saveObject(String key, Object obj, Type t)
    {
      //  Log.d(TAG, "Saving: " + key);
        Gson gson = new Gson();
        String json = gson.toJson(obj, t);

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(key, json);
        editor.commit();
        Log.d(TAG, "Successfully saved: " + key);
    }

    /**
     * Retrieve an object from storage via deserialization
     * @param key Key of object to retrieve
     * @param t Type to return object in
     * @param <T> Type to return object in
     * @return Object from storage
     */
    public <T> T retrieveObject(String key, Type t)
    {
        Gson gson = new Gson();

        if(sharedPrefs.contains(key))
        {
            String json = sharedPrefs.getString(key, null);
            System.out.println(json);
            return gson.fromJson(json, t);
        }
        else
        {
            Log.e(TAG, key + " NOT CONTAINED IN SHARED PREFS");
            return null;
        }
    }

    /**
     * Gets the last game in-progress value
     * @return True if game was in-progress; false otherwise
     */
    public boolean isGameInProgress()
    {
        Log.d(TAG, "Contains in_progress key: " + sharedPrefs.contains(GAME_IN_PROGRESS_KEY));
        return sharedPrefs.getBoolean(GAME_IN_PROGRESS_KEY, false);
    }
}
