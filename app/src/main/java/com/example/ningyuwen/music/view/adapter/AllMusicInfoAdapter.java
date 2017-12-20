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
 * 所有音乐fragment的adapter
 * Created by ningyuwen on 17-9-26.
 */

public class AllMusicInfoAdapter extends RecyclerView.Adapter<AllMusicInfoAdapter.MyViewHolder> {

    private List<MusicData> mMusicDatas;
    private Context mContext;
    private AddItemClickListener listener;  //监听回调

    public AllMusicInfoAdapter(Context context, List<MusicData> musicDatas) {
        super();
        mMusicDatas = musicDatas;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("test", "onCreateViewHolder: test 1");
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_all_music_info, null, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.i("test", "onCreateViewHolder: test 2");
        holder.tvMusicName.setText(getItem(position).getMusicName());
//        holder.ivState.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_launcher));
        Glide.with(mContext).load(getItem(position).getMusicAlbumPicPath()).into(holder.ivState);
        if (getItem(position).isLove()){
            holder.ivIsLove.setImageResource(R.mipmap.ic_love);
        }else {
            holder.ivIsLove.setImageResource(R.mipmap.ic_not_love);
        }
        setPlayAndIsLoveListener(holder, position);
    }

    /**
     * 设置是否喜爱的图片
     * @param isLove bool
     */
    public void setIsLoveImage(boolean isLove){

    }

    private void setPlayAndIsLoveListener(final MyViewHolder holder, final int position) {
        holder.tvMusicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playMusic(position);
            }
        });
        holder.ivIsLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem(position).isLove()){
                    holder.ivIsLove.setImageResource(R.mipmap.ic_not_love);
                }else {
                    holder.ivIsLove.setImageResource(R.mipmap.ic_love);
                }
                listener.setIsLove(position);
            }
        });
        holder.tvMusicName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == R.id.tv_music_name) {
                    listener.longClickMusicItem(position);
                    return true;
                }
                return false;
            }
        });
        holder.ivState.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == R.id.iv_play_state) {
                    listener.longClickMusicItem(position);
                    return true;
                }
                return false;
            }
        });
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
        Log.i("test", "onCreateViewHolder: test 3");
        return mMusicDatas.size();
//        return 0;
    }

    public void addItemClickListener(AddItemClickListener addItemClickListener){
        this.listener = addItemClickListener;
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.tv_music_name:
//                break;
//            case R.id.iv_is_love:
//                break;
//            default:
//                break;
//        }
//    }

    public interface AddItemClickListener{
        void playMusic(int position);
        void setIsLove(int position);
        void longClickMusicItem(int position);
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
