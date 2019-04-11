package com.uol.yt120.lecampus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageViewAdapter extends PagerAdapter {

    private Context mContext;
    private int[] mImageIds =
            new int[] {R.drawable.};

    public ImageViewAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getCount() {
        return mImageIds.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(mImageIds[position]);
        container.addView(imageView,0);
        return imageView;
    }

    public void destoryItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

}
