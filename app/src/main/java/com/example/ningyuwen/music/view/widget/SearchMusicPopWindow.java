package com.example.ningyuwen.music.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicData;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.adapter.AllMusicInfoAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 搜索音乐
 * Created by money on 18-1-10.
 */

public class SearchMusicPopWindow extends PopupWindow {
    private View mViewSearch;
    private Button btn_backButton;
    private ListView mListSearch;
//    public List<String> HotMusicdata=new ArrayList<>();
    private Context mContext;
    private List<String> list=new ArrayList<String>();
    private List<MusicData> SQL_music = BaseActivity.mMusicDatas;

    @SuppressLint("ResourceType")
    public SearchMusicPopWindow(Context context, View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);

//        Context context = getContentView(contentView);
        mContext = context;

        mViewSearch = contentView;
        mViewSearch.findViewById(R.layout.layout_popsearch);
        mListSearch = mViewSearch.findViewById(R.id.search_list);
//        setMyAdapter();
//        mListSearch.setAdapter(new ArrayAdapter<String>(context,
//                android.R.layout.simple_expandable_list_item_1,list));

        setMusicNameListener();
    }

    private void setMusicNameListener() {
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //newText得到的字符
//                list.clear();
//                if(SQL_music!=null){
//                    int i =0;
//                    while(SQL_music.get(i)!=null){
//                        if(SQL_music.get(i).getMusicName().contains(newText)&&!newText.equals("")){
//                            Log.e("musicname", "onQueryTextChange: "+SQL_music.get(i).getMusicName());
//                            list.add(SQL_music.get(i).getMusicName());
//                        }
//                        i++;
//                        if(i==SQL_music.size())break;
//                    }
//                    if (list!=null&&!newText.equals("")){
//                        Log.e("musicname", "onQueryTextChange: "+1);
//                        mListSearch.setAdapter(new ArrayAdapter<String>(mContext,
//                                android.R.layout.simple_expandable_list_item_1,list));
//                    }else return false;
//                }
//                return false;
//            }
//
//
//        });
    }


//    private void setMyAdapter() {
//        list = new ArrayList<String>();
//        Log.e("takedatatimer","1");
//        if(BaseActivity.mMusicDatas!=null) {
//            for (int i = 0; i < 5; i++) {
//                list.add(BaseActivity.mMusicDatas.get(i).getMusicName());
//            }
//            HotMusicdata = list;
//        }
//    }

//    @SuppressLint("ResourceType")
//    @Override
//    public void showAtLocation(View parent, int gravity, int x, int y) {
//        super.showAtLocation(parent, gravity, x, y);
//
//    }


    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
    }
}
