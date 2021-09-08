package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.database.DatabaseEntity;
import com.java.cuiyikai.utilities.ConstantUtilities;

import java.util.List;

/**
 * {@link ArrayAdapter} for {@link android.widget.ListView} in {@link com.java.cuiyikai.activities.OfflineActivity}
 */
public class OfflineAdapter extends ArrayAdapter<DatabaseEntity> {

    private final int resourceId;

    public OfflineAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<DatabaseEntity> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DatabaseEntity entity = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        ImageView imageView = view.findViewById(R.id.itemSubjectPicture);
        View itemView = view.findViewById(R.id.directoryFavouriteItem);
        switch(entity.getSubject()) { // bind the picture and background.
            case ConstantUtilities.SUBJECT_CHINESE :
                itemView.setBackgroundResource(R.drawable.chinese_radius);
                imageView.setImageResource(R.drawable.chinese);
                break;
            case ConstantUtilities.SUBJECT_MATH :
                itemView.setBackgroundResource(R.drawable.maths_radius);
                imageView.setImageResource(R.drawable.maths);
                break;
            case ConstantUtilities.SUBJECT_ENGLISH :
                itemView.setBackgroundResource(R.drawable.english_radius);
                imageView.setImageResource(R.drawable.english);
                break;
            case ConstantUtilities.SUBJECT_PHYSICS :
                itemView.setBackgroundResource(R.drawable.physics_radius);
                imageView.setImageResource(R.drawable.physics);
                break;
            case ConstantUtilities.SUBJECT_CHEMISTRY :
                itemView.setBackgroundResource(R.drawable.chemistry_radius);
                imageView.setImageResource(R.drawable.chemistry);
                break;
            case ConstantUtilities.SUBJECT_BIOLOGY :
                itemView.setBackgroundResource(R.drawable.biology_radius);
                imageView.setImageResource(R.drawable.biology);
                break;
            case ConstantUtilities.SUBJECT_HISTORY :
                itemView.setBackgroundResource(R.drawable.history_radius);
                imageView.setImageResource(R.drawable.history);
                break;
            case ConstantUtilities.SUBJECT_GEO :
                itemView.setBackgroundResource(R.drawable.geography_radius);
                imageView.setImageResource(R.drawable.geography);
                break;
            case ConstantUtilities.SUBJECT_POLITICS:
            default:
                itemView.setBackgroundResource(R.drawable.politics_radius);
                imageView.setImageResource(R.drawable.politics);
                break;
        }
        TextView textView = view.findViewById(R.id.favouriteItemName);
        textView.setText(entity.getName());
        view.setOnClickListener((View v) -> { //bind the click event to visit the entity page.
            Intent intent = new Intent(getContext(), EntityActivity.class);
            intent.putExtra(ConstantUtilities.ARG_NAME,entity.getName());
            intent.putExtra(ConstantUtilities.ARG_SUBJECT,entity.getSubject());
            getContext().startActivity(intent);
        });
        return view;
    }
}
