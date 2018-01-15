package com.example.ningyuwen.music.view.activity.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.ningyuwen.music.R;

public class ChangeBackActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_bank);

        ImageView backImg1 = findViewById(R.id.back1);
        ImageView backImg2 = findViewById(R.id.back2);
        ImageView backImg3 = findViewById(R.id.back3);
        ImageView backImg4 = findViewById(R.id.back4);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backImg1.setOnClickListener(this);
        backImg2.setOnClickListener(this);
        backImg3.setOnClickListener(this);
        backImg4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.back1:
                //把背景设置为pic_change_bg1;
                getSharedPreferences("backImg", Context.MODE_PRIVATE).edit().putInt("backImgId",1).apply();
                Intent intent1 = new Intent();
                intent1.putExtra("backImg","pic_change_bg1");
                setResult(RESULT_OK,intent1);
                finish();
                break;
            case R.id.back2:
                //把背景设置为pic_change_bg2;
                getSharedPreferences("backImg", Context.MODE_PRIVATE).edit().putInt("backImgId",2).apply();
                Intent intent2 = new Intent();
                intent2.putExtra("backImg","pic_change_bg2");
                setResult(RESULT_OK,intent2);
                finish();
                break;
            case R.id.back3:
                //把背景设置成pic_change_bg3;
                getSharedPreferences("backImg", Context.MODE_PRIVATE).edit().putInt("backImgId",3).apply();
                Intent intent3 = new Intent();
                intent3.putExtra("backImg","pic_change_bg3");
                setResult(RESULT_OK,intent3);
                finish();
                break;
            case R.id.back4:
                //把背景设置成默认背景;
                getSharedPreferences("backImg", Context.MODE_PRIVATE).edit().putInt("backImgId",0).apply();
                Intent intent4 = new Intent();
                intent4.putExtra("backImg","pic_main_bg");
                setResult(RESULT_OK,intent4);
                finish();
                break;
        }
    }
}
