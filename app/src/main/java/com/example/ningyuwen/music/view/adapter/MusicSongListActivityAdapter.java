package com.example.ningyuwen.music.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
 * 歌单activity的适配器
 * Created by ningyuwen on 18-1-12.
 */

public class MusicSongListActivityAdapter extends RecyclerView.Adapter<MusicSongListActivityAdapter.MyViewHolder> {

    private List<MusicData> mMusicDatas;
    private Context mContext;
    private MusicSongListActivityAdapter.IAddItemOnClickListener listener;  //监听回调

    public MusicSongListActivityAdapter(Context context, List<MusicData> musicDatas) {
        super();
        mMusicDatas = musicDatas;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_music_song_list_activity_item, null, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvMusicName.setText(getItem(position).getMusicName());
//        holder.ivState.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_launcher));
        Glide.with(mContext).load(getItem(position).getMusicAlbumPicPath()).error(R.drawable.back_add_playlist).into(holder.ivState);
//        Glide.with(mContext).load(getItem(position).getMusicAlbumPicUrl()).into(holder.ivState);
//        if (getItem(position).isLove()){
//            holder.ivIsLove.setImageResource(R.mipmap.ic_love);
//        }else {
//            holder.ivIsLove.setImageResource(R.mipmap.ic_not_love);
//        }
        setPlayAndIsLoveListener(holder, position);
    }

    private void setPlayAndIsLoveListener(MyViewHolder holder, final int position) {
        holder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null){
                    return;
                }
                listener.playMusicFromPosition(position);
            }
        });

    }

    public void setItemClickListener(IAddItemOnClickListener listener){
        this.listener = listener;
    }

    public interface IAddItemOnClickListener{
        void playMusicFromPosition(int position);       //传回一个position，切换歌单，然后根据position确定播放的音乐
    }

    /**
     * 获取其中的一个item
     * @param position position
     * @return MusicData
     */
    public MusicData getItem(int position){
        return mMusicDatas.get(position);
    }

    @Override
    public int getItemCount() {
        return mMusicDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivState;
        TextView tvMusicName;
//        ImageView ivIsLove;
        RelativeLayout rlItem;

        MyViewHolder(View view) {
            super(view);
            ivState = (ImageView) view.findViewById(R.id.iv_play_state);
            tvMusicName = (TextView) view.findViewById(R.id.tv_music_name);
//            ivIsLove = (ImageView) view.findViewById(R.id.iv_is_love);
            rlItem = (RelativeLayout) view.findViewById(R.id.rl_item_all_music);
        }
    }

}
