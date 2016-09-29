package com.vng.fresherandroid.mykara.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.vng.fresherandroid.mykara.Adapter.FavoriteListSongAdapter;
import com.vng.fresherandroid.mykara.Adapter.ListSongAdapter;
import com.vng.fresherandroid.mykara.Adapter.ListVFSongAdaper;

/**
 * Custom View ListView for Viet's Songs and Foreign's Songs
 */
public class IndexableListView extends ListView {
	
	private boolean mIsFastScrollEnabled = false;
	private IndexScroller mScroller = null; //Index bar
	private GestureDetector mGestureDetector = null;
	private int heightMax = 0;
	public IndexableListView(Context context) {
		super(context);
	}

	public IndexableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndexableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFastScrollEnabled() {
		return mIsFastScrollEnabled;
	}

	@Override
	public void setFastScrollEnabled(boolean enabled) {
		mIsFastScrollEnabled = enabled;
		if (mIsFastScrollEnabled) { // Only used to index bar if listView is setFastScrollEnabled
			if (mScroller == null) {
				mScroller = new IndexScroller(getContext(), this);
			}
		} else {
			if (mScroller != null) {
				mScroller.hide();
				mScroller = null;
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		// Draw index bar
		if (mScroller != null) {
			mScroller.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Intercept ListView's touch event
		if (mScroller != null && mScroller.onTouchEvent(ev))
			return true;
		
		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					// If fling happens, index bar shows
					mScroller.show();
					return super.onFling(e1, e2, velocityX, velocityY);
				}
				
			});
		}
		mGestureDetector.onTouchEvent(ev);
		
		return super.onTouchEvent(ev);
	}


	public void setAdapter(ListVFSongAdaper adapter) {
		super.setAdapter(adapter);
		if (mScroller != null) {
			mScroller.setAdapter(adapter);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null && h > heightMax) {
			heightMax = h;
			mScroller.onSizeChanged(w, h);
		}
	}

}
