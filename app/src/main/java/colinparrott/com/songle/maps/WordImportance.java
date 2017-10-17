package colinparrott.com.songle.maps;

/**
 * Different possible importance of words as defined
 * in the coursework specification
 */

public enum WordImportance
{
    /**
     * Unclassified - word importance is hidden
     */
    UNCLASSIFIED,

    /**
     * Boring - word is very common and not too helpful
     */
    BORING,

    /**
     * Not boring - word is less common
     */
    NOT_BORING,

    /**
     * Interesting - word is even less common
     */
    INTERESTING,

    /**
     * Very interesting - word is rare and should be a key word in the song
     */
    VERY_INTERESTING
}
