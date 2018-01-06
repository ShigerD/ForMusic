package com.example.ningyuwen.music.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ningyuwen.music.R;

import java.util.List;

/**
 * Created by ningyuwen on 18-1-6.
 */

public class SongListItemAdapter extends RecyclerView.Adapter<SongListItemAdapter.MyViewHolder>{

    private Context mContext;
    private List<String> mItemStrs;

    public SongListItemAdapter(Context context, int type) {
        super();
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongListItemAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_pop_item, null, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        Glide.with(mContext).load(R.drawable.back_add_playlist).into(holder.ivItemLogo);
        holder.tvItemName.setText("test");
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    private String getItem(int position){
        return mItemStrs.get(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView ivItemLogo;
        TextView tvItemName;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivItemLogo = (ImageView) itemView.findViewById(R.id.iv_item_logo);
//            ivItemLogo = (ImageView)itemView.findViewById(R.id.)
            tvItemName = (TextView)itemView.findViewById(R.id.tv_list_item);
        }
    }
}
