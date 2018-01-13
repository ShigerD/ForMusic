package com.example.ningyuwen.music.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicData;

import java.util.List;

/**
 * adapter,我喜愛的音樂列表
 * Created by ningyuwen on 17-10-6.
 */

public class MyLoveMusicAdapter extends RecyclerView.Adapter<MyLoveMusicAdapter.MyViewHolder>{

    private Context mContext;
    private List<MusicData> mMyLoveMusicDatas;
    private AddItemClickListener listener;

    public MyLoveMusicAdapter(Context context, List<MusicData> musicDatas) {
        super();
        mContext = context;
        mMyLoveMusicDatas = musicDatas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyLoveMusicAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_my_love_music, null, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(mContext).load(getItem(position).getMusicAlbumPicPath()).error(R.drawable.back_add_playlist).into(holder.ivState);
        holder.tvMusicName.setText(getItem(position).getMusicName());
        holder.ivIsLove.setImageResource(R.mipmap.ic_love);
        setClickListener(holder, position);
    }

    private void setClickListener(MyViewHolder holder, final int position) {
        holder.tvMusicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playMusic(position);
            }
        });
        holder.ivIsLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.setNotLove(position);
            }
        });
    }

    /**
     * 获取其中的一个item
     * @param position position
     * @return MusicData
     */
    public MusicData getItem(int position){
        return mMyLoveMusicDatas.get(position);
    }

    public void addItemClickListener(AddItemClickListener addItemClickListener){
        this.listener = addItemClickListener;
    }

    public interface AddItemClickListener{
        void playMusic(int position);
        void setNotLove(int position);
    }

    @Override
    public int getItemCount() {
        Log.i("test", "getItemCount: " + mMyLoveMusicDatas.size());
        return mMyLoveMusicDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivState;
        TextView tvMusicName;
        ImageView ivIsLove;
        RelativeLayout rlItem;

        MyViewHolder(View view) {
            super(view);
            ivState = (ImageView) view.findViewById(R.id.iv_play_state);
            tvMusicName = (TextView) view.findViewById(R.id.tv_music_name);
            ivIsLove = (ImageView) view.findViewById(R.id.iv_is_love);
            rlItem = (RelativeLayout) view.findViewById(R.id.rl_item_all_music);
        }
    }
}
