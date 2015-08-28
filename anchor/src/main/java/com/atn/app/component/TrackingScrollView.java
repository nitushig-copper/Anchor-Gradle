/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.atn.app.listener.OnScrollChangedListener;

//TrackingScroll view to check scroll position for parallax effect
public class TrackingScrollView extends ScrollView {
    
    private OnScrollChangedListener mOnScrollChangedListener;

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    public TrackingScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackingScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

   
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }
}