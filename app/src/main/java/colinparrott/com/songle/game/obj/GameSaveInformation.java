package colinparrott.com.songle.game.obj;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import colinparrott.com.songle.menu.Difficulty;
import colinparrott.com.songle.storage.UserPrefsManager;

/**
 * Class for holding and obtaining information in storage about a saved game
 */

public class GameSaveInformation
{
    /**
     * Number of found words in save
     */
    private int numFoundWords;

    /**
     * Number of words (markers) left to be found in save
     */
    private int numWordsRemaining;

    /**
     * Difficulty game save is on
     */
    private Difficulty difficulty;

    /**
     * Duration user has been on MapsActivity in milliseconds
     */
    private long playTime;

    /**
     * Epoch time (milliseconds) of time game was last saved
     */
    private long lastSaveTime;

    /**
     * Context used to access data in storage
     */
    private Context context;

    /**
     * To be used with GSON library when storing/retrieving serialised lists for markerInfos and foundWords
     */
    private static Type gsonListType = new TypeToken<List<SongleMarkerInfo>>(){}.getType();

    public GameSaveInformation(Context c)
    {
        this.context = c;
    }

    /**
     * Obtain and store save data
     */
    public void initialise()
    {
        UserPrefsManager userPrefsManager = new UserPrefsManager(context);

        ArrayList<SongleMarkerInfo> foundWords = userPrefsManager.retrieveObject(GameStateKey.FOUND_WORDS.name(), gsonListType);
        numFoundWords = foundWords.size();

        ArrayList<SongleMarkerInfo> markerInfos = userPrefsManager.retrieveObject(GameStateKey.MARKER_INFOS.name(), gsonListType);
        numWordsRemaining = markerInfos.size() - numFoundWords;

        difficulty = userPrefsManager.retrieveObject(GameStateKey.DIFFICULTY.name(), Difficulty.class);
        playTime = userPrefsManager.retrieveObject(GameStateKey.TIME_PLAYED.name(), long.class);
        lastSaveTime = userPrefsManager.retrieveObject(GameStateKey.TIME_OF_LAST_SAVE.name(), long.class);
    }

    /**
     * Returns play time in a nicely formatted string
     * @return Play time string in <x>h <y>m <z>s format
     */
    public String getPlayTimeFormatted()
    {
        Duration d = Duration.ofMillis(playTime);
        return d.toHours() + "h " + d.toMinutes() + "m " + d.toMillis()/1000 + "s";
    }

    /**
     * Returns time of last save as a formatted string
     * @return Save time in dd/MM/yy HH:mm:ss format
     */
    public String getSaveTimeFormatted()
    {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.UK);
        return formatter.format(lastSaveTime);
    }

    /**
     * Gets number of founds words in saved instance
     * @return Number of found words
     */
    public int getNumFoundWords() {
        return numFoundWords;
    }

    /**
     * Gets number of words left to be found in saved instance
     * @return Number of words left to be found
     */
    public int getNumWordsRemaining() {
        return numWordsRemaining;
    }

    /**
     * Gets difficulty saved game was created on
     * @return Difficulty of saved instance
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Gets the time played of the saved instance in milliseconds
     * @return Time played (milliseconds)
     */
    public long getPlayTime() {
        return playTime;
    }

    /**
     * Gets the epoch time the game was last saved
     * @return Epoch time of last save (milliseconds)
     */
    public long getLastSaveTime() {
        return lastSaveTime;
    }
}
