package com.doitlite.taglayoutmanager.example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.doitlite.taglayoutmanager.example.demo1.Demo1Activity;
import com.doitlite.taglayoutmanager.example.demo2.Demo2Activity;
import com.doitlite.taglayoutmanager.example.demo3.Demo3Activity;
import com.doitlite.taglayoutmanager.example.demo4.Demo4Activity;

/**
 * Description:
 * Date: 2017-04-06 12:36
 * Author: chenzc
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        findViewById(R.id.btn_demo_1).setOnClickListener(this);
        findViewById(R.id.btn_demo_2).setOnClickListener(this);
        findViewById(R.id.btn_demo_3).setOnClickListener(this);
        findViewById(R.id.btn_demo_4).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_demo_1:
                startActivity(new Intent(this, Demo1Activity.class));
                break;
            case R.id.btn_demo_2:
                startActivity(new Intent(this, Demo2Activity.class));
                break;
            case R.id.btn_demo_3:
                startActivity(new Intent(this, Demo3Activity.class));
                break;
            case R.id.btn_demo_4:
                startActivity(new Intent(this, Demo4Activity.class));
                break;
        }
    }

}
