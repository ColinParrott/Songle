package colinparrott.com.songle.game.obj;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import colinparrott.com.songle.R;
import colinparrott.com.songle.game.SongleMap;

/**
 * Custom ArrayAdapter for displaying the found words in MapsActivity
 */

public class FoundWordsArrayAdapter extends ArrayAdapter<SongleMarkerInfo>
{

    private Context context;
    private int resourceId;
    private List<SongleMarkerInfo> words;

    public FoundWordsArrayAdapter(Context context, int resourceId, List<SongleMarkerInfo> words)
    {
        super(context, resourceId, words);

        this.context = context;
        this.resourceId = resourceId;
        this.words = words;
    }

    // Code adapted from: https://www.androidcode.ninja/android-viewholder-pattern-example/
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolderItem viewHolder;


        // If we've not loaded this row before, inflate it otherwise get it's ViewHolder and update it
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.words_list_row_2, parent, false);


            viewHolder = new FoundWordsArrayAdapter.ViewHolderItem();
            viewHolder.txtLoc = (TextView) convertView.findViewById(R.id.txtRowLoc);
            viewHolder.txtLyric = (TextView)convertView.findViewById(R.id.txtRowLyric);
            viewHolder.imgImportance = (ImageButton) convertView.findViewById(R.id.imgImportance);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (FoundWordsArrayAdapter.ViewHolderItem) convertView.getTag();
        }

        // Update row with info for that song
        SongleMarkerInfo word = words.get(position);
        if(word != null)
        {

            viewHolder.txtLoc.setText(word.getLyricPointer().toString());
            viewHolder.txtLyric.setText(word.getLyric());
            viewHolder.imgImportance.setBackground(context.getDrawable(SongleMap.determineMarkerIcon(word.getImportance())));

        }

        return convertView;
    }

    /**
     * Object representing a row
     */
    static class ViewHolderItem
    {
        TextView txtLoc, txtLyric;
        ImageButton imgImportance;
    }
}
