package com.doitlite.taglayoutmanager.example.demo1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.doitlite.taglayoutmanager.example.R;
import com.doitlite.taglayoutmanager.TagLayoutManager;
import com.doitlite.taglayoutmanager.example.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Date: 2017-04-06 13:15
 * Author: chenzc
 */
public class Demo1Activity extends AppCompatActivity {

    public static final String TAG = Demo1Activity.class.getSimpleName();

    private Toolbar mToolbar;

    private RecyclerView mRvTag;

    private List<String> mList;

    private TagAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_1);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRvTag = (RecyclerView) findViewById(R.id.rv_tag);

        mList = new ArrayList<>();

        mAdapter = new TagAdapter(this, mList, new TagAdapter.OnTagClickListener() {
            @Override
            public void onRemoved(String removedTag) {
                Log.i(TAG, "onRemoved: =============removed tag:" + removedTag);
            }

            @Override
            public void onDataSetChange() {
                mRvTag.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        });

        mRvTag.setLayoutManager(new TagLayoutManager.Builder()
                .setBorderLeft(Utils.dp2px(this, 12))
                .setBorderTop(Utils.dp2px(this, 8))
                .setBorderRight(Utils.dp2px(this, 12))
                .setBorderBottom(Utils.dp2px(this, 8))
                .setBorderHor(Utils.dp2px(this, 8))
                .setBorderVer(Utils.dp2px(this, 4))
                .setMaxHeight(Utils.dp2px(this, 100))
                .create());

        mRvTag.setAdapter(mAdapter);

        // avoid adapter call notifyItemChanged then the other item flashing
        ((SimpleItemAnimator)mRvTag.getItemAnimator()).setSupportsChangeAnimations(false);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getWindow().getDecorView();
            if(isBlankTouched(view, ev.getX(), ev.getY())){
                mAdapter.inputDone();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isBlankTouched(View view, float x, float y){
        boolean isBlankTouched = true;
        if(isContained(view, x, y)) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for(int i = 0; i < viewGroup.getChildCount(); i++){
                    isBlankTouched = isBlankTouched(viewGroup.getChildAt(i), x, y);
                    if(!isBlankTouched){
                        break;
                    }
                }
            } else {
                if(view instanceof EditText || view instanceof AppCompatEditText
                        || view instanceof TextView || view instanceof AppCompatTextView){
                    isBlankTouched  = false;
                }
            }
        }
        return isBlankTouched;
    }

    public boolean isContained(View view, float x, float y){
        boolean thisView = false;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xl = location[0];
        int yl = location[1];
        if(x > xl
                && x < xl + view.getWidth()
                && y > yl
                && y < yl + view.getHeight()){
            thisView = true;
        }
        return thisView;
    }

}
