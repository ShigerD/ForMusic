package com.example.ningyuwen.music.view.fragment.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.view.activity.i.IMainActivityToFragment;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.activity.impl.MusicSongListActivity;
import com.example.ningyuwen.music.view.adapter.CustomizeMusicAdapter;
import com.example.ningyuwen.music.view.fragment.i.ICustomizeMusicFragment;

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
    private boolean shouldRefreshList = false;  //判断是否需要刷新列表，在接收到广播时置为true

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View customizeMusicFragment = inflater.inflate(R.layout.fragment_customize_music, container, false);
        mRvCustomizeMusic = customizeMusicFragment.findViewById(R.id.rv_customize_music);

        mSongListInfos = new ArrayList<>();
        showCustomizeMusicInfo();
        return customizeMusicFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }


    /**
     * 显示歌单信息，从数据库中读取
     */
    @Override
    public void showCustomizeMusicInfo() {
        mRvCustomizeMusic.setLayoutManager(new LinearLayoutManager(getContext()));
        mSongListInfos = ((MainActivity)getActivity()).getSongListInfo();
        mAdapter = new CustomizeMusicAdapter(getActivity(), mSongListInfos, false);
        mRvCustomizeMusic.setAdapter(mAdapter);
        mAdapter.addItemClickListener(this);
    }

    /**
     * 跳转到歌单
     * @param info
     */
    @Override
    public void jumpSongList(SongListInfo info) {
//        Dialog dialog = new Dialog(getActivity());
//        dialog.setTitle("歌曲列表");
//        dialog.show();

        startActivity(new Intent(getActivity(), MusicSongListActivity.class).putExtra("title", info.getName()));
    }

    /**
     * 添加歌单
     * @param position position
     */
    @Override
    public void addSongList(int position) {
        View view = from(getActivity()).inflate(R.layout.layout_add_songlist, null);
        final EditText et = (EditText) view.findViewById(R.id.et_song_list);
        final TextView tv = (TextView) view.findViewById(R.id.tv_cha_number);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tv.setText(editable.toString().length() + "/20");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                //判断数据库中是否存在名字相同的歌单，存在则不能添加
                if ("".equals(et.getText().toString())){
                    ((MainActivity)getActivity()).showToast(mRvCustomizeMusic, "歌单名不能为空");
                    return;
                }
                if (((MainActivity)getActivity()).existSongListName(et.getText().toString())){
                    ((MainActivity)getActivity()).showToast(mRvCustomizeMusic, "歌单已存在");
                    return;
                }
                ((MainActivity)getActivity()).showToast(mRvCustomizeMusic, "添加成功");
                SongListInfo info = new SongListInfo();
                info.setName(et.getText().toString());
                info.setNumber(0);
                mSongListInfos.add(info);
                mAdapter.notifyDataSetChanged();
                //存储到数据库
                ((MainActivity)getActivity()).addSongListToDB(info);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

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
        mSongListInfos = ((MainActivity)getActivity()).getSongListInfo();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshAllMusicDislike(MusicData musicData) {

    }
}
