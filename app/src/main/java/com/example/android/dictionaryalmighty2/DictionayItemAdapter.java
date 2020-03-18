package com.example.android.dictionaryalmighty2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


/**
 * 設置客製化spinner的Adapter
 */
public class DictionayItemAdapter extends ArrayAdapter <DictionaryItem> {

    public DictionayItemAdapter(Context context, ArrayList<DictionaryItem> countryList) {
        super(context, 0, countryList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.custom_spinner_with_images, parent, false
            );
        }

        ImageView imageViewFlag = convertView.findViewById(R.id.custom_spinner_image_view);
        TextView textViewName = convertView.findViewById(R.id.custom_spinner_text_view);

        DictionaryItem currentItem = getItem(position);

        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getSpinnerImage());
            textViewName.setText(currentItem.getSpinnerText());
        }

        return convertView;
    }
}
