package colinparrott.com.songle.game.parsers;

import colinparrott.com.songle.game.obj.LyricPointer;

/**
 * Utility class for parsing lyrics
 */

public class LyricsParser
{
    /**
     * Gets lyric at specified location in a lyrics string
     * @param lyricString String with lyrics of whole song
     * @param pointer Location of lyric
     * @return The lyric
     */
    public static String getLyric(String lyricString, LyricPointer pointer)
    {
        // Split lines
        String[] lines = lyricString.split("\n");

        // Get specified line - minus 1 since line numbering in lyrics file begins at 1 not 0
        String theLine = lines[pointer.getLineNumber() - 1];

        // Split off first part with line number and tab - take 2nd element i.e. the actual lyric line
        // Then split on spaces to get each word in a list
        String[] theWords = theLine.split("\t")[1].split(" ");

        // Get the specified word in the line - minus 1 again since word numbering begins at 1 not 0
        String theWord = theWords[pointer.getWordNumber() - 1].trim();

       // System.out.println("Pointer: " + pointer + "\tLine: " + theLine + "\tWord: " + theWord);
        return theWord;
    }
}
