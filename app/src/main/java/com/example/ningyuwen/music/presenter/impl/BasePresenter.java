package com.example.ningyuwen.music.presenter.impl;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.ningyuwen.music.MusicApplication;
import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.model.entity.music.MusicRecordInfo;
import com.example.ningyuwen.music.presenter.i.IBasePresenter;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import greendao.gen.DaoSession;
import greendao.gen.MusicBasicInfoDao;
import greendao.gen.MusicRecordInfoDao;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * BasePresenter
 * Created by ningyuwen on 17-9-22.
 */

public class BasePresenter<V extends BaseActivity> implements IBasePresenter {
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

    /**
     * 从数据库获取数据
     * @return List<MusicData>
     */
    @Override
    public List<MusicData> getMusicBasicInfoFromDB() {
        List<MusicBasicInfo> musicBasicInfoList = mDaoSession.getMusicBasicInfoDao().loadAll();
        if (musicBasicInfoList == null || musicBasicInfoList.size() == 0){
            return new ArrayList<>();
        }
        return getMusicAllInfoFromBasic(musicBasicInfoList);
    }

    /**
     * 获取音乐数据，包括MusicBasicInfo 和 MusicRecordInfoDao中的数据，通过pid查询，存储到 musicDataList返回
     * @param basicInfoList basicInfo
     * @return musicDataList
     */
    @Override
    public List<MusicData> getMusicAllInfoFromBasic(List<MusicBasicInfo> basicInfoList) {
        List<MusicData> musicDataList = new ArrayList<>();
        for (int i = 0;i < basicInfoList.size();i++){
            MusicData data = new MusicData();
            data.setpId(basicInfoList.get(i).getPId());
            data.setMusicName(basicInfoList.get(i).getMusicName());
            data.setMusicPlayer(basicInfoList.get(i).getMusicPlayer());
            data.setMusicAlbum(basicInfoList.get(i).getMusicAlbum());
            data.setMusicFilePath(basicInfoList.get(i).getMusicFilePath());
            data.setMusicTime(basicInfoList.get(i).getMusicTime());
            data.setMusicFileSize(basicInfoList.get(i).getMusicFileSize());
            data.setMusicAlbumPicUrl(basicInfoList.get(i).getMusicAlbumPicUrl());
            data.setMusicAlbumPicPath(basicInfoList.get(i).getMusicAlbumPicPath());

            //通过pid在表recordInfo中查询信息存储到
            MusicRecordInfo recordInfo = mDaoSession.getMusicRecordInfoDao().load(basicInfoList.get(i).getPId());
            data.setMusicPlayTimes(recordInfo.getMusicPlayTimes());
            data.setLove(recordInfo.getIsLove());
            data.setMusicSongList(recordInfo.getMusicSongList());
            musicDataList.add(data);
        }
        return musicDataList;
    }

    /**
     * 获取音乐数据，包括MusicBasicInfo 和 MusicRecordInfoDao中的数据，通过pid查询，存储到 musicDataList返回
     * @param recordInfos recordInfos
     * @return musicDataList
     */
    public List<MusicData> getMusicAllInfoFromRecord(List<MusicRecordInfo> recordInfos) {
        List<MusicData> musicDataList = new ArrayList<>();
        for (int i = 0;i < recordInfos.size();i++){
            MusicData data = new MusicData();
            MusicBasicInfo basicInfo = mDaoSession.getMusicBasicInfoDao().load(recordInfos.get(i).getPId());    //当前音乐的记录信息，根据记录信息获取基本信息

            data.setpId(recordInfos.get(i).getPId());
            data.setMusicName(basicInfo.getMusicName());
            data.setMusicPlayer(basicInfo.getMusicPlayer());
            data.setMusicAlbum(basicInfo.getMusicAlbum());
            data.setMusicFilePath(basicInfo.getMusicFilePath());
            data.setMusicTime(basicInfo.getMusicTime());
            data.setMusicFileSize(basicInfo.getMusicFileSize());
            data.setMusicAlbumPicUrl(basicInfo.getMusicAlbumPicUrl());
            data.setMusicAlbumPicPath(basicInfo.getMusicAlbumPicPath());

            //加上记录信息
            data.setMusicPlayTimes(recordInfos.get(i).getMusicPlayTimes());
            data.setLove(recordInfos.get(i).getIsLove());
            data.setMusicSongList(recordInfos.get(i).getMusicSongList());
            musicDataList.add(data);
        }
        return musicDataList;
    }

