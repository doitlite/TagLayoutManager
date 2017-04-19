package com.doitlite.taglayoutmanager.example.demo3;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doitlite.taglayoutmanager.TagLayoutManager;
import com.doitlite.taglayoutmanager.example.R;

import java.util.IllegalFormatCodePointException;
import java.util.List;

/**
 * Description:
 * Date: 2017-04-19 11:21
 * Author: chenzc
 */
public class ExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LAST = 1;

    private Context mContext;
    private List<String> mList;
    private TagLayoutManager mTagLayoutManager;
    private LayoutInflater mInflater;
    private Handler mHandler;

    public ExpandableAdapter(@NonNull Context context, @NonNull List<String> list, @NonNull TagLayoutManager tagLayoutManager) {
        this.mContext = context;
        this.mList = list;
        this.mTagLayoutManager = tagLayoutManager;
        this.mInflater = LayoutInflater.from(context);
        this.mHandler = new Handler();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextViewHolder(mInflater.inflate(R.layout.item_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_NORMAL:
                ((TextViewHolder) holder).mTvText.setText(mList.get(position));
                break;
            case TYPE_LAST:
                ((TextViewHolder) holder).mTvText.setText("···");
                break;
        }

//        ((TextViewHolder) holder).mTvText.setText(mList.get(position));
//        if (position == mTagLayoutManager.getMaxVisiableCount() && mTagLayoutManager.getMaxVisiableCount() != getItemCount() - 1) {
//            ((TextViewHolder) holder).mTvText.setText("···");
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    notifyItemChanged(position);
//                }
//            });
//        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mTagLayoutManager.getMaxVisiableCount() && mTagLayoutManager.getMaxVisiableCount() != getItemCount() - 1) {
            return TYPE_LAST;
        }
        return TYPE_NORMAL;
    }

    class TextViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvText;

        public TextViewHolder(View view) {
            super(view);
            mTvText = (TextView) view.findViewById(R.id.tv_tag);
        }
    }

}
