package com.example.ningyuwen.music.view.activity.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 启动页
 * Created by ningyuwen on 17-10-8.
 */

public class StartPageActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(StartPageActivity.this, MainActivity.class));
        finish();
    }
}
