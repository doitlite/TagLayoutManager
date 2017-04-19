package com.doitlite.taglayoutmanager;

import android.graphics.Rect;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Description:
 * Date: 2017-04-06 11:27
 * Author: chenzc
 */
public class TagLayoutManager extends RecyclerView.LayoutManager {

    public static final String TAG = TagLayoutManager.class.getSimpleName();

    private int mHorizontalOffset;
    private int mVerticalOffset;
    private int mTotalWidth;      // just for record, not the true total width
    private int mTotalHeight;
    private int mTotalLineNum;
    private int mMaxVisiableCount;

    private RectArray<Rect> mItems;

    private Params mParams;

    private int[] mMeasuredDimension = new int[2];
    private int mHeightSize;

    private TagLayoutManager() {
        mItems = new RectArray<>(new RectArray.New<Rect>() {
            @Override
            public Rect get() { return new Rect();}
        });
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }

        int widthMode = View.MeasureSpec.getMode(widthSpec);
        int heightMode = View.MeasureSpec.getMode(heightSpec);
        int widthSize = View.MeasureSpec.getSize(widthSpec);
        int heightSize = View.MeasureSpec.getSize(heightSpec);

        int width = getWidth();
        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
            case View.MeasureSpec.AT_MOST:
                width = widthSize;
                break;
            case View.MeasureSpec.UNSPECIFIED:
                break;
        }

        int childWidth;
        int childHeight;
        boolean isNewLine = false;
        mTotalWidth = 0;
        mTotalHeight = 0;
        mTotalLineNum = 0;
        for (int i = 0; i < getItemCount(); i++) {
            measureScrapChild(recycler, i,
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    mMeasuredDimension);
            childWidth = mMeasuredDimension[0];
            childHeight = mMeasuredDimension[1];

            Log.i(TAG, "onMeasure: position:" + i + ",childWidth:" + childWidth + ",childHeight:" + childHeight);

            if (i == 0) {
                mTotalLineNum = 1;
                mTotalHeight = childHeight + mParams.mBorderTop;
                mTotalWidth += childWidth;
            } else {
                mTotalWidth += childWidth + mParams.mBorderHor;
            }

            if (mTotalWidth + mParams.mBorderLeft + mParams.mBorderRight > width) {
                if (mParams.getMaxLineNum() != 0 && mTotalLineNum >= mParams.getMaxLineNum()) {
                    break;
                }
                mTotalLineNum++;
                isNewLine = true;
                mTotalWidth = 0;
                mTotalHeight += childHeight + mParams.mBorderVer;
                mTotalWidth += childWidth;
            } else {
                if(isNewLine) {
                    isNewLine = false;
                }
            }

            mMaxVisiableCount = i;
        }

        mTotalHeight += mParams.mBorderBottom;
        Log.i(TAG, "onMeasure: mTotalHeight：" + mTotalHeight);

        int height = mTotalHeight;
        if (mParams.mMaxHeight != 0) {
            mHeightSize = Math.min(heightSize, mParams.mMaxHeight);
        } else {
            mHeightSize = heightSize;
        }
        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case View.MeasureSpec.AT_MOST:
                if (mParams.mMaxHeight != 0) {
                    height = Math.min(height, mParams.mMaxHeight);
                }
                break;
            case View.MeasureSpec.UNSPECIFIED:
                break;
        }

        Log.i(TAG, "onMeasure: height：" + height);
        if (mParams.mMaxHeight != 0 && mTotalHeight < mParams.mMaxHeight) {
            mVerticalOffset = 0;
        }
        Log.i(TAG, "onMeasure: mVerticalOffset:" + mVerticalOffset);
        setMeasuredDimension(width, height);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,int heightSpec, int[] measuredDimension) {
        View view = null;
        try {
            view = recycler.getViewForPosition(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (view != null) {
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                    getPaddingTop() + getPaddingBottom(), p.height);
            view.measure(widthSpec, childHeightSpec);
            measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
            measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
            recycler.recycleView(view);
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }

        measureChild(recycler, state);
        detachAndScrapAttachedViews(recycler);
        fill(recycler, state);
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return mParams.isNestedScrollingEnabled();
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            if (mTotalHeight < mHeightSize) {
                mVerticalOffset = 0;
                detachAndScrapAttachedViews(recycler);
                offsetChildrenVertical(0);
                fill(recycler, state);
                return 0;
            }

            int verticalSpace;
            if (mTotalHeight == getVerticalSpace()) {
                verticalSpace = mHeightSize;
            } else {
                verticalSpace = getVerticalSpace();
            }

            if (mVerticalOffset + dy < 0) {
                dy = -mVerticalOffset;
            } else if (mVerticalOffset + dy > mTotalHeight - verticalSpace) {
                dy = mTotalHeight - verticalSpace - mVerticalOffset;
            }

            detachAndScrapAttachedViews(recycler);
            offsetChildrenVertical(-dy);
            fill(recycler, state);
            mVerticalOffset += dy;
            return dy;
        } catch (Exception e) {
            Log.i(TAG, "chenzc scrollVerticallyBy: exception:" + e.getMessage());
            return 0;
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }

        if (position == getItemCount() - 1) {
            mVerticalOffset = mTotalHeight - getHeight();
        }
        if (mVerticalOffset != 0) {
//            LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext());
//            linearSmoothScroller.setTargetPosition(position);
//            startSmoothScroll(linearSmoothScroller);
        }
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    private void measureChild(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }

        int childWidth;
        int childHeight;
        boolean isNewLine = false;
        mTotalWidth = 0;
        mTotalHeight = 0;
        for (int i = 0; i <= Math.min(mMaxVisiableCount, getItemCount() - 1); i++) {
            Rect item = mItems.get(i);

            View child = recycler.getViewForPosition(i);
            measureChildWithMargins(child, 0, 0);
            childWidth = getDecoratedMeasuredWidth(child);
            childHeight = getDecoratedMeasuredHeight(child);

            if (i == 0) {
                mTotalHeight = childHeight + mParams.mBorderTop;
                mTotalWidth += childWidth;
            } else {
                mTotalWidth += childWidth + mParams.mBorderHor;
            }

            if (mTotalWidth + mParams.mBorderLeft + mParams.mBorderRight > getWidth()) {
                isNewLine = true;
                mTotalWidth = 0;
                mTotalHeight += childHeight + mParams.mBorderVer;
                item.set(
                        mParams.mBorderLeft,
                        mTotalHeight - childHeight,
                        mParams.mBorderLeft + childWidth,
                        mTotalHeight);
                mTotalWidth += childWidth;
            } else {
                if(isNewLine) {
                    isNewLine = false;
                }
                item.set(
                        mTotalWidth - childWidth + mParams.mBorderLeft,
                        mTotalHeight - childHeight,
                        mTotalWidth + mParams.mBorderRight,
                        mTotalHeight);
            }
        }

        mTotalHeight += mParams.mBorderBottom;
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }

        Rect displayRect = new Rect(mHorizontalOffset, mVerticalOffset,
                getHorizontalSpace() + mHorizontalOffset,
                getVerticalSpace() + mVerticalOffset);

        for (int i = 0; i < getItemCount(); i++) {
            Rect frame = mItems.get(i);
            if (Rect.intersects(displayRect, frame)) {
                View scrap = recycler.getViewForPosition(i);
                addView(scrap);
                measureChildWithMargins(scrap, 0, 0);
                layoutDecorated(scrap, frame.left - mHorizontalOffset, frame.top - mVerticalOffset,
                        frame.right - mHorizontalOffset, frame.bottom - mVerticalOffset);
            }
        }
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public Params getParams() {
        return mParams;
    }

    public void setParams(Params params) {
        mParams = params;
    }

    public int getMaxVisiableCount() {
        return mMaxVisiableCount;
    }

    static class RectArray<T> {
        private SparseArrayCompat<T> mPool;
        private New<T> mNewInstance;

        public RectArray(New<T> newInstance) {
            mPool = new SparseArrayCompat<>();
            mNewInstance = newInstance;
        }

        public T get(int key) {
            T res = mPool.get(key);
            if (res == null) {
                res = mNewInstance.get();
                mPool.put(key, res);
            }
            return res;
        }

        interface New<T> {
            T get();
        }
    }

    public static class Params {
        private int mBorderLeft;
        private int mBorderTop;
        private int mBorderRight;
        private int mBorderBottom;
        private int mBorderHor;
        private int mBorderVer;
        private int mMaxHeight;
        private int mMaxLineNum;
        private boolean isNestedScrollingEnabled = true;

        public int getBorderLeft() {
            return mBorderLeft;
        }

        public void setBorderLeft(int borderLeft) {
            mBorderLeft = borderLeft;
        }

        public int getBorderTop() {
            return mBorderTop;
        }

        public void setBorderTop(int borderTop) {
            mBorderTop = borderTop;
        }

        public int getBorderRight() {
            return mBorderRight;
        }

        public void setBorderRight(int borderRight) {
            mBorderRight = borderRight;
        }

        public int getBorderBottom() {
            return mBorderBottom;
        }

        public void setBorderBottom(int borderBottom) {
            mBorderBottom = borderBottom;
        }

        public int getBorderHor() {
            return mBorderHor;
        }

        public void setBorderHor(int borderHor) {
            mBorderHor = borderHor;
        }

        public int getBorderVer() {
            return mBorderVer;
        }

        public void setBorderVer(int borderVer) {
            mBorderVer = borderVer;
        }

        public int getMaxHeight() {
            return mMaxHeight;
        }

        public void setMaxHeight(int maxHeight) {
            mMaxHeight = maxHeight;
        }

        public int getMaxLineNum() {
            return mMaxLineNum;
        }

        public void setMaxLineNum(int maxLineNum) {
            mMaxLineNum = maxLineNum;
        }

        public boolean isNestedScrollingEnabled() {
            return isNestedScrollingEnabled;
        }

        public void setNestedScrollingEnabled(boolean nestedScrollingEnabled) {
            isNestedScrollingEnabled = nestedScrollingEnabled;
        }
    }

    public static class Builder {

        private Params mParams;

        public Builder() {
            mParams = new Params();
        }

        public Builder setBorderLeft(int borderLeft) {
            mParams.setBorderLeft(borderLeft);
            return this;
        }

        public Builder setBorderTop(int borderTop) {
            mParams.setBorderTop(borderTop);
            return this;
        }

        public Builder setBorderRight(int borderRight) {
            mParams.setBorderRight(borderRight);
            return this;
        }

        public Builder setBorderBottom(int borderBottom) {
            mParams.setBorderBottom(borderBottom);
            return this;
        }

        public Builder setBorderHor(int borderHor) {
            mParams.setBorderHor(borderHor);
            return this;
        }

        public Builder setBorderVer(int borderVer) {
            mParams.setBorderVer(borderVer);
            return this;
        }

        public Builder setMaxHeight(int maxHeight) {
            if (mParams.getMaxLineNum() != 0) {
                throw new IllegalArgumentException("already set the max line number");
            }
            if (maxHeight < 0) {
                throw new IllegalArgumentException("the max height can not less than 0");
            }
            mParams.setMaxHeight(maxHeight);
            return this;
        }

        public Builder setMaxLineNum(int maxLineNum) {
            if (mParams.getMaxHeight() != 0) {
                throw new IllegalArgumentException("already set the max height");
            }
            if (maxLineNum < 0) {
                throw new IllegalArgumentException("the max line number can not less than 0");
            }
            mParams.setMaxLineNum(maxLineNum);
            return this;
        }

        public Builder setNestedScrollingEnabled(boolean enabled) {
            mParams.setNestedScrollingEnabled(enabled);
            return this;
        }

        public TagLayoutManager create() {
            TagLayoutManager tagLayoutManager = new TagLayoutManager();
            tagLayoutManager.setParams(mParams);
            return tagLayoutManager;
        }
    }

}
