package com.example.ningyuwen.music.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;

import java.util.List;

/**
 * adapter,歌单页面
 * Created by ningyuwen on 17-10-5.
 */

public class CustomizeMusicAdapter extends RecyclerView.Adapter<CustomizeMusicAdapter.MyViewHolder> {

    private Context mContext;
    private List<SongListInfo> mSongListInfo;

    public CustomizeMusicAdapter(Context context, List<SongListInfo> songListInfoList) {
        super();
        mContext = context;
        mSongListInfo = songListInfoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomizeMusicAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_customize_music, null, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvMusicName.setText("jsjj");
    }

    @Override
    public int getItemCount() {
        return mSongListInfo.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
//        ImageView ivState;
        TextView tvMusicName;
//        ImageView ivIsLove;

        MyViewHolder(View view) {
            super(view);
//            ivState = (ImageView) view.findViewById(R.id.iv_play_state);
            tvMusicName = (TextView) view.findViewById(R.id.test);
//            ivIsLove = (ImageView) view.findViewById(R.id.iv_is_love);
        }
    }
}