    private static String TAG = "ningyuwen";

    @Override
    public void saveMusicInfoFromSD(List<MusicBasicInfo> musicDatas) {
        //dao 基本信息
        if (musicDatas == null || musicDatas.size() == 0){
            Log.i(TAG, "saveMusicInfoFromSD: 0 ");
            return;
        }
        Log.i(TAG, "saveMusicInfoFromSD: 1  " + musicDatas.size());
        MusicBasicInfoDao basicInfoDao = mDaoSession.getMusicBasicInfoDao();
        basicInfoDao.insertOrReplaceInTx(musicDatas);

        MusicRecordInfoDao recordInfoDao = mDaoSession.getMusicRecordInfoDao();
        //插入或替换记录数据
        for (int i = 0;i < musicDatas.size();i++){
            MusicRecordInfo info = new MusicRecordInfo();
            info.setPId(musicDatas.get(i).getPId());
            recordInfoDao.insertOrReplace(info);
        }
    }

    /**
     * 用pid查询音乐数据基本信息
     * @param pid pid
     * @return MusicBasicInfo
     */
    @Override
    public MusicBasicInfo getMusicDataUsePid(long pid) throws NullPointerException{
        return mDaoSession.getMusicBasicInfoDao().load(pid);
    }

    /**
     * 获取歌词数据
     * @param musicBasicInfo long pid
     * @return string
     */
    @Override
    public String getLyricFromDBUsePid(MusicBasicInfo musicBasicInfo) {
        String lyricPath = musicBasicInfo.getMusicLyricPath();  //歌词文件路径
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(lyricPath));
            byte[] buf = new byte[1024];
            StringBuilder sb = new StringBuilder();
            while((inputStream.read(buf)) != -1){
                sb.append(new String(buf));
                buf = new byte[1024];//重新生成，避免和上次读取的数据重复
            }
            //歌词string
            String string = sb.toString();  //歌词文件文本
            inputStream.close();

            //json解析
            JSONObject jsonObject = (JSONObject) JSON.parse(string);
            return jsonObject.getString("lyric");   //歌词
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 設置是否喜愛，在所有歌曲頁面和我喜愛的音樂頁面都有用到
     * @param pid pid
     * @param isLove true,false
     */
    @Override
    public void setIsLoveToDB(long pid, boolean isLove) {
        MusicRecordInfo info = mDaoSession.getMusicRecordInfoDao().load(pid);
        info.setIsLove(isLove);
        mDaoSession.getMusicRecordInfoDao().insertOrReplace(info);
    }

    /**
     * 暂时解决几大音乐播放器歌词的问题
     * 网易：获取到本地歌词文件解析出 musicID之后访问网易云音乐获取音乐名，然后存储歌词路径
     * 虾米音乐：解析本地音乐文件就能解决
     * QQ音乐：
     * 酷狗音乐：
     * 网易云音乐、QQ音乐、虾米音乐等几款音乐播放器歌词路径
     */
    @Override
    public void scanLyricFileFromSD() throws IOException {
        getAlbumPicNetease();
        getLyricFromNetease();
        getLyricFromXiami();
    }

    /**
     * 获取头像
     */
    private void getAlbumPicNetease() {
        List<MusicBasicInfo> basicInfos = mDaoSession.getMusicBasicInfoDao().loadAll();
        for (int i = 0;i < basicInfos.size();i++) {
            long albumId = basicInfos.get(i).getMusicAlbumId();
//            long songId = basicInfos.get(i).getPId();
            basicInfos.get(i).setMusicAlbumPicPath(getAlbumArt(String.valueOf(albumId)));
//            basicInfos.get(i).setMusicAlbumPicPath(getArtwork(songId, albumId));
        }
        mDaoSession.getMusicBasicInfoDao().insertOrReplaceInTx(basicInfos);
    }

