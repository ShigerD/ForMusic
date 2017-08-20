package com.example.ningyuwen.music.View;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ningyuwen.music.R;

/**
 * MainActivity,进入App之后的第一个页面，检测数据库中是否有音乐信息，若无，需要扫描一遍存储卡
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
    }

    private void initData() {
//        SharedPreferences sharedPreferences = getSharedPreferences("hasMusic", fa)
    }
}
