package com.example.ningyuwen.music.view.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.presenter.i.IMusicSongListPresenter;
import com.example.ningyuwen.music.util.StaticFinalUtil;
import com.example.ningyuwen.music.view.activity.impl.BaseActivity;
import com.example.ningyuwen.music.view.activity.impl.MainActivity;
import com.example.ningyuwen.music.view.adapter.MusicSongListItemAdapter;

import org.w3c.dom.Text;

/**
 * 播放页面显示歌单的popupwindow
 * Created by ningyuwen on 18-1-12.
 */

public class MusicSongListPopupWindow extends PopupWindow implements
        MusicSongListItemAdapter.ISetItemOnClickListener {

    private RecyclerView mRvSongList;   //歌单
    private MusicSongListItemAdapter adapter;   //adapter
    private Context mContext;
    private IMusicSongPopToPlayPop mIMusicPopListener;  //监听
    private TextView mTvMusicListNmae;      //歌单名

    public MusicSongListPopupWindow(Context context) {
//        super(context);
        this(LayoutInflater.from(context)
                        .inflate(R.layout.layout_music_popup_window, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                900, true);
        mContext = context;
    }

    public MusicSongListPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicSongListPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MusicSongListPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MusicSongListPopupWindow() {
        super();
    }

    public MusicSongListPopupWindow(View contentView) {
        super(contentView);
    }

    public MusicSongListPopupWindow(int width, int height) {
        super(width, height);
    }

    public MusicSongListPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public MusicSongListPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    @Override
    public void setContentView(View contentView) {
        super.setContentView(contentView);
        adapter = new MusicSongListItemAdapter(contentView.getContext());
        mTvMusicListNmae = contentView.findViewById(R.id.tv_playlist_name);
        mRvSongList = contentView.findViewById(R.id.rv_music_list_pop);
        mRvSongList.setLayoutManager(new LinearLayoutManager(contentView.getContext()));
        mRvSongList.setAdapter(adapter);
        adapter.setListener(this);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_ALL_MUSIC){
            mTvMusicListNmae.setText("歌单：所有音乐");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_CISTOM_LIST){
            mTvMusicListNmae.setText("歌单：自定歌单");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_MYLOVE){
            mTvMusicListNmae.setText("歌单：我喜爱的");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_CLASSIFY_PLAYER){
            mTvMusicListNmae.setText("歌单：歌手分类");
        }else if (BaseActivity.MUSIC_LIST_PLAY_NOW == StaticFinalUtil.MUSIC_LIST_PLAY_SEARCH_MUSIC){
            mTvMusicListNmae.setText("歌单：搜索音乐");
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 点击事件，传给popupwindow,切换音乐
     * @param position position
     */
    @Override
    public void setPlayMusicPosition(int position) {
        if (mIMusicPopListener != null) {
            mIMusicPopListener.setPlayMusicPosition(position);
        }
    }

    public void setPopupWindowListener(IMusicSongPopToPlayPop listener){
        mIMusicPopListener = listener;
    }

    /**
     * popupwindow传递数据
     */
    public interface IMusicSongPopToPlayPop{
        void setPlayMusicPosition(int position);
    }
}