    /**
     * 虾米音乐
     */
    private void getLyricFromXiami() throws IOException {
        File neteaseFile = new File("/storage/emulated/0/xiami/lyrics");
        if (neteaseFile.exists() && neteaseFile.canRead()){
            //文件存在
            File[] files = neteaseFile.listFiles();
            FileInputStream in = null;
            for (File file:files){
                try {
                    // read file content from file
                    StringBuilder sb= new StringBuilder("");
                    FileReader reader = new FileReader(file);
                    BufferedReader br = new BufferedReader(reader);
                    String str = null;
                    String musicName = "", musicPlayer = "";
//                    String regx1 = "\\[\\d{2}:\\d{2}.\\d{3}\\]";
//                    String regx = "\\[ti:\\]";
//                    Pattern p = Pattern.compile(regx);
                    while((str = br.readLine()) != null) {
                        sb.append(str).append("/n");
                        if (str.contains("ti:")){
                            musicName = str.replace("[", "")
                                    .replace("]","")
                                    .replace("ti:","");
                        }
                        if (str.contains("ar:")){
                            musicPlayer = str.replace("[", "")
                                    .replace("]","")
                                    .replace("ar:","");
                            break;
                        }
                    }
                    br.close();
                    reader.close();

                    //根据音乐名在数据库中查询，再将musicPic存储到数据库
                    //在数据库中查询音乐名和音乐人对应的歌曲名
                    addLrcPathAndMusicPicToDB(musicName, musicPlayer, "", file.getPath(),
                            "xiami");
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 网易云音乐
     */
    private void getLyricFromNetease() throws IOException {
        File neteaseFile = new File("/storage/emulated/0/netease/cloudmusic/Download/Lyric");
        if (neteaseFile.exists() && neteaseFile.canRead()){
            //文件存在
            File[] files = neteaseFile.listFiles();
            FileInputStream in = null;
            for (File file:files){
                Log.i(TAG, "scanLyricFileFromSD: " + file.getPath());
                try {
                    in = new FileInputStream(file);
                    byte[] buf = new byte[1024];
                    StringBuilder sb = new StringBuilder();
                    while((in.read(buf)) != -1){
                        sb.append(new String(buf));
                        buf = new byte[1024];//重新生成，避免和上次读取的数据重复
                    }
                    //歌词string
                    String string = sb.toString();

                    //取出musicID  http://music.163.com/#/song?id=
                    JSONObject jsonObject = (JSONObject) JSON.parse(string);
                    String musicId = jsonObject.getString("musicId");
                    //从api根据musicId查询音乐名
                    getMusicNameFromNet(musicId, file.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * 从网易云获取音乐名
     * http://music.163.com/api/song/detail/?id=25706282&ids=[25706282]
     */
    private void getMusicNameFromNet(final String musicId, final String filePath){
        //访问的url
        String url = "http://music.163.com/api/song/detail/?id=" + musicId + "&ids=[" + musicId + "]";

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36")
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .addHeader("Host", "music.163.com")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Cache-Control", "max-age=0")
                    .addHeader("Upgrade-Insecure-Requests", "1")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                    .build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.i(TAG, "onFailure: 获取歌词失败");
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    // 注：该回调是子线程，非主线程
                    String string = response.body().string();

                    //解析response中的歌曲名，然后存储对应的歌曲名的屈词路径
                    JSONObject jsonObject = (JSONObject) JSON.parse(string);
                    JSONArray songs = jsonObject.getJSONArray("songs");

                    //音乐名
                    String musicName = ((JSONObject)songs.get(0)).getString("name");
                    Log.i(TAG, "onResponse: musicName  " + musicName);

                    //音乐专辑图片URL,在播放页面可以使用Glide直接加载
                    JSONObject albumObj = ((JSONObject)songs.get(0)).getJSONObject("album");
                    String musicPic = albumObj.getString("picUrl");
                    Log.i(TAG, "onResponse: " + musicPic);

                    JSONArray artistsObj = ((JSONObject)songs.get(0)).getJSONArray("artists");
                    String musicPlayer = ((JSONObject)artistsObj.get(0)).getString("name");
                    Log.i(TAG, "onResponse: " + musicPlayer);

                    //根据音乐名在数据库中查询，再将musicPic存储到数据库
                    //在数据库中查询音乐名和音乐人对应的歌曲名
                    addLrcPathAndMusicPicToDB(musicName, musicPlayer, musicPic, filePath,
                            "netease");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析歌词
     * @param lyric lyric
     */
    @Override
    public List<Pair<Long, String>> analysisLyric(String lyric) {
        String[] strings = lyric.split("\n");
        Log.i(TAG, "analysisLyric: " + strings.length);

        //List，用于存储歌词的时间和歌词
        List<Pair<Long, String>> timeAndLyric = new ArrayList<>();

        String regx="\\[\\d{2}:\\d{2}.\\d{3}\\]";
        Pattern p = Pattern.compile(regx);

        for (int i = 0;i < strings.length;i++) {
            Matcher m = p.matcher(strings[i]);
            if(!m.find()){
                Log.i(TAG, "analysisLyric: 输入格式不符合要求" );
            }else{
                //有时间，解析
                Pair<Long, String> pair;  //一个新的对象，存储时间和歌词
                String[] str = strings[i].split("\\[|\\]");

//                Log.i(TAG, "analysisLyric: " + str[1].split("\\:|\\.")[0] + str[1].split("\\:|\\.")[1] +
//                        str[1].split("\\:|\\.")[2]);

                //歌词时间
                long time = Long.parseLong(str[1].split("\\:|\\.")[0])*60*1000 +
                        Long.parseLong(str[1].split("\\:|\\.")[1])*1000 +
                        Long.parseLong(str[1].split("\\:|\\.")[2]);
                try {
                    Log.i(TAG, "analysisLyric: " + time + " " + str[2]);
                    pair = Pair.create(time, str[2]);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i(TAG, "analysisLyric: 异常 " + time + " " + str[1]);
                    pair = Pair.create(time, "");
                }
                timeAndLyric.add(pair);  //添加到List
            }
        }
        return timeAndLyric;
    }

    /**
     * 将音乐歌词文件路径存储到对应的音乐数据中
     * @param musicName musicName
     * @param musicPlayer musicPlayer
     * @param musicPicUrl 专辑图片
     * @param lyricFilePath 歌词文件路径
     */
    @Override
    public void addLrcPathAndMusicPicToDB(String musicName, String musicPlayer,
                                          String musicPicUrl, String lyricFilePath, String whichApp) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                "/data/data/com.example.ningyuwen.music/databases/music.db",null);

        Cursor cursor = db.rawQuery("select * from MUSIC_BASIC_INFO where MUSIC_NAME=?" +
                " and MUSIC_PLAYER=?", new String[]{musicName, musicPlayer});
        if (cursor.getCount() == 0){
            Log.i(TAG, "addLyricPathToDB: 查询结果没有词条音乐");
        }else {
            Log.i(TAG, "addLyricPathToDB: 查询结果有有有有有有有有词条音乐");
            cursor.moveToNext();
//            Log.i(TAG, "addLyricPathToDB: " + cursor.getLong(0));

            MusicBasicInfo musicBasicInfo = mDaoSession.getMusicBasicInfoDao().readEntity(cursor,0);
            musicBasicInfo.setMusicLyricPath(lyricFilePath);     //歌词路径

//            if ("netease".equals(whichApp)) { //网易云音乐
//                musicBasicInfo.setMusicAlbumPicUrl(musicPicUrl);   //专辑图片
//                musicBasicInfo.setWhichApp("netease");
//            }else if ("xiami".equals(whichApp)){
//                if (!"".equals(musicBasicInfo.getMusicAlbumPicUrl())) {
//                    String albumPicPath = musicBasicInfo.getMusicAlbumId();
//                    musicBasicInfo.setMusicAlbumPicPath(getAlbumArt(albumPicPath));
//                    musicBasicInfo.setWhichApp("xiami");
//                }
//            }

//            String albumPicPath = musicBasicInfo.getMusicAlbumId();
//            musicBasicInfo.setMusicAlbumPicPath(getAlbumArt(albumPicPath));
            musicBasicInfo.setWhichApp(whichApp);
            mDaoSession.getMusicBasicInfoDao().insertOrReplace(musicBasicInfo);
        }
        cursor.close();
        db.close();
    }

    /**
     * 获取专辑图片
     * @param album_id 专辑id
     * @return path
     */
    private String getAlbumArt(String album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] { "album_art" };
        Cursor cur = mView.getContentResolver().query(Uri.parse(mUriAlbums + "/" +
                        album_id),  projection, null,
                null, null);
        String album_art = null;
        if (cur != null && cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        if (cur != null) {
            cur.close();
        }
        cur = null;
        return album_art;
    }

    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    /**
     * 获取专辑封面位图对象
     */
    public String getArtwork(long song_id, long album_id) {
        if (album_id < 0) {
            if (song_id < 0) {
                String bm = getArtworkFromFile(song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
        }
        ContentResolver res = mView.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
        return uri.getPath();
    }

    /**
     * 从文件当中获取专辑封面位图
     */
    private String getArtworkFromFile(long songid, long albumid) {
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        Uri uri = null;
        if (albumid < 0) {
            uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");

        } else {
            uri = ContentUris.withAppendedId(albumArtUri, albumid);
        }
        if (uri == null){
            return "";
        }
        return uri.getPath();
    }

}
