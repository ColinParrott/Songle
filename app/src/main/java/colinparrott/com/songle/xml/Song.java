package colinparrott.com.songle.xml;

/**
 * Created by Colin on 10/10/2017.
 */

public class Song
{
    private final int number;
    private final String artist;
    private final String title;
    private final String link;

    public Song(int number, String artist, String title, String link)
    {
        this.number = number;
        this.artist = artist;
        this.title = title;
        this.link = link;
    }

    public int getNumber()
    {
        return number;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getTitle()
    {
        return title;
    }

    public String getLink()
    {
        return link;
    }
}
