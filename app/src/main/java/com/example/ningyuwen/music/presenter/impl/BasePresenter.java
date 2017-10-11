package com.example.ningyuwen.music.presenter.impl;

import com.example.ningyuwen.music.MusicApplication;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;

import greendao.gen.DaoSession;

/**
 * BasePresenter
 * Created by ningyuwen on 17-9-22.
 */

public class BasePresenter<V extends BaseActivity> {
    V mView;    //Presenter所持有的Activity对象
    DaoSession mDaoSession;   //数据库session

    public BasePresenter(V view){
        mView = view;
        getDaoSession();
    }

    /**
     * 获取daosession
     * @return mDaoSession
     */
    private DaoSession getDaoSession() {
        if (mDaoSession == null) {
//            mDaoSession = ((MusicApplication) mView.getApplication()).getDaoSession();
            mDaoSession = MusicApplication.getApplication().getDaoSession();
        }
        return mDaoSession;
    }


}
