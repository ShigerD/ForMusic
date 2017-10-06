package com.example.ningyuwen.music.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    private CustomizeMusicAdapter.AddItemClickListener listener;

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
        setClickListener(holder, position);
        if (getItemCount() - 1 == position){
//            holder.tvCustomName.setText("+");
//            holder.tvCustomName.setGravity(Gravity.CENTER);
//            holder.tvCustomName.setTextColor(Color.parseColor("#0000FF"));
//            holder.tvCustomName.setTextSize(26);
            holder.rlAddSongList.setVisibility(View.VISIBLE);
            holder.rlSongList.setVisibility(View.GONE);
            return;
        }
        holder.rlAddSongList.setVisibility(View.GONE);
        holder.rlSongList.setVisibility(View.VISIBLE);
        holder.tvCustomName.setTextSize(24);
        holder.tvCustomName.setTextColor(Color.parseColor("#FFFFFF"));
        holder.tvCustomName.setText(mSongListInfo.get(position).getName());
        holder.tvCustomNumber.setTextSize(18);
        holder.tvCustomNumber.setTextColor(Color.parseColor("#FFFFFF"));
        holder.tvCustomNumber.setText(mSongListInfo.get(position).getNumber() + "首");
    }

    /**
     * 设置点击监听
     * @param holder holder
     * @param position position
     */
    private void setClickListener(MyViewHolder holder, final int position) {
        holder.tvCustomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.jumpSongList(position);
            }
        });
        holder.rlAddSongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.addSongList(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongListInfo.size() + 1;
    }

    public void addItemClickListener(CustomizeMusicAdapter.AddItemClickListener addItemClickListener){
        this.listener = addItemClickListener;
    }

    public interface AddItemClickListener{
        void jumpSongList(int position);
        void addSongList(int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCustomPic;      //歌单图片
        TextView tvCustomName;      //歌单名
        TextView tvCustomNumber;    //歌曲数量
        RelativeLayout rlAddSongList;  //添加歌单按钮布局
        RelativeLayout rlSongList;      //存在的歌单

        MyViewHolder(View view) {
            super(view);
            ivCustomPic = (ImageView) view.findViewById(R.id.iv_custom_music_pic);
            tvCustomName = (TextView) view.findViewById(R.id.tv_custom_music_name);
            tvCustomNumber = (TextView) view.findViewById(R.id.tv_custom_music_number);
            rlAddSongList = (RelativeLayout) view.findViewById(R.id.rl_add_song_list);
            rlSongList = (RelativeLayout) view.findViewById(R.id.rl_song_list);
        }
    }
}
