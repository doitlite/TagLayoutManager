package com.doitlite.taglayoutmanager.example.demo2;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doitlite.taglayoutmanager.example.R;

import java.util.List;

/**
 * Description:
 * Date: 2017-04-18 19:31
 * Author: chenzc
 */
public class TextAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_0 = 0;
    private static final int TYPE_1 = 1;

    private Context mContext;
    private List<String> mList;
    private LayoutInflater mInflater;

    public TextAdapter(@NonNull Context context, @NonNull List<String> list) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextViewHolder(mInflater.inflate(R.layout.item_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TextViewHolder) holder).mTvText.setText(mList.get(position));
        ((TextViewHolder) holder).mTvText.setTextColor(Color.WHITE);
        switch (getItemViewType(position)) {
            case TYPE_0:
                ((TextViewHolder) holder).mTvText.setBackgroundResource(R.drawable.bg_tag_blue);
                break;
            case TYPE_1:
                ((TextViewHolder) holder).mTvText.setBackgroundResource(R.drawable.bg_tag_orange);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return TYPE_0;
        }
        return TYPE_1;
    }

    class TextViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvText;

        public TextViewHolder(View view) {
            super(view);
            mTvText = (TextView) view.findViewById(R.id.tv_tag);
        }
    }

}
