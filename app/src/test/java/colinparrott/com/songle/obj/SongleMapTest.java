package colinparrott.com.songle.obj;

import org.junit.Test;

import colinparrott.com.songle.R;

import static org.junit.Assert.*;

/**
 * Created by Colin on 07/11/2017.
 */
public class SongleMapTest
{
    @Test
    public void determineMarkerIconCorrect() throws Exception
    {
        assertEquals(R.mipmap.marker_unclassified, SongleMap.determineMarkerIcon(WordImportance.UNCLASSIFIED));
        assertEquals(R.mipmap.marker_boring, SongleMap.determineMarkerIcon(WordImportance.BORING));
        assertEquals(R.mipmap.marker_notboring, SongleMap.determineMarkerIcon(WordImportance.NOT_BORING));
        assertEquals(R.mipmap.marker_interesting, SongleMap.determineMarkerIcon(WordImportance.INTERESTING));
        assertEquals(R.mipmap.marker_veryinteresting, SongleMap.determineMarkerIcon(WordImportance.VERY_INTERESTING));
    }

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

}