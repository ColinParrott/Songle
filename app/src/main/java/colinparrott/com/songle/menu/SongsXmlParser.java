package colinparrott.com.songle.menu;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import colinparrott.com.songle.game.obj.Song;

/**
 * XML parser for songs database - adapted from lecture slides
 */

public class SongsXmlParser
{
    private static final String ns = null;

    public List<Song> parse(InputStream in) throws XmlPullParserException, IOException
    {
        try
        {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            return readFeed(parser);
        }
        finally
        {
            in.close();
        }
    }

    private List<Song> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        List<Song> songs = new ArrayList<Song>();
        parser.require(XmlPullParser.START_TAG, ns, "Songs");

        while(parser.next() != XmlPullParser.END_TAG)
        {
            if(parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }

            String name = parser.getName();

            if(name.equals("Song"))
            {
                songs.add(readSong(parser));
            }
            else
            {
                skip(parser);
            }
        }

        return songs;
    }

    private Song readSong(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, ns, "Song");

        int number = -1;
        String title = null;
        String artist = null;
        String link = null;

        while(parser.next() != XmlPullParser.END_TAG)
        {
            if(parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }

            String name = parser.getName();

            if(name.equals("Number"))
            {
                number = readSongNumber(parser);
            }
            else if(name.equals("Title"))
            {
                title = readSongTitle(parser);
            }
            else if(name.equals("Artist"))
            {
                artist = readSongArtist(parser);
            }
            else if(name.equals("Link"))
            {
                link = readSongLink(parser);
            }
            else
            {
                skip(parser);
            }
        }

        return new Song(number, artist, title, link);
    }

    private int readSongNumber(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, ns, "Number");
        int number = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "Number");

        return number;
    }

    private String readSongArtist(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, ns, "Artist");
        String artist = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Artist");

        return artist;
    }

    private String readSongTitle(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, ns, "Title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Title");

        return title;
    }

    private String readSongLink(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, ns, "Link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Link");

        return link;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        String result = "";

        if(parser.next() == XmlPullParser.TEXT)
        {
            result = parser.getText();
            parser.nextTag();
        }

        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        if(parser.getEventType() != XmlPullParser.START_TAG)
        {
            throw new IllegalStateException();
        }

        int depth = 1;

        while(depth != 0)
        {
            switch (parser.next())
            {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
