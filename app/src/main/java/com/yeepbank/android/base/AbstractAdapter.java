package com.yeepbank.android.base;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by WW on 2015/8/21.
 */
public abstract class AbstractAdapter<T> extends BaseAdapter {

    private List<T> data;
    private Context context;
    private IViewHolder iViewHolder;

    public AbstractAdapter(List<T> data,Context context,IViewHolder iViewHolder) {
        this.data = data;
        this.context = context;
        this.iViewHolder = iViewHolder;

    }

    public List<T> getData(){
        return data;
    }

    @Override
    public int getCount() {
        if(data == null){
            return 0;
        }
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(getLayoutResources(),null);

        }
        if(iViewHolder != null){
            iViewHolder.initView(convertView);
            iViewHolder.fillData(position);
        }
        return convertView;
    }

    public interface IViewHolder{
        void initView(View view);
        void fillData(int position);
    }

    public abstract int getLayoutResources();
}
