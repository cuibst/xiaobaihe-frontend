package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
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

import java.util.List;

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
        ImageView imageView = (ImageView) view.findViewById(R.id.itemSubjectPicture);
        switch(entity.getSubject()) {
            case "physics":
                imageView.setImageResource(R.drawable.phy);
                break;
            case "chemistry":
                imageView.setImageResource(R.drawable.che);
                break;
            case "biology":
                imageView.setImageResource(R.drawable.bio);
                break;
            case "geo":
                imageView.setImageResource(R.drawable.geo);
                break;
            case "chinese": //FIXME: add different resource for different subject!!
            case "math":
            case "english":
            case "history":
            case "politics":
            default:
                imageView.setImageResource(R.drawable.book);
                break;
        }
        TextView textView = (TextView) view.findViewById(R.id.favouriteItemName);
        textView.setText(entity.getName());
        view.setOnClickListener((View v) -> {
            Intent intent = new Intent(getContext(), EntityActivity.class);
            intent.putExtra("name",entity.getName());
            intent.putExtra("subject",entity.getSubject());
            getContext().startActivity(intent);
        });
        return view;
    }
}
