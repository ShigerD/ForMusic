package com.example.ningyuwen.music.view.impl;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.presenter.impl.MainActivityPresenter;

import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * 主页面，音乐播放，扫描音乐等
 * Created by ningyuwen on 17-9-22.
 */

public class MainActivity extends BaseActivity<MainActivityPresenter> {
    List<MusicData> mMusicDatas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.test);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: " + mPresenter.getSize());
            }
        });

        mMusicDatas = new ArrayList<>();
        //获取读写权限，此操作后续搬到启动页
        getReadPermission();

    }

    @Override
    protected MainActivityPresenter getPresenter() {
        return new MainActivityPresenter(this);
    }

    private void getReadPermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, permissions, 321);
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //获取权限失败
                    showToast("权限获取失败,可到设置中打开存储权限");
                } else {
                    //获取到了权限
                    showToast("获取权限成功");
                    //从SD卡扫描音乐文件
                    getMusicInfoFromSD();
                }
            }
        }
    }

    private String TAG = "ningyuwen";

    /**
     * 从SD卡扫描音乐文件并存储到数据库
     */
    private void getMusicInfoFromSD() {
        mMusicDatas.clear();

        //查询媒体数据库
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历媒体数据库
        if (cursor != null && cursor.moveToFirst()) {
            List<MusicBasicInfo> musicBasicInfos = new ArrayList<>();
            while (!cursor.isAfterLast()) {

                //歌曲编号
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲标题
                String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if (size > 1024 * 800) {//大于800K
                    MusicBasicInfo data = new MusicBasicInfo();
                    data.setPId((long) id);
                    data.setMusicName(tilte);
                    data.setMusicPlayer(artist);
                    data.setMusicAlbum(album);
                    data.setMusicFilePath(url);
                    data.setMusicTime(duration);
                    data.setMusicFileSize(size);

                    musicBasicInfos.add(data);   //保存到基本信息List，存储到数据库，其他信息不变
                }
                cursor.moveToNext();
            }

            mPresenter.saveMusicInfoFromSD(musicBasicInfos);
        }
    }
}
