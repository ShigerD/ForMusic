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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.customize.SongListInfo;
import com.example.ningyuwen.music.util.DensityUtil;
import com.example.ningyuwen.music.view.widget.MusicPopupWindow;

import java.util.List;

/**
 * adapter,歌单页面
 * Created by ningyuwen on 17-10-5.
 */

public class CustomizeMusicAdapter extends RecyclerView.Adapter<CustomizeMusicAdapter.MyViewHolder> {

    private Context mContext;
    private List<SongListInfo> mSongListInfo;
    private CustomizeMusicAdapter.AddItemClickListener listener;
    private boolean isDialog;

    public CustomizeMusicAdapter(Context context, List<SongListInfo> songListInfoList, boolean isDialog) {
        super();
        this.mContext = context;
        this.mSongListInfo = songListInfoList;
        this.isDialog = isDialog;
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
            holder.llAddPlayList.setVisibility(View.GONE);
            holder.tvAddPlayList.setVisibility(View.VISIBLE);
            holder.ivCustomPic.setImageResource(R.mipmap.ic_add_playlist);
            if (isDialog){
                holder.tvAddPlayList.setTextColor(Color.parseColor("#000000"));
            }else {
                holder.tvAddPlayList.setTextColor(Color.parseColor("#FFFFFF"));
            }
            holder.ivStatusPlay.setVisibility(View.GONE);
            return;
        }
        holder.llAddPlayList.setVisibility(View.VISIBLE);
        holder.tvAddPlayList.setVisibility(View.GONE);
        if (getItem(position).getSonglistImgUrl() == null || "".equals(getItem(position).getSonglistImgUrl())) {
            Glide.with(mContext).load(R.mipmap.ic_play_album).into(holder.ivCustomPic);
        }else {
            Glide.with(mContext).load(getItem(position).getSonglistImgUrl()).into(holder.ivCustomPic);
        }
        if (isDialog){
            holder.ivStatusPlay.setVisibility(View.GONE);
            holder.tvCustomName.setTextColor(Color.parseColor("#000000"));
            holder.tvCustomNumber.setTextColor(Color.parseColor("#000000"));
        }else {
            holder.ivStatusPlay.setVisibility(View.VISIBLE);
            holder.tvCustomName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.tvCustomNumber.setTextColor(Color.parseColor("#FFFFFF"));
        }
        holder.tvCustomName.setTextSize(16);
        holder.tvCustomName.setText(getItem(position).getName());
        holder.tvCustomNumber.setTextSize(14);
        holder.tvCustomNumber.setText(getItem(position).getNumber() + "首");
    }

    /**
     * 获取条目
     * @param position position
     * @return SongListInfo
     */
    public SongListInfo getItem(int position){
        return mSongListInfo.get(position);
    }

    /**
     * 设置点击监听
     * @param holder holder
     * @param position position
     */
    private void setClickListener(final MyViewHolder holder, final int position) {
        holder.llAddPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.jumpSongList(getItem(position));
            }
        });
        holder.flAddPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.addSongList(position);
            }
        });
        holder.ivStatusPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((MainActivity)mContext).showToast(holder.ivStatusPlay, "暂未开放此功能");
                //弹出popupwindow
                listener.showPopupWindow(getItem(position).getName());
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
        void jumpSongList(SongListInfo info);     //跳转到歌单页面
        void addSongList(int position); //添加歌单
        void showPopupWindow(String listName);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCustomPic;      //歌单图片
        TextView tvCustomName;      //歌单名
        TextView tvCustomNumber;    //歌曲数量
//        RelativeLayout rlAddSongList;  //添加歌单按钮布局
        RelativeLayout rlSongList;      //存在的歌单
        ImageView ivStatusPlay;         //播放状态
        FrameLayout flAddPlayList;      //新建歌单
        LinearLayout llAddPlayList;
        TextView tvAddPlayList;

        MyViewHolder(View view) {
            super(view);
            ivCustomPic = (ImageView) view.findViewById(R.id.iv_custom_music_pic);
            tvCustomName = (TextView) view.findViewById(R.id.tv_custom_music_name);
            tvCustomNumber = (TextView) view.findViewById(R.id.tv_custom_music_number);
//            rlAddSongList = (RelativeLayout) view.findViewById(R.id.rl_add_song_list);
            rlSongList = (RelativeLayout) view.findViewById(R.id.rl_song_list);
            ivStatusPlay = (ImageView) view.findViewById(R.id.iv_status_play);
            flAddPlayList = (FrameLayout) view.findViewById(R.id.fl_add_playlist);
            llAddPlayList = (LinearLayout) view.findViewById(R.id.ll_add_playlist);
            tvAddPlayList = (TextView) view.findViewById(R.id.tv_add_playlist);
        }
    }
}
