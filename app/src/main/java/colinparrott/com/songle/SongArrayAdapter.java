package colinparrott.com.songle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import colinparrott.com.songle.obj.Song;
import colinparrott.com.songle.storage.UserPrefsManager;

/**
 * Created by s1546623 on 26/10/17.
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_row, parent, false);

        Song s = this.getItem(position);

        if(s != null)
        {
            LinearLayout layout = rowView.findViewById(R.id.listLinearLayout);

            boolean completed = songCompleted(s);

            if(completed)
            {
                layout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryMedium));
                ((TextView) rowView.findViewById(R.id.textRowTitle)).setText(s.getTitle());
                ((TextView) rowView.findViewById(R.id.textRowArtist)).setText(s.getArtist());

            }
            else
            {
                layout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                ((TextView) rowView.findViewById(R.id.textRowTitle)).setText("Not Guessed");
                ((TextView) rowView.findViewById(R.id.textRowArtist)).setText("???");
            }

        }

        return rowView;

    }

    public boolean songCompleted(Song s)
    {
        UserPrefsManager userPrefsManager = new UserPrefsManager(context.getSharedPreferences("userDetails", Context.MODE_PRIVATE));
        int[] songNums = userPrefsManager.getCompletedNumbersInt();

        if(songNums != null)
        {
            for (int i : songNums) {
                if (s.getNumber() == i) {
                    return true;
                }
            }
        }

        return false;
    }
}
