package com.example.ningyuwen.music.view.activity.impl;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    private Snackbar mSnacker;       //toast对象

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

    public void showToast(@NonNull View view, @NonNull String message) {
        if (mSnacker != null){
            mSnacker.dismiss();
        }
//        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mSnacker = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        mSnacker.show();
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

    /**
     * 设置状态栏透明
     * 使用于非为DrawerLayout.当以图片作为背景时,图片会铺满全屏
     */
    public void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //设置状态栏透明
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置跟布局的参数,让布局从状态栏下方开始,而不是跟状态栏重合
            ViewGroup rootView = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(false);
            rootView.setClipToPadding(false);
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    private int getStatusBarHeight() {
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return this.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取一个和状态栏大小相同的View
     *
     * @param color view的颜色,ARGB值
     * @return view
     */
    public View createStatusBarView(int color) {
        //绘制一个和状态栏大小相同的view
        View statusView = new View(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);              //设置背景色
        return statusView;
    }

    /**
     * 设置状态栏颜色
     * 适用于非DrawerLayout,布局包含toolbar时，可以将状态栏设置为何toolbar相同的颜色
     *
     * @param color 设置的状态栏的颜色ARGB值
     */
    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //设置状态栏透明
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View statusView = createStatusBarView(color);  //生成一个和状态栏一样大小的View
            ViewGroup decorView = (ViewGroup) this.getWindow().getDecorView(); //获取布局容器
            decorView.addView(statusView);  //添加View到布局中
            //设置跟布局的参数,让布局从状态栏下方开始,而不是跟状态栏重合
            ViewGroup rootView = (ViewGroup) ((ViewGroup) this
                    .findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }
}
