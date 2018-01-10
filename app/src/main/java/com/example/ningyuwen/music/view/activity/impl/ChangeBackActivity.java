package com.example.ningyuwen.music.view.activity.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.view.activity.i.IChangeBack;

public class ChangeBackActivity extends AppCompatActivity implements IChangeBack , View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_bank);

        ImageView backImg1 = findViewById(R.id.back1);
        ImageView backImg2 = findViewById(R.id.back2);
        ImageView backImg3 = findViewById(R.id.back3);

        backImg1.setOnClickListener(this);
        backImg2.setOnClickListener(this);
        backImg3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.back1:
                //把背景设置为pic_change_bg1;
                Intent intent1 = new Intent();
                intent1.putExtra("backImg","pic_change_bg1");
                setResult(RESULT_OK,intent1);
                finish();
                break;
            case R.id.back2:
                //把背景设置为pic_change_bg2;
                Intent intent2 = new Intent();
                intent2.putExtra("backImg","pic_change_bg2");
                setResult(RESULT_OK,intent2);
                finish();
                break;
            case R.id.back3:
                //把背景设置成pic_change_bg3;
                Intent intent3 = new Intent();
                intent3.putExtra("backImg","pic_change_bg3");
                setResult(RESULT_OK,intent3);
                finish();
                break;

        }
    }

    @Override
    public void ChangeBack() {

    }
}
