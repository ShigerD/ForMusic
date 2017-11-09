package com.example.ningyuwen.music.presenter.impl;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.model.entity.music.MusicRecordInfo;
import com.example.ningyuwen.music.presenter.i.IMainActivityPresenter;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import greendao.gen.MusicBasicInfoDao;
import greendao.gen.MusicRecordInfoDao;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * MainActivityPresenter对应MainActivity
 * Created by ningyuwen on 17-9-22.
 */

public class MainActivityPresenter extends BasePresenter<MainActivity>
        implements IMainActivityPresenter {
    public MainActivityPresenter(MainActivity view) {
        super(view);
    }

    private String TAG = "ningywuen";

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
     * 获取音乐数据，包括MusicBasicInfo 和 MusicRecordInfoDao中的数据，通过pid查询，存储到 musicDataList返回
     * @param basicInfoList basicInfo
     * @return musicDataList
     */
    @Override
    public List<MusicData> getMusicAllInfo(List<MusicBasicInfo> basicInfoList) {
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
     * 从数据库获取数据
     * @return List<MusicData>
     */
    @Override
    public List<MusicData> getMusicBasicInfoFromDB() {
        List<MusicBasicInfo> musicBasicInfoList = mDaoSession.getMusicBasicInfoDao().loadAll();
        if (musicBasicInfoList == null || musicBasicInfoList.size() == 0){
            return new ArrayList<>();
        }
        return getMusicAllInfo(musicBasicInfoList);
    }

    /**
     * 获取歌单信息
     * @return
     */
    @Override
    public List<SongListInfo> getSongListInfoFromDB() {
        List<SongListInfo> songListInfoList = mDaoSession.getSongListInfoDao().loadAll();
        if (songListInfoList == null || songListInfoList.size() == 0){
            return new ArrayList<>();
        }
        return songListInfoList;
    }

    @Override
    public void addSongListToDB(SongListInfo info) {
        mDaoSession.getSongListInfoDao().insertOrReplace(info);
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
     * 将音乐歌词文件路径存储到对应的音乐数据中
     * @param musicName musicName
     * @param musicPlayer musicPlayer
     * @param musicPic 专辑图片
     * @param filePath 歌词文件路径
     */
    @Override
    public void addLrcPathAndMusicPicToDB(String musicName, String musicPlayer,
                                          String musicPic, String filePath) {
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
            musicBasicInfo.setMusicLyricPath(filePath);     //歌词路径
            musicBasicInfo.setMusicAlbumPicUrl(musicPic);   //专辑图片
            mDaoSession.getMusicBasicInfoDao().insertOrReplace(musicBasicInfo);
        }
        cursor.close();
        db.close();


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
                    StringBuffer sb = new StringBuffer();
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
     * 传入歌词string，文本文件路径，用于解析歌词文件
     * @param lyricStr string
     * @param filePath string:文件路径
     */
    private void analysisLyric(String lyricStr, String filePath){
        try {
            JSONObject jsonObject = (JSONObject) JSON.parse(lyricStr);
            String[] musicPlayerInfo = jsonObject.getString("kalaokLyric").split("\n", 5);
            for (int i = 0;i < 4;i++){
                musicPlayerInfo[i] = musicPlayerInfo[i]
                        .replace("[", "")
                        .replace("]","");
            }

            String musicName = musicPlayerInfo[0].replace("ti:", "");
            String musicPlayer = musicPlayerInfo[1].replace("ar:", "");

            //在数据库中查询音乐名和音乐人对应的歌曲名
//            addLyricPathToDB(musicName, musicPlayer, filePath);

        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "run: 异常");

//            //取出musicID  http://music.163.com/#/song?id=
//            JSONObject jsonObject = (JSONObject) JSON.parse(lyricStr);
//            String musicId = jsonObject.getString("musicId");
//            Log.i(TAG, "analysisLyric: " + getMusicNameFromNet(musicId));

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
                    addLrcPathAndMusicPicToDB(musicName, musicPlayer, musicPic, filePath);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
