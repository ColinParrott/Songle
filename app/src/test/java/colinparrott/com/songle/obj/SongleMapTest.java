package colinparrott.com.songle.obj;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import colinparrott.com.songle.R;
import colinparrott.com.songle.game.MapsActivity;
import colinparrott.com.songle.game.SongleMap;
import colinparrott.com.songle.game.obj.LyricPointer;
import colinparrott.com.songle.game.obj.SongleMarkerInfo;
import colinparrott.com.songle.game.obj.WordImportance;

import static org.junit.Assert.assertEquals;


public class SongleMapTest
{
    private static List<SongleMarkerInfo> infos = new ArrayList<SongleMarkerInfo>();

    @BeforeClass
    public static void initialise()
    {
        // Populate base SongleMarkerInfo list
        infos.add(new SongleMarkerInfo("absolute", new LyricPointer(1, 1), WordImportance.BORING, null));
        infos.add(new SongleMarkerInfo("banana", new LyricPointer(1, 5), WordImportance.NOT_BORING, null));
        infos.add(new SongleMarkerInfo("figure", new LyricPointer(20, 5), WordImportance.VERY_INTERESTING, null));
        infos.add(new SongleMarkerInfo(",", new LyricPointer(20, 4), WordImportance.INTERESTING, null));
        infos.add(new SongleMarkerInfo("Hello", new LyricPointer(16, 7), WordImportance.UNCLASSIFIED, null));
        infos.add(new SongleMarkerInfo("Wonderwall", new LyricPointer(9, 10), WordImportance.UNCLASSIFIED, null));
        infos.add(new SongleMarkerInfo("absolutely", new LyricPointer(3, 8), WordImportance.BORING, null));
    }

    // Checks the correct marker icons are returned
    @Test
    public void determineMarkerIconCorrect() throws Exception
    {
        assertEquals(R.mipmap.marker_unclassified, SongleMap.determineMarkerIcon(WordImportance.UNCLASSIFIED));
        assertEquals(R.mipmap.marker_boring, SongleMap.determineMarkerIcon(WordImportance.BORING));
        assertEquals(R.mipmap.marker_notboring, SongleMap.determineMarkerIcon(WordImportance.NOT_BORING));
        assertEquals(R.mipmap.marker_interesting, SongleMap.determineMarkerIcon(WordImportance.INTERESTING));
        assertEquals(R.mipmap.marker_veryinteresting, SongleMap.determineMarkerIcon(WordImportance.VERY_INTERESTING));
    }

    // Tests string normalisation method used for guess comparisons
    @Test
    public void normaliseStringCorrect() throws Exception
    {
        // Basic case
        assertEquals("hallelujah", SongleMap.normaliseString("Hallelujah"));

        // Punctuation
        assertEquals("anotherbrickinthewallparttwo", SongleMap.normaliseString("Another Brick In The Wall, Part Two!"));

        // Case with parentheses, trailing/leading whitespace and punctuation
        String realTitle = "  It's The End Of The World As We Know It (And I Feel Fine)    ";
        String normalisedTitle = "itstheendoftheworldasweknowit";
        assertEquals(normalisedTitle, SongleMap.normaliseString(realTitle));
    }

    // Tests list for the found words view is sorted alphabetically correctly
    @Test
    public void sortFoundWordsAlphabeticallyCorrect() throws Exception
    {
        MapsActivity mapsActivity = Mockito.mock(MapsActivity.class);
        Mockito.when(mapsActivity.sortFoundWords(Mockito.anyListOf(SongleMarkerInfo.class), Mockito.any(MapsActivity.sortCategory.class))).thenCallRealMethod();

        String[] sorted = {",", "absolute", "absolutely", "banana", "figure", "Hello", "Wonderwall"};
        List<SongleMarkerInfo> returned = mapsActivity.sortFoundWords(infos, MapsActivity.sortCategory.ALPHABETICAL);

        for(int i = 0; i < infos.size(); i++)
        {
            assertEquals(returned.get(i).getLyric(), sorted[i]);
        }
    }

    // Tests list for the found words view is sorted by occurrence in lyrics correctly
    @Test
    public void sortFoundWordsByOrderInLyricsCorrect() throws Exception
    {
        MapsActivity mapsActivity = Mockito.mock(MapsActivity.class);
        Mockito.when(mapsActivity.sortFoundWords(Mockito.anyListOf(SongleMarkerInfo.class), Mockito.any(MapsActivity.sortCategory.class))).thenCallRealMethod();

        String[] sorted = {"absolute", "banana", "absolutely", "Wonderwall", "Hello", ",", "figure"};
        List<SongleMarkerInfo> returned = mapsActivity.sortFoundWords(infos, MapsActivity.sortCategory.SONG_ORDER);

        for(int i = 0; i < infos.size(); i++)
        {
            assertEquals(returned.get(i).getLyric(), sorted[i]);
        }
    }

    // Tests list for the found words view is sorted by importance correctly
    @Test
    public void sortFoundWordsByImportanceCorrect() throws Exception
    {
        MapsActivity mapsActivity = Mockito.mock(MapsActivity.class);
        Mockito.when(mapsActivity.sortFoundWords(Mockito.anyListOf(SongleMarkerInfo.class), Mockito.any(MapsActivity.sortCategory.class))).thenCallRealMethod();

        String[] sorted = {"figure", ",", "banana", "absolute", "absolutely", "Wonderwall", "Hello"};
        List<SongleMarkerInfo> returned = mapsActivity.sortFoundWords(infos, MapsActivity.sortCategory.IMPORTANCE);

        for(int i = 0; i < infos.size(); i++)
        {
            assertEquals(returned.get(i).getLyric(), sorted[i]);
        }
    }


}