package com.example.ningyuwen.music;

import android.app.Application;
import android.util.Log;

import java.io.File;

import greendao.GreenDaoHelper;
import greendao.gen.DaoMaster;
import greendao.gen.DaoSession;

import static android.content.ContentValues.TAG;

/**
 * application  app的入口
 * Created by ningyuwen on 17-9-22.
 */

public class MusicApplication extends Application {
    private static DaoSession daoSession;
    private static MusicApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        daoSession = getDaoSession();

    }

    public static MusicApplication getApplication(){
        return application;
    }

    /**
     * 获取greenDao的daoSession
     *
     * @return daoSession
     */
    public DaoSession getDaoSession() {
        if (daoSession == null) {
            GreenDaoHelper helper = new GreenDaoHelper(this);
            DaoMaster daoMaster = new DaoMaster(helper.getWritableDb());
            daoSession = daoMaster.newSession();
            Log.i("ning", "getDaoSession: 0");
        }
        return daoSession;
    }

//    public DaoMaster getDaoMaster()
//    {
//        if (daoMaster == null)
//        {
//            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(this, dbName, null);
//            daoMaster = new DaoMaster(helper.getWritableDatabase());
//        }
//        return daoMaster;
//    }
//
//    public DaoSession getDaoSession()
//    {
//        if (daoSession == null)
//        {
//            if (daoMaster == null)
//            {
//                daoMaster = getDaoMaster();
//            }
//            daoSession = daoMaster.newSession();
//        }
//        return daoSession;
//    }



}
