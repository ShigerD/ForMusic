package com.example.ningyuwen.music.view.fragment.impl;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.CancellationSignal;
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
import android.widget.Toast;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.activity.impl.MusicSongListActivity;
import com.example.ningyuwen.music.view.adapter.CustomizeMusicAdapter;
import com.example.ningyuwen.music.view.fragment.i.ICustomizeMusicFragment;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.view.LayoutInflater.from;

/**
 * 自定歌单
 * Created by ningyuwen on 17-9-26.
 */

public class CustomizeMusicFragment extends Fragment implements ICustomizeMusicFragment, CustomizeMusicAdapter.AddItemClickListener {

    private RecyclerView mRvCustomizeMusic;   //RecyclerView 用于显示自定义歌单
    private CustomizeMusicAdapter mAdapter;   //adapter,歌单列表
    private List<SongListInfo> mSongListInfos; //歌单List

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View customizeMusicFragment = inflater.inflate(R.layout.fragment_customize_music, container, false);
        mRvCustomizeMusic = customizeMusicFragment.findViewById(R.id.rv_customize_music);

        showCustomizeMusicInfo();

        return customizeMusicFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * 显示歌单信息，从数据库中读取
     */
    @Override
    public void showCustomizeMusicInfo() {
        mRvCustomizeMusic.setLayoutManager(new LinearLayoutManager(getContext()));
        mSongListInfos = ((MainActivity)getActivity()).getSongListInfo();
        mAdapter = new CustomizeMusicAdapter(getActivity(), mSongListInfos);
        mRvCustomizeMusic.setAdapter(mAdapter);
        mAdapter.addItemClickListener(this);
    }

    /**
     * 跳转到歌单
     * @param position position
     */
    @Override
    public void jumpSongList(int position) {
//        Dialog dialog = new Dialog(getActivity());
//        dialog.setTitle("歌曲列表");
//        dialog.show();
        startActivity(new Intent(getActivity(), MusicSongListActivity.class));
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
}
