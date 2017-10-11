package colinparrott.com.songle.maps;

/**
 * Created by Colin on 11/10/2017.
 */

public class LyricPointer
{
    private int lineNumber;
    private int wordNumber;

    public LyricPointer(int lineNumber, int wordNumber)
    {
        this.lineNumber = lineNumber;
        this.wordNumber = wordNumber;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public int getWordNumber()
    {
        return wordNumber;
    }

    public String toString()
    {
        return lineNumber + ":" + wordNumber;
    }
}
