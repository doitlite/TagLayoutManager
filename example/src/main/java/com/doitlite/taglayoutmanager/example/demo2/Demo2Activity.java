package com.doitlite.taglayoutmanager.example.demo2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.doitlite.taglayoutmanager.TagLayoutManager;
import com.doitlite.taglayoutmanager.example.R;
import com.doitlite.taglayoutmanager.example.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Date: 2017-04-18 19:04
 * Author: chenzc
 */
public class Demo2Activity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mRvTag;

    private List<String> mList;

    private TextAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo_2);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRvTag = (RecyclerView) findViewById(R.id.rv_tag);

        mList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mList.add("Tag " + i);
        }

        mAdapter = new TextAdapter(this, mList);

        mRvTag.setAdapter(mAdapter);

        mRvTag.setLayoutManager(new TagLayoutManager.Builder()
                .setBorderLeft(Utils.dp2px(this, 12))
                .setBorderTop(Utils.dp2px(this, 8))
                .setBorderRight(Utils.dp2px(this, 12))
                .setBorderBottom(Utils.dp2px(this, 8))
                .setBorderHor(Utils.dp2px(this, 8))
                .setBorderVer(Utils.dp2px(this, 8))
                .setNestedScrollingEnabled(false)
                .create());
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
