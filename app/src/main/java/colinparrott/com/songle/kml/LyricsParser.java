package colinparrott.com.songle.kml;

import colinparrott.com.songle.maps.LyricPointer;

/**
 * Created by Colin on 11/10/2017.
 */

public class LyricsParser
{
    public static String getLyric(String lyricString, LyricPointer pointer)
    {
        String[] lines = lyricString.split("\n");
        String theLine = lines[pointer.getLineNumber() - 1];


        String[] theWords = theLine.split("\t")[1].split(" ");
        String theWord = theWords[pointer.getWordNumber() - 1].trim();

       // System.out.println("Pointer: " + pointer + "\tLine: " + theLine + "\tWord: " + theWord);
        return theWord;
    }
}
