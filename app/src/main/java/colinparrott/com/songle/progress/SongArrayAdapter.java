package colinparrott.com.songle.progress;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import colinparrott.com.songle.R;
import colinparrott.com.songle.game.obj.Song;
import colinparrott.com.songle.game.parsers.SongleKmlParser;
import colinparrott.com.songle.storage.UserPrefsManager;

/**
 * Custom ArrayAdapter used for ListView in ProgressActivity which lists
 * the songs
 */

public class SongArrayAdapter extends ArrayAdapter<Song>
{

    private Context context;
    private int resourceId;
    private List<Song> songs;

    public SongArrayAdapter(@NonNull Context context, int resource, @NonNull List<Song> objects)
    {
        super(context, resource, objects);

        this.context = context;
        this.resourceId = resource;
        this.songs = objects;
    }



    // Code adapted from: https://www.androidcode.ninja/android-viewholder-pattern-example/
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolderItem viewHolder;


        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row, parent, false);


            viewHolder = new ViewHolderItem();
            viewHolder.txtNum = (TextView) convertView.findViewById(R.id.txtRowNum);
            viewHolder.txtTitle = (TextView)convertView.findViewById(R.id.txtRowTitle);
            viewHolder.txtArtist = (TextView)convertView.findViewById(R.id.txtRowArtist);
            viewHolder.btnPlayVideo = (ImageButton) convertView.findViewById(R.id.imgCompletedPlay);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        Song song = songs.get(position);

        if(song != null)
        {
            boolean completed = songCompleted(song);

            viewHolder.txtNum.setText(SongleKmlParser.formatNumber(song.getNumber()));
            ConstraintLayout layout = convertView.findViewById(R.id.ConstraintLayoutList);

            // If song completed show its details otherwise hide it
            if(completed)
            {
                layout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryMedium));
                viewHolder.txtTitle.setText(song.getTitle());
                viewHolder.txtArtist.setText(song.getArtist());
                viewHolder.btnPlayVideo.setVisibility(View.VISIBLE);
            }
            else
            {
                layout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                viewHolder.txtTitle.setText(R.string.txt_NotGuessed);
                viewHolder.txtArtist.setText(R.string.txt_Unknown);
                viewHolder.btnPlayVideo.setVisibility(View.INVISIBLE);
            }

            // For opening YouTube link for a song
            final String link = song.getLink();
            viewHolder.btnPlayVideo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    context.startActivity(browserIntent);
                }
            });
        }

        return convertView;
    }

    /**
     * Check whether user has completed a song
     * @param s Song to check
     * @return True if completed; false otherwise
     */
    public boolean songCompleted(Song s)
    {
        UserPrefsManager userPrefsManager = new UserPrefsManager(context);
        int[] songNums = userPrefsManager.getCompletedNumbersInt();

        if(songNums != null)
        {
            // If song number is in completed list return true
            for (int i : songNums)
            {
                if (s.getNumber() == i)
                {
                    return true;
                }
            }
        }

        // Reach this statement if we haven't returned true in the for loop (i.e. song not in completed list)
        return false;
    }

    /**
     * Class representing an item in the list
     */
    static class ViewHolderItem
    {
        TextView txtNum, txtTitle, txtArtist;
        ImageButton btnPlayVideo;
    }
}
