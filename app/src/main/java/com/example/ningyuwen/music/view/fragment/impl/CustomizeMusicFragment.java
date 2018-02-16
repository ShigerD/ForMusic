package com.example.ningyuwen.music.view.fragment.impl;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.util.DensityUtil;
import com.example.ningyuwen.music.view.activity.i.IMainActivityToFragment;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.activity.impl.MusicSongListActivity;
import com.example.ningyuwen.music.view.adapter.CustomizeMusicAdapter;
import com.example.ningyuwen.music.view.fragment.i.ICustomizeMusicFragment;
import com.example.ningyuwen.music.view.widget.MusicPopupWindow;

import java.util.ArrayList;
import java.util.List;

import static android.view.LayoutInflater.from;

/**
 * 自定歌单
 * Created by ningyuwen on 17-9-26.
 */

public class CustomizeMusicFragment extends Fragment implements ICustomizeMusicFragment,
        CustomizeMusicAdapter.AddItemClickListener, IMainActivityToFragment {

    private static final String TAG = "test22";
    private BroadcastReceiver receiver;
    private RecyclerView mRvCustomizeMusic;   //RecyclerView 用于显示自定义歌单
    private CustomizeMusicAdapter mAdapter;   //adapter,歌单列表
    private List<SongListInfo> mSongListInfos; //歌单List
    private View customizeMusicFragment;    //根布局
    private MusicPopupWindow mPopupWindow;   //显示歌单三个点的点击事件
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (customizeMusicFragment == null) {
            customizeMusicFragment = inflater.inflate(R.layout.fragment_customize_music, container, false);
            mRvCustomizeMusic = customizeMusicFragment.findViewById(R.id.rv_customize_music);
        }

        mSongListInfos = new ArrayList<>();
        showCustomizeMusicInfo();
        return customizeMusicFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        createBroadcastReceiver();
    }

    /**
     * 广播接收
     */
    private void createBroadcastReceiver() {
        Log.i("test", "createBroadcastReceiver: ");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("test", "onReceive: " + action);

            }
        };

        IntentFilter filter = new IntentFilter();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, filter);
//        mContext.registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
//            mContext.unregisterReceiver(receiver);
        }
    }


    /**
     * 显示歌单信息，从数据库中读取
     */
    @Override
    public void showCustomizeMusicInfo() {
        mRvCustomizeMusic.setLayoutManager(new LinearLayoutManager(getContext()));
        mSongListInfos = ((MainActivity)mContext).getSongListInfo();
        mAdapter = new CustomizeMusicAdapter(mContext, mSongListInfos, false);
        mRvCustomizeMusic.setAdapter(mAdapter);
        mAdapter.addItemClickListener(this);
    }

    /**
     * 跳转到歌单
     * @param info info歌单
     */
    @Override
    public void jumpSongList(SongListInfo info) {
        try {
            startActivity(new Intent(mContext, MusicSongListActivity.class)
                    .putExtra("name", info.getName())
                    .putExtra("picUrl",info.getSonglistImgUrl())
                    .putExtra("number",info.getNumber())
                    .putExtra("id",info.getId()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 添加歌单
     * @param position position
     */
    @Override
    public void addSongList(final int position, final String title) {
        @SuppressLint("InflateParams") View view = from(mContext).inflate(R.layout.layout_add_songlist, null);
        final EditText et = view.findViewById(R.id.et_song_list);
        final TextView tv = view.findViewById(R.id.tv_cha_number);
        et.setText(title);
        if (title.length() > 0){
            et.setSelection(title.length());
        }
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                tv.setText(editable.toString().length() + "/20");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        builder.setTitle("添加歌单");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //判断是否为编辑歌单
                if (title.length() > 0){
                    //编辑歌单
                    mSongListInfos.get(position).setName(et.getText().toString());
                    mAdapter.notifyDataSetChanged();
                    ((MainActivity)mContext).editMusicListFromId(mSongListInfos.get(position));
                    mPopupWindow.dismiss();
                    return;
                }

                //判断数据库中是否存在名字相同的歌单，存在则不能添加
                if ("".equals(et.getText().toString())){
                    ((MainActivity)mContext).showToast(mRvCustomizeMusic, "歌单名不能为空");
                    return;
                }
                if (((MainActivity)mContext).existSongListName(et.getText().toString())){
                    ((MainActivity)mContext).showToast(mRvCustomizeMusic, "歌单已存在");
                    return;
                }
                ((MainActivity)mContext).showToast(mRvCustomizeMusic, "添加成功");
                SongListInfo info = new SongListInfo();
                info.setName(et.getText().toString());
                info.setNumber(0);
                mSongListInfos.add(info);
                mAdapter.notifyDataSetChanged();
                //存储到数据库
                ((MainActivity)mContext).addSongListToDB(info);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 显示PopupWindow
     */
    @SuppressLint("InflateParams")
    @Override
    public void showPopupWindow(int position, String listName) {
        if (mPopupWindow == null){
            mPopupWindow = new MusicPopupWindow(LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_edit_music_list, null),
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    400, true, iPopupToCustomFragment);
            mPopupWindow.setOutsideTouchable(true);

        }
        mPopupWindow.setClickPosition(position);
        mPopupWindow.setTitle(listName);
        mPopupWindow.showAtLocation(((MainActivity)mContext).findViewById(R.id.iv_main_activity_bg),
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    MusicPopupWindow.IPopupToCustomFragment iPopupToCustomFragment = new MusicPopupWindow.IPopupToCustomFragment() {
        @Override
        public void editMusicList(int position, String title) {
            addSongList(position, title);
        }

        @Override
        public void deleteMusicList(final int position, String title) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("删除歌单");
            builder.setMessage("确定删除歌单《" + title + "》吗？");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mPopupWindow.dismiss();
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //删除歌单
                    ((MainActivity)mContext).deleteMusicListFromId(mSongListInfos.get(position).getId());
                    mSongListInfos.remove(position);
                    mAdapter.notifyDataSetChanged();
                    mPopupWindow.dismiss();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        }
    };

    /**
     * 刷新音乐列表，初始化时通知几个Fragment获取相应数据
     */
    @Override
    public void refreshAllMusic() {
        Log.i(TAG, "refreshAllMusic: 刷新列表custom");
        if (mSongListInfos == null){
            mSongListInfos = new ArrayList<>();
        }
        mSongListInfos.clear();
        mSongListInfos.addAll(((MainActivity)mContext).getSongListInfo());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshAllMusicDislike(MusicData musicData) {

    }

    /**
     * 将音乐添加进入歌单时，这里需要刷新数据，传入pid，只修改这一个歌单数据，减少消耗
     * @param songListInfo songListId
     */
    @Override
    public void refreshCustomMusic(SongListInfo songListInfo) {
        int num = mSongListInfos.indexOf(songListInfo);
        mSongListInfos.get(num).setNumber(songListInfo.getNumber());
        mSongListInfos.get(num).setSonglistImgUrl(songListInfo.getSonglistImgUrl());
        mAdapter.notifyDataSetChanged();
    }
}
