package com.apyarapk.khakhway.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.apyarapk.khakhway.R;

import java.util.ArrayList;

public class ApyarAdapter extends BaseAdapter {
    Activity context;
    ArrayList<String> book_name;

    public ApyarAdapter(Activity context, ArrayList<String> book_name) {
        this.context = context;
        this.book_name = book_name;
    }

    @Override
    public int getCount() {
        return book_name.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.book_items, null);
            TextView tv = view.findViewById(R.id.tv);
            tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "font/mm.ttf"));
            tv.setText(book_name.get(i));
        return view;
    }


    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
