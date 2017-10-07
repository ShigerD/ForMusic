package com.example.ningyuwen.music.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ningyuwen.music.R;
import com.example.ningyuwen.music.model.entity.music.MusicData;

import java.util.List;

/**
 * 歌手分类的adapter
 * Created by ningyuwen on 17-10-7.
 */

public class ClassifyMusicAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private LayoutInflater mInflater = null;
    private List<String> mGroupStrings = null;
    private List<List<MusicData>> mData = null;

    public ClassifyMusicAdapter(Context context, List<String> groupStrings,
                                List<List<MusicData>>list) {
        super();
        mContext = context;
        mGroupStrings = groupStrings;
        mData = list;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mData.get(i).size();
    }

    @Override
    public List<MusicData> getGroup(int i) {
        return mData.get(i);
    }

    @Override
    public MusicData getChild(int i, int i1) {
        return mData.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_classify_group_item, null);
        }
        GroupViewHolder holder = new GroupViewHolder();
        holder.mGroupName = (TextView) convertView
                .findViewById(R.id.group_name);
        holder.mGroupName.setText(mGroupStrings.get(groupPosition));
        holder.mGroupCount = (TextView) convertView
                .findViewById(R.id.group_count);
        holder.mGroupCount.setText("[" + mData.get(groupPosition).size() + "]");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_classify_child_item, null);
        }
        ChildViewHolder holder = new ChildViewHolder();
//        holder.mIcon = (ImageView) convertView.findViewById(R.id.img);
//        holder.mIcon.setBackgroundResource(getChild(groupPosition,
//                childPosition).getImageId());
        holder.mChildName = (TextView) convertView.findViewById(R.id.item_name);
        holder.mChildName.setText(getChild(groupPosition, childPosition)
                .getMusicName());
        holder.mDetail = (TextView) convertView.findViewById(R.id.item_detail);
        holder.mDetail.setText(getChild(groupPosition, childPosition)
                .getMusicPlayer());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupViewHolder {
        TextView mGroupName;
        TextView mGroupCount;

    }

    private class ChildViewHolder {
        ImageView mIcon;
        TextView mChildName;
        TextView mDetail;
    }
}
