package greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import greendao.gen.DaoMaster;

/**
 * GreenDao数据库帮助类
 * 在这里进行数据库升级等操作
 * Created by ningyuwen on 17-9-22.
 */

public class GreenDaoHelper extends DaoMaster.OpenHelper{

    private static final String DB_NAME = "music.db";      //数据库名字

    public GreenDaoHelper(Context context) {
        super(context, DB_NAME, null);
    }

//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        super.onUpgrade(db, oldVersion, newVersion);
////        DaoMaster.dropAllTables((new DaoMaster(db)).getDatabase(), true);
////        onCreate(db);
//
//        if (oldVersion != newVersion) {
//            //升级数据库,修改build.gradle中的versioncode 和 schemaVersion , 统一
//            Log.i("test", "onUpgrade: 测试升级 " + "old " + oldVersion + " " + newVersion);
//            try {
//                DaoMaster.dropAllTables((new DaoMaster(db)).getDatabase(), true);
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
//            DaoMaster.createAllTables((new DaoMaster(db)).getDatabase(), true);
//        }
//    }
}
