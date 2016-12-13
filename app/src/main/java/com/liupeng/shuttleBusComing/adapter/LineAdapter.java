package com.liupeng.shuttleBusComing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.utils.Initialize;

import java.util.ArrayList;

/**
 * Created by liupeng on 2016/5/27.
 */
public class LineAdapter extends BaseAdapter {
    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<Integer> data;

    /**构造函数*/
    public LineAdapter(Context context, ArrayList<Integer> lineItems) {
        this.mInflater = LayoutInflater.from(context);
        this.data = lineItems;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Integer lineName = data.get(i);
        ViewHolder viewHolder;
        if(null == view){
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.recent_stations,null);
            viewHolder.lineName = $(view,R.id.recent_stations);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.lineName.setText(lineName + "号线");

        view.setLayoutParams(new ListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                Initialize.SCREEN_HEIGHT/15));

        return view;
    }

    private <T extends View> T $(View view,int resId){
        return (T) view.findViewById(resId);
    }

    public static class ViewHolder{
        private TextView lineName;
    }
}
