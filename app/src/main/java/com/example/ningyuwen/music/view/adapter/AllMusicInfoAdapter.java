package com.example.ningyuwen.music.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicData;

import java.util.List;

/**
 * Created by ningyuwen on 17-9-26.
 */

public class AllMusicInfoAdapter extends RecyclerView.Adapter<AllMusicInfoAdapter.MyViewHolder> {

    private List<MusicData> mMusicDatas;

    public AllMusicInfoAdapter(List<MusicData> musicDatas) {
        super();
        mMusicDatas = musicDatas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("test", "onCreateViewHolder: test 1");
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_all_music_info, null, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.i("test", "onCreateViewHolder: test 2");
        holder.tv.setText("ningyuwen");
    }

    @Override
    public int getItemCount() {
        Log.i("test", "onCreateViewHolder: test 3");
        return mMusicDatas.size();
//        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv_item_all_music);
        }
    }
}
