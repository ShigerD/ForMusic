package com.example.ningyuwen.music.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.ningyuwen.music.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by money on 18-1-7.
 */

public class DrawerListAdapter extends SimpleAdapter{
    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */

    private List<? extends Map<String, ?>> data;
    private int resouces = R.id.item_drawerlist;
    private String[] from= {"主题换肤","关于开发者","计时关闭","退出"};
    private int[] to;


    public DrawerListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);

        this.data = data;
        this.resouces = resource;
        this.from = from;
        this.to = to;
    }

    private void setList() {
        List<Map<String,Object>> Listmaps =  new ArrayList<Map<String,Object>>();
        for (int i = 0;i<from.length;i++){
            Map<String,Object> item = new HashMap<String, Object>();
            item.put("changebg",from);
            
        }


    }


}
