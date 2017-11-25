package colinparrott.com.songle.parsers;

import org.junit.Test;


import colinparrott.com.songle.game.obj.LyricPointer;
import colinparrott.com.songle.game.parsers.LyricsParser;

import static org.junit.Assert.*;

/**
 * Created by Colin on 07/11/2017.
 */
public class LyricsParserTest
{
    // Tests correct lyric is obtained
    @Test
    public void getLyricCorrect() throws Exception
    {
        String lyrics = "     1\tI got my head checked\n" +
                "     2\tBy a jumbo jet\n" +
                "     3\tIt wasn't easy\n" +
                "     4\tBut nothing is \n" +
                "     5\tNo\n" +
                "     6\t\n" +
                "     7\t[chorus]\n" +
                "     8\tWoo-hoo\n" +
                "     9\tWhen I feel heavy-metal\n" +
                "    10\tAnd I'm pins and I'm needles\n" +
                "    11\tWell, I lie and I'm easy\n" +
                "    12\tAll the time but I am never sure\n" +
                "    13\tWhy I need you\n" +
                "    14\tPleased to meet you\n" +
                "    15\t\n" +
                "    16\tI got my head down\n" +
                "    17\tWhen I was young\n" +
                "    18\tIt's not my problem\n" +
                "    19\tIt's not my problem\n" +
                "    20\t\n" +
                "    21\t[repeat chorus]\n" +
                "    22\t\n" +
                "    23\tYeah yeah\n" +
                "    24\tYeah yeah\n" +
                "    25\tYeah yeah\n" +
                "    26\tOh yeah";

        // 2 edge cases and one normal case
        assertEquals("I", LyricsParser.getLyric(lyrics, new LyricPointer(1, 1)));
        assertEquals("I'm", LyricsParser.getLyric(lyrics, new LyricPointer(10, 2)));
        assertEquals("yeah", LyricsParser.getLyric(lyrics, new LyricPointer(26, 2)));

        // Exceptional cases
        assertEquals(null, LyricsParser.getLyric(lyrics, new LyricPointer(0, 2)));
        assertEquals(null, LyricsParser.getLyric(lyrics, new LyricPointer(5, 10)));
        assertEquals(null, LyricsParser.getLyric(lyrics, new LyricPointer(412, 2)));
        assertEquals(null, LyricsParser.getLyric(lyrics, null));
    }

}