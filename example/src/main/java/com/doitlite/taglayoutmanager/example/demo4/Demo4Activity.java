package com.doitlite.taglayoutmanager.example.demo4;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.doitlite.taglayoutmanager.TagLayoutManager;
import com.doitlite.taglayoutmanager.example.R;
import com.doitlite.taglayoutmanager.example.demo2.TextAdapter;
import com.doitlite.taglayoutmanager.example.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Date: 2017-04-19 14:11
 * Author: chenzc
 */
public class Demo4Activity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mRvTag;

    private List<String> mList;

    private TextAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_4);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRvTag = (RecyclerView) findViewById(R.id.rv_tag);

        mList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
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
