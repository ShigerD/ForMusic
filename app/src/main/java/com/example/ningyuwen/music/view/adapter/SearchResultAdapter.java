package com.example.ningyuwen.music.view.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by money on 18-1-13.
 */

public class SearchResultAdapter extends BaseAdapter {
    private List<MusicBasicInfo> list;
    private LayoutInflater inflater;
    private long ClickPid;

    public SearchResultAdapter(List<MusicBasicInfo> list,Context context){
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    public void setListData(List<MusicBasicInfo> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public List<MusicBasicInfo> getListData(){
        if (list != null){
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
/*
获取被点击音乐的pid;
 */
    public long getClickPid() {
        return ClickPid;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(list.size()!=0&&list!=null) {
            ViewHolder hoder = null;
            if (convertView == null) {
                hoder = new ViewHolder();

                convertView = inflater.inflate(R.layout.item_search, null);
                hoder.searchMusicName = convertView.findViewById(R.id.search_music_name);
                hoder.searchMusicPlayer = convertView.findViewById(R.id.search_music_player);
//                hoder.relativeLayout = convertView.findViewById(R.id.rl_item_search_music);
                convertView.setTag(hoder);
            } else {
                hoder = (ViewHolder) convertView.getTag();
            }

            hoder.searchMusicName.setText(list.get(position).getMusicName());
            hoder.searchMusicPlayer.setText(list.get(position).getMusicPlayer());

            final ViewHolder finalHoder = hoder;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ClickPid = list.get(position).getPId();
//                    ((MainActivity)v.getContext()).showToast(finalHoder.searchMusicName, String.valueOf(position));

                    //点击其中一首音乐
                    ((MainActivity)v.getContext()).changeMusicListFromSearch(position, list);

                }
            });
        }
        return convertView;
    }

    public final class ViewHolder{
        public TextView searchMusicName;
        public TextView searchMusicPlayer;
        public RelativeLayout relativeLayout;
    }

}
