package colinparrott.com.songle.xml;

/**
 * Object to represent a Song
 */

public class Song
{
    /**
     * Number of song in songs.xml
     */
    private final int number;

    /**
     * Artist of song
     */
    private final String artist;

    /**
     * Title of song
     */
    private final String title;

    /**
     * YouTube URL of song
     */
    private final String link;

    public Song(int number, String artist, String title, String link)
    {
        this.number = number;
        this.artist = artist;
        this.title = title;
        this.link = link;
    }

    /**
     * Gets number of song
     * @return Number of song
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * Gets name of artist of the song
     * @return Artist of song
     */
    public String getArtist()
    {
        return artist;
    }

    /**
     * Gets title of song
     * @return Song title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Gets YouTube link to song
     * @return YouTube URL of song
     */
    public String getLink()
    {
        return link;
    }
}
