package com.example.ningyuwen.music;

import android.app.Application;
import android.app.Notification;
import android.util.Log;

import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import greendao.GreenDaoHelper;
import greendao.gen.DaoMaster;
import greendao.gen.DaoSession;

import static android.content.ContentValues.TAG;

/**
 * application  app的入口
 * 线程池在此处调用
 * Created by ningyuwen on 17-9-22.
 */

public class MusicApplication extends Application {
    private static DaoSession daoSession;
    private static MusicApplication application;
//    public static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    //现在使用newFixedThreadPool类型的线程池，线程一直存在，线程数为3,当线程池中没有空闲线程时，进入线程队列等待
    //MusicApplication.getFixedThreadPool().execute(Runnable);
    private static ExecutorService fixedThreadPool;

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "1c9b07d4ed", true);
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

    public static ExecutorService getFixedThreadPool(){
        if (fixedThreadPool == null || fixedThreadPool.isShutdown()){
            fixedThreadPool = Executors.newFixedThreadPool(3);
        }
        return fixedThreadPool;
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
