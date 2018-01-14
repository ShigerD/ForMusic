package com.example.ningyuwen.music.view.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.view.adapter.SongListItemAdapter;
import com.example.ningyuwen.music.view.fragment.impl.CustomizeMusicFragment;

/**
 * MusicPopupWindow 歌单，音乐等等需要
 * Created by ningyuwen on 17-12-21.
 */

public class MusicPopupWindow extends PopupWindow {

    private String title;
    private View mRootView;
    private IPopupToCustomFragment listener;
    private int position;

    public MusicPopupWindow(Context context) {
        super(context);
    }

    public MusicPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MusicPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MusicPopupWindow() {
        super();
    }

    public MusicPopupWindow(View contentView) {
        super(contentView);
    }

    public MusicPopupWindow(int width, int height) {
        super(width, height);
    }

    public MusicPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public MusicPopupWindow(View contentView, int width, int height, boolean focusable, IPopupToCustomFragment listener) {
        super(contentView, width, height, focusable);
        mRootView = contentView;
        this.listener = listener;
    }

    public interface IPopupToCustomFragment{
        void editMusicList(int position, String title);
        void deleteMusicList(int position, String title);
    }

    /**
     * 设置点击位置
     * @param position position
     */
    public void setClickPosition(int position){
        this.position = position;
    }

    @Override
    public void setContentView(View contentView) {
        super.setContentView(contentView);
        contentView.findViewById(R.id.tv_edit_music_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //编辑歌单
                listener.editMusicList(position, title);
            }
        });
        contentView.findViewById(R.id.tv_delete_music_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除歌单
                listener.deleteMusicList(position, title);
            }
        });
    }

    public MusicPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        ((TextView) mRootView.findViewById(R.id.tv_playlist_name)).setText("歌单：" + title);


//        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_music_list_pop);
//        recyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
//        recyclerView.setAdapter(new SongListItemAdapter(parent.getContext(), 1));
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    public void setTitle(String listName) {
        title = listName;
    }
}
