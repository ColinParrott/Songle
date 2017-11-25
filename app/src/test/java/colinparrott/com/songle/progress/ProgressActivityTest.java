package colinparrott.com.songle.progress;


import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;

import colinparrott.com.songle.game.obj.Song;

import static org.junit.Assert.assertEquals;

/**
 * Created by Colin on 25/11/2017.
 */
public class ProgressActivityTest
{

    @Mock
    private static ProgressActivity progressActivity = Mockito.mock(ProgressActivity.class);

    private static ArrayList<Song> songs = new ArrayList<Song>();
    private static ArrayList<Song> trueSort = new ArrayList<Song>();
    private static ArrayList<Song> trueCompletedSort = new ArrayList<Song>();

    @BeforeClass
    public static void initialise()
    {
        // Make sure we use actual sort method
        Mockito.when(progressActivity.sortSongs(songs)).thenCallRealMethod();

        // Create base list to test and shuffle for randomness
        songs.add(new Song(3, "Lou Reed", "Perfect Day", "x"));
        songs.add(new Song(1, "Madonna", "Vogue", "x"));
        songs.add(new Song(4, "David Bowie", "Life on Mars?", "x"));
        songs.add(new Song(2, "Blondie", "Heart of Glass", "x"));
        Collections.shuffle(songs);

        // Desired order when sorting by number
        trueSort.add(new Song(1, "Madonna", "Vogue", "x"));
        trueSort.add(new Song(2, "Blondie", "Heart of Glass", "x"));
        trueSort.add(new Song(3, "Lou Reed", "Perfect Day", "x"));
        trueSort.add(new Song(4, "David Bowie", "Life on Mars?", "x"));


        //////////////////////////////////////////////////////////////////////////////////

        // Make sure we use the actual sortByCompleted method
        Mockito.when(progressActivity.sortByCompleted(songs)).thenCallRealMethod();

        // Mock isCompleted method to pretend that these songs have been completed before by the user
        Mockito.when(progressActivity.isCompleted(songs.get(1))).thenReturn(true);  // 1 - Vogue - Madonna
        Mockito.when(progressActivity.isCompleted(songs.get(0))).thenReturn(true);  // 3 - Perfect Day - Lou Reed

        // Mock isCompleted method to pretend that these songs have NOT been completed before by the user
        Mockito.when(progressActivity.isCompleted(songs.get(2))).thenReturn(false); // 4 - David Bowie - Life on Mars?
        Mockito.when(progressActivity.isCompleted(songs.get(3))).thenReturn(false); // 2 - Heart of Glass - Blondie

        // Desired order when sorted by completed (then by number on ties)
        trueCompletedSort.add(new Song(1, "Madonna", "Vogue", "x"));
        trueCompletedSort.add(new Song(3, "Lou Reed", "Perfect Day", "x"));
        trueCompletedSort.add(new Song(2, "Blondie", "Heart of Glass", "x"));
        trueCompletedSort.add(new Song(4, "David Bowie", "Life on Mars?", "x"));

    }


    // Check sorting by song number method is correct
    @Test
    public void sortSongsCorrect() throws Exception
    {
        for(int i = 0; i < songs.size(); i++)
        {
            assertEquals(progressActivity.sortSongs(songs).get(i).getNumber(), trueSort.get(i).getNumber());
        }
    }

    // Check sorting by completed method is correct
    @Test
    public void sortByCompletedCorrect() throws Exception
    {
        for(int i = 0; i < songs.size(); i++)
        {
            assertEquals(progressActivity.sortByCompleted(songs).get(i).getNumber(), trueCompletedSort.get(i).getNumber());
        }
    }

}