package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


// extends PagerAdapter 를 하고 나면 빨간줄이 그어지는데 반드시 상속해야할 메소드를 상속하지 않았기 때문.
// 해당 메소드들을 상속해준다
public class Adapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<Uri> imageList;

    public Adapter(Context context, ArrayList<Uri> imageList)
    {
        this.mContext = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.viewpager_childview, null);

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageURI(imageList.get(position));
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == (View)o);
    }
}
