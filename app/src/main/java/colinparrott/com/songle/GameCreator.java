package colinparrott.com.songle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import colinparrott.com.songle.obj.Song;

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
        loadMapActivity(chosenSong);
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
        System.out.println("chooseSong()");


//        for(Song s : songs)
//        {
//            System.out.println(s.getNumber() + "\t" + s.getArtist() + "\t" + s.getTitle());
//        }

        ArrayList<Song> tempSongs = new ArrayList<>();

        if(completedNumbers != null && completedNumbers.length < songs.size())
        {

            System.out.println("COMPLETED AMOUNT: " + completedNumbers.length);


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

                if(!completed)
                {
                    tempSongs.add(songs.get(i));
                    System.out.println("ADD: " + songs.get(i).getNumber() + "\t" + songs.get(i).getArtist() + "\t" + songs.get(i).getTitle());
                }
            }

            songs = (ArrayList<Song>) tempSongs.clone();

            System.out.println("LEFT SONGS TO CHOOSE FROM");
            for(Song s : songs)
            {
                System.out.println(s.getNumber() + "\t" + s.getArtist() + "\t" + s.getTitle());
            }
        }



        return songs.get(new Random().nextInt(songs.size()));
    }

    /**
     * Load MapActivity and pass chosen song
     * @param song Chosen song
     */
    private void loadMapActivity(Song song)
    {
        Intent intent = new Intent(mainContext, MapsActivity.class);
        intent.putExtra(SONG_MSG, song);
        mainContext.startActivity(intent);
    }


}
