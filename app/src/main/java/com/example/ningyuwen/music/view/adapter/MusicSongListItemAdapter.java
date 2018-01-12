package com.example.ningyuwen.music.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;

import java.util.List;

/**
 * 播放页面歌单的adapter
 * Created by ningyuwen on 18-1-12.
 */

public class MusicSongListItemAdapter extends RecyclerView.Adapter<MusicSongListItemAdapter.MyViewHolder> {

    private Context mContext;
    private ISetItemOnClickListener listener;

    public MusicSongListItemAdapter(Context context) {
//        super();
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_songlist_item, null, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvItemName.setText(BaseActivity.mMusicDatas.get(position).getMusicName());
        setOnClickListener(holder, position);
    }

    /**
     * 设置监听
     * @param holder holder
     * @param position i
     */
    private void setOnClickListener(MyViewHolder holder, final int position) {
        holder.tvItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //监听点击事件，切换音乐
                if (listener != null) {
                    listener.setPlayMusicPosition(position);
                }
            }
        });
    }

    public void setListener(ISetItemOnClickListener listener){
        this.listener = listener;
    }

    /**
     * Popupwindow之间数据传递
     */
    public interface ISetItemOnClickListener{
        void setPlayMusicPosition(int position);    //点击事件，传给popupwindow
    }

    @Override
    public int getItemCount() {
        return BaseActivity.mMusicDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvItemName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvItemName = (TextView)itemView.findViewById(R.id.tv_music_name);
        }
    }
}
