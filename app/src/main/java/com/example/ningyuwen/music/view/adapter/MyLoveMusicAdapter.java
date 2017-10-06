package com.example.ningyuwen.music.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        holder.tvLoveMusicName.setText(mMyLoveMusicDatas.get(position).getMusicName());
        setClickListener(holder, position);
    }

    private void setClickListener(MyViewHolder holder, final int position) {
        holder.tvLoveMusicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playMusic(position);
            }
        });
    }

    public void addItemClickListener(AddItemClickListener addItemClickListener){
        this.listener = addItemClickListener;
    }

    public interface AddItemClickListener{
        void playMusic(int position);
        void setIsLove(int position);
    }

    @Override
    public int getItemCount() {
        Log.i("test", "getItemCount: " + mMyLoveMusicDatas.size());
        return mMyLoveMusicDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
//        ImageView ivState;
        TextView tvLoveMusicName;
//        ImageView ivIsLove;

        MyViewHolder(View view) {
            super(view);
//            ivState = (ImageView) view.findViewById(R.id.iv_play_state);
            tvLoveMusicName = (TextView) view.findViewById(R.id.tv_mylove_music_name);
//            ivIsLove = (ImageView) view.findViewById(R.id.iv_is_love);
        }
    }
}
