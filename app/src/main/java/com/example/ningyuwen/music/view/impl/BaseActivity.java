package com.example.ningyuwen.music.view.impl;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.WindowManager;
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

    /**
     * 设置状态栏透明
     * 适用于DrawerLayout
     *
     * @param drawerLayout drawerLayout
     */
    public void setStatusBarTransparentForDrawerLayout(DrawerLayout drawerLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置内容布局属性
            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
            contentLayout.setFitsSystemWindows(false);
            contentLayout.setClipToPadding(false);
            // 设置抽屉布局属性
            ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
            drawer.setFitsSystemWindows(true);
            drawer.setClipToPadding(true);
            // 设置 DrawerLayout 属性
            drawerLayout.setFitsSystemWindows(false);
        }
    }
}
