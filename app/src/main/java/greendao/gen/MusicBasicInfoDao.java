package greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MUSIC_BASIC_INFO".
*/
public class MusicBasicInfoDao extends AbstractDao<MusicBasicInfo, Long> {

    public static final String TABLENAME = "MUSIC_BASIC_INFO";

    /**
     * Properties of entity MusicBasicInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property PId = new Property(0, long.class, "pId", true, "_id");
        public final static Property MusicName = new Property(1, String.class, "musicName", false, "MUSIC_NAME");
        public final static Property MusicPlayer = new Property(2, String.class, "musicPlayer", false, "MUSIC_PLAYER");
        public final static Property MusicTime = new Property(3, int.class, "musicTime", false, "MUSIC_TIME");
        public final static Property MusicAlbum = new Property(4, String.class, "musicAlbum", false, "MUSIC_ALBUM");
        public final static Property MusicFilePath = new Property(5, String.class, "musicFilePath", false, "MUSIC_FILE_PATH");
        public final static Property MusicFileSize = new Property(6, long.class, "musicFileSize", false, "MUSIC_FILE_SIZE");
    }


    public MusicBasicInfoDao(DaoConfig config) {
        super(config);
    }
    
    public MusicBasicInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MUSIC_BASIC_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: pId
                "\"MUSIC_NAME\" TEXT," + // 1: musicName
                "\"MUSIC_PLAYER\" TEXT," + // 2: musicPlayer
                "\"MUSIC_TIME\" INTEGER NOT NULL ," + // 3: musicTime
                "\"MUSIC_ALBUM\" TEXT," + // 4: musicAlbum
                "\"MUSIC_FILE_PATH\" TEXT," + // 5: musicFilePath
                "\"MUSIC_FILE_SIZE\" INTEGER NOT NULL );"); // 6: musicFileSize
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MUSIC_BASIC_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MusicBasicInfo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getPId());
 
        String musicName = entity.getMusicName();
        if (musicName != null) {
            stmt.bindString(2, musicName);
        }
 
        String musicPlayer = entity.getMusicPlayer();
        if (musicPlayer != null) {
            stmt.bindString(3, musicPlayer);
        }
        stmt.bindLong(4, entity.getMusicTime());
 
        String musicAlbum = entity.getMusicAlbum();
        if (musicAlbum != null) {
            stmt.bindString(5, musicAlbum);
        }
 
        String musicFilePath = entity.getMusicFilePath();
        if (musicFilePath != null) {
            stmt.bindString(6, musicFilePath);
        }
        stmt.bindLong(7, entity.getMusicFileSize());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MusicBasicInfo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getPId());
 
        String musicName = entity.getMusicName();
        if (musicName != null) {
            stmt.bindString(2, musicName);
        }
 
        String musicPlayer = entity.getMusicPlayer();
        if (musicPlayer != null) {
            stmt.bindString(3, musicPlayer);
        }
        stmt.bindLong(4, entity.getMusicTime());
 
        String musicAlbum = entity.getMusicAlbum();
        if (musicAlbum != null) {
            stmt.bindString(5, musicAlbum);
        }
 
        String musicFilePath = entity.getMusicFilePath();
        if (musicFilePath != null) {
            stmt.bindString(6, musicFilePath);
        }
        stmt.bindLong(7, entity.getMusicFileSize());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public MusicBasicInfo readEntity(Cursor cursor, int offset) {
        MusicBasicInfo entity = new MusicBasicInfo( //
            cursor.getLong(offset + 0), // pId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // musicName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // musicPlayer
            cursor.getInt(offset + 3), // musicTime
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // musicAlbum
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // musicFilePath
            cursor.getLong(offset + 6) // musicFileSize
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MusicBasicInfo entity, int offset) {
        entity.setPId(cursor.getLong(offset + 0));
        entity.setMusicName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMusicPlayer(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMusicTime(cursor.getInt(offset + 3));
        entity.setMusicAlbum(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMusicFilePath(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setMusicFileSize(cursor.getLong(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MusicBasicInfo entity, long rowId) {
        entity.setPId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MusicBasicInfo entity) {
        if(entity != null) {
            return entity.getPId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MusicBasicInfo entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
