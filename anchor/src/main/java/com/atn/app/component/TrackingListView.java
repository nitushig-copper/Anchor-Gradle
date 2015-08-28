/**
 * @Copyright CopperMobile 2014.
 * */
package com.atn.app.component;

import java.util.Dictionary;
import java.util.Hashtable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.atn.app.listener.OnScrollChangedListener;

//tracking list view to scroll position for parallax effect
public class TrackingListView extends ListView {

	private OnScrollChangedListener mOnScrollChangedListener;

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

	public TrackingListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TrackingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TrackingListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	 @Override
	    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
	        super.onScrollChanged(l, t, oldl, oldt);

	        if (mOnScrollChangedListener != null) {
	            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
	        }
	    }

	 
	 @Override
	    public int computeVerticalScrollOffset() {
	        return super.computeVerticalScrollOffset();
	    }
	 
	 
	 private Dictionary<Integer, Integer> listViewItemHeights = new Hashtable<Integer, Integer>();

	    public int getScroll() {
	        View c = getChildAt(0); //this is the first visible row
	        int scrollY = -c.getTop();
	        listViewItemHeights.put(getFirstVisiblePosition(), c.getHeight());
	        for (int i = 0; i < getFirstVisiblePosition(); ++i) {
	            if (listViewItemHeights.get(i) != null) // (this is a sanity check)
	                scrollY += listViewItemHeights.get(i); //add all heights of the views that are gone
	        }
	        return scrollY;
	    }
	    
	    public int getViewHeigth(View v) {
	        int viewPosition = getPositionForView(v);
	        int scrollY = 0;
	        for (int i = 0; i < viewPosition; ++i) {
	                scrollY += getChildAt(i).getHeight();
	        }
	        return scrollY;
	    }
}
