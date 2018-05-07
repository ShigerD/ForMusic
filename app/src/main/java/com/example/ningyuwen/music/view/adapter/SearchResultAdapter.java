package com.example.ningyuwen.music.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicBasicInfo;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索适配adapter
 * Created by money on 18-1-13.
 */

public class SearchResultAdapter extends BaseAdapter implements Filterable {
    private List<MusicBasicInfo> mMusiclist;
    private List<MusicBasicInfo> mResultlist;
    private LayoutInflater inflater;
    private long ClickPid;

    public SearchResultAdapter(List<MusicBasicInfo> list, Context context) {
        this.mMusiclist = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mResultlist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 获取被点击音乐的pid
     * @return long
     */
    public long getClickPid() {
        return ClickPid;
    }

    String TAG = "adapter";

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (mResultlist.size() != 0 && mResultlist != null) {
            ViewHolder hoder;
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

            hoder.searchMusicName.setText(mResultlist.get(position).getMusicName());
            hoder.searchMusicPlayer.setText(mResultlist.get(position).getMusicPlayer());

            final ViewHolder finalHoder = hoder;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ClickPid = list.get(position).getPId();
//                    ((MainActivity)v.getContext()).showToast(finalHoder.searchMusicName, String.valueOf(position));

                    //点击其中一首音乐
                    ((MainActivity) v.getContext()).changeMusicListFromSearch(position, mResultlist);

                }
            });
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            //在工作线程中调用，约束字符串过滤数据
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<MusicBasicInfo> resultlist = new ArrayList<MusicBasicInfo>();
                if (mMusiclist != null && mMusiclist.size() != 0) {
                    String str = constraint.toString();
                    for (int i = 0; i < mMusiclist.size(); i++) {
                        if (mMusiclist.get(i).getMusicPlayer().contains(str) ||
                                mMusiclist.get(i).getMusicName().contains(str)) {

                            resultlist.add(mMusiclist.get(i));
                        }
                    }
                    results.values = resultlist;
                    results.count = resultlist.size();
                }

                return results;
            }

            //在ui线程总调用，以在用户界面发布过滤结果
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null && results.count != 0) {
                    mResultlist = (List<MusicBasicInfo>) results.values;
                    notifyDataSetChanged();
                } else notifyDataSetInvalidated();


            }
        };
        return filter;
    }

    private final class ViewHolder {
        private TextView searchMusicName;
        private TextView searchMusicPlayer;
    }

}
