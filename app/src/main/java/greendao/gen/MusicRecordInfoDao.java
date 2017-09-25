package greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.example.ningyuwen.music.model.entity.music.MusicRecordInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MUSIC_RECORD_INFO".
*/
public class MusicRecordInfoDao extends AbstractDao<MusicRecordInfo, Long> {

    public static final String TABLENAME = "MUSIC_RECORD_INFO";

    /**
     * Properties of entity MusicRecordInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property PId = new Property(0, long.class, "pId", true, "_id");
        public final static Property MusicPlayTimes = new Property(1, int.class, "musicPlayTimes", false, "MUSIC_PLAY_TIMES");
        public final static Property IsLove = new Property(2, boolean.class, "isLove", false, "IS_LOVE");
        public final static Property MusicSongList = new Property(3, String.class, "musicSongList", false, "MUSIC_SONG_LIST");
    }


    public MusicRecordInfoDao(DaoConfig config) {
        super(config);
    }
    
    public MusicRecordInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MUSIC_RECORD_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: pId
                "\"MUSIC_PLAY_TIMES\" INTEGER NOT NULL ," + // 1: musicPlayTimes
                "\"IS_LOVE\" INTEGER NOT NULL ," + // 2: isLove
                "\"MUSIC_SONG_LIST\" TEXT);"); // 3: musicSongList
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MUSIC_RECORD_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MusicRecordInfo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getPId());
        stmt.bindLong(2, entity.getMusicPlayTimes());
        stmt.bindLong(3, entity.getIsLove() ? 1L: 0L);
 
        String musicSongList = entity.getMusicSongList();
        if (musicSongList != null) {
            stmt.bindString(4, musicSongList);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MusicRecordInfo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getPId());
        stmt.bindLong(2, entity.getMusicPlayTimes());
        stmt.bindLong(3, entity.getIsLove() ? 1L: 0L);
 
        String musicSongList = entity.getMusicSongList();
        if (musicSongList != null) {
            stmt.bindString(4, musicSongList);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public MusicRecordInfo readEntity(Cursor cursor, int offset) {
        MusicRecordInfo entity = new MusicRecordInfo( //
            cursor.getLong(offset + 0), // pId
            cursor.getInt(offset + 1), // musicPlayTimes
            cursor.getShort(offset + 2) != 0, // isLove
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // musicSongList
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MusicRecordInfo entity, int offset) {
        entity.setPId(cursor.getLong(offset + 0));
        entity.setMusicPlayTimes(cursor.getInt(offset + 1));
        entity.setIsLove(cursor.getShort(offset + 2) != 0);
        entity.setMusicSongList(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MusicRecordInfo entity, long rowId) {
        entity.setPId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MusicRecordInfo entity) {
        if(entity != null) {
            return entity.getPId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MusicRecordInfo entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
