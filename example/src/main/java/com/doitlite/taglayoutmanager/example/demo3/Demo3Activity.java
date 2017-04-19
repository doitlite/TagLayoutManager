package com.doitlite.taglayoutmanager.example.demo3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doitlite.taglayoutmanager.TagLayoutManager;
import com.doitlite.taglayoutmanager.example.R;
import com.doitlite.taglayoutmanager.example.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Date: 2017-04-19 10:29
 * Author: chenzc
 */
public class Demo3Activity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mRvTag;

    private List<String> mList;

    private ExpandableAdapter mAdapter;

    private TagLayoutManager mTagLayoutManager;

    private RelativeLayout mRlMore;
    private TextView mTvMore;
    private ImageView mIvArrow;

    private boolean isExpandable;
    private boolean isMoreClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo_3);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRvTag = (RecyclerView) findViewById(R.id.rv_tag);

        mList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            mList.add("Tag " + i);
        }

        mTagLayoutManager = new TagLayoutManager.Builder()
                .setBorderLeft(Utils.dp2px(this, 12))
                .setBorderTop(Utils.dp2px(this, 8))
                .setBorderRight(Utils.dp2px(this, 12))
                .setBorderBottom(Utils.dp2px(this, 8))
                .setBorderHor(Utils.dp2px(this, 8))
                .setBorderVer(Utils.dp2px(this, 8))
                .setNestedScrollingEnabled(false)
                .setMaxLineNum(3)
                .create();

        mAdapter = new ExpandableAdapter(this, mList, mTagLayoutManager);

        mRvTag.setAdapter(mAdapter);

        mRvTag.setLayoutManager(mTagLayoutManager);

        mRlMore = (RelativeLayout) findViewById(R.id.rl_more);
        mTvMore = (TextView) findViewById(R.id.tv_more);
        mIvArrow = (ImageView) findViewById(R.id.iv_arrow);

        mRvTag.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if (mTagLayoutManager.getParams().getMaxLineNum() == 0) {
                    return;
                }
                if (mTagLayoutManager.getMaxVisiableCount() != mAdapter.getItemCount() - 1) {
                    mRlMore.setVisibility(View.VISIBLE);
                } else {
                    mRlMore.setVisibility(View.GONE);
                }
            }
        });

        mRlMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isExpandable) {
                    mTagLayoutManager.getParams().setMaxLineNum(0);
                    mAdapter.notifyDataSetChanged();
                    mTvMore.setText("收起");
                    mIvArrow.setImageResource(R.mipmap.ic_arrow_up);
                } else {
                    mTagLayoutManager.getParams().setMaxLineNum(3);
                    mAdapter.notifyDataSetChanged();
                    mTvMore.setText("更多");
                    mIvArrow.setImageResource(R.mipmap.ic_arrow_down);
                }

                isExpandable = !isExpandable;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
