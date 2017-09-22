package com.example.ningyuwen.music.view.impl;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.ningyuwen.music.presenter.impl.BasePresenter;

/**
 * BaseActivity,所有activity继承BaseActivity
 * Created by ningyuwen on 17-9-22.
 */

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity {
    protected P mPresenter;     //presenter对象，处理逻辑运算和数据存储
    private Toast mToast;       //toast对象

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = getPresenter();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //设置为强制竖屏
    }

    /**
     * 返回一个持有Activity对象的Presenter对象
     *
     * @return 返回的presenter对象
     * @see #onCreate(Bundle) 中调用了该方法,子类只需要复写,不需要调用
     */
    protected abstract P getPresenter();

    /**
     * 显示Toast,该方法已实现,子类只需要调用即可,短时的Toast.
     *打断之前的toast，在显示短时的toast
     * @param message 要显示的信息
     */

    public void showToast(@NonNull String message) {
        if (mToast != null){
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
