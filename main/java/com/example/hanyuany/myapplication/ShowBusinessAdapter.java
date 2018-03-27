package com.example.hanyuany.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by hanyuany on 21/03/2018.
 */

public class ShowBusinessAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<ShowBusinessActivity.BusinessData> dataArrayList;
    ShowBusinessActivity.DataViewHolder holder;

    public ShowBusinessAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void initiate(ArrayList<ShowBusinessActivity.BusinessData> dataArrayList) {
        this.dataArrayList = dataArrayList;
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        if (dataArrayList == null) {
            return 0;
        }
        if (dataArrayList.size() == 0 || dataArrayList.isEmpty()) {
            return 0;
        }
        return dataArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public ShowBusinessActivity.BusinessData getItem(int position) {
        return dataArrayList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_show_business, parent, false);
            holder = new ShowBusinessActivity.DataViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.name);
            holder.hoursTextView =  (TextView) convertView.findViewById(R.id.hours);
            holder.distanceTextView = (TextView) convertView.findViewById(R.id.distance);
            convertView.setTag(holder);
        } else {
            holder = (ShowBusinessActivity.DataViewHolder)convertView.getTag();
        }
        holder.nameTextView.setText(getItem(position).getName());
        holder.nameTextView.setTextSize(mContext.getResources().getDimension(R.dimen.textsize));
        holder.hoursTextView.setText(getItem(position).getHours());
        holder.hoursTextView.setTextSize(mContext.getResources().getDimension(R.dimen.textsize));
        holder.distanceTextView.setText(Double.toString(getItem(position).getDistance()));
        holder.distanceTextView.setTextSize(mContext.getResources().getDimension(R.dimen.textsize));
        return convertView;
    }
}
