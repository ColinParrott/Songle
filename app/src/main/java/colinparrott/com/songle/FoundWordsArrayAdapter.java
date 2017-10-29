package colinparrott.com.songle;

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

import colinparrott.com.songle.obj.Song;
import colinparrott.com.songle.obj.SongleMap;
import colinparrott.com.songle.obj.SongleMarkerInfo;
import colinparrott.com.songle.parsers.SongleKmlParser;

/**
 * Created by Colin on 28/10/2017.
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

        SongleMarkerInfo word = words.get(position);

        if(word != null)
        {

            viewHolder.txtLoc.setText(word.getLyricPointer().toString());
            viewHolder.txtLyric.setText(word.getLyric());
            viewHolder.imgImportance.setBackground(context.getDrawable(SongleMap.determineMarkerIcon(word.getImportance())));

        }

        return convertView;
    }

    static class ViewHolderItem {
        TextView txtLoc, txtLyric;
        ImageButton imgImportance;
    }
}
