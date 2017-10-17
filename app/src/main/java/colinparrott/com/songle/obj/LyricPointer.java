package colinparrott.com.songle.obj;

/**
 * Object that represents the position of a word in a lyrics file
 */

public class LyricPointer
{
    /**
     * Line number lyric occurs (beginning at 1)
     */
    private int lineNumber;

    /**
     * The number of words along a line the lyric occurs (beginning at 1)
     */
    private int wordNumber;

    public LyricPointer(int lineNumber, int wordNumber)
    {
        this.lineNumber = lineNumber;
        this.wordNumber = wordNumber;
    }

    /**
     * Gets the line number the lyric occurs in
     * @return The line number
     */
    public int getLineNumber()
    {
        return lineNumber;
    }

    /**
     * Gets the position of the word in the line
     * @return The position of the word in the line
     */
    public int getWordNumber()
    {
        return wordNumber;
    }

    /**
     * Gets a formatted string showing lyric's line number and word number
     * @return Formatted string of format [lineNumber]:[wordNumber]
     */
    public String toString()
    {
        return lineNumber + ":" + wordNumber;
    }
}
