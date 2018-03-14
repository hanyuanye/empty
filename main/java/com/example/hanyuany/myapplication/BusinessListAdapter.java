package com.example.hanyuany.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.http.Url;



/**
 * Created by hanyuany on 14/03/2018.
 */

public class BusinessListAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private ArrayList<BusinessEntity> yelpBusiness;
    private Context mContext;
    private static final String TAG = "tag";
    BusinessListActivity.BusinessListViewHolder holder;
    public BusinessListAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    public void initiate(String parentList) {
        String params[] = {parentList};
        new instantiateList().execute(params);
    }

    public void addItem(final BusinessEntity item) {
        for (BusinessEntity entity : yelpBusiness) {
            if (item.equals(entity)) {
                return;
            }
        }
        yelpBusiness.add(item);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (yelpBusiness == null) {
            return 0;
        }
        if (yelpBusiness.size() == 0 || yelpBusiness.isEmpty()) {
            return 0;
        }
        return yelpBusiness.size();
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public BusinessEntity getItem(int position) {
        return yelpBusiness.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getting view");
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout, parent, false);
            holder = new BusinessListActivity.BusinessListViewHolder();
            holder.businessName = convertView.findViewById(R.id.business_list_name);
            holder.businessPicture =  convertView.findViewById(R.id.business_list_picture);
            convertView.setTag(holder);
        } else {
            holder = (BusinessListActivity.BusinessListViewHolder)convertView.getTag();
        }
        holder.businessName.setText(getItem(position).yelpBusinessName);
        holder.businessName.setTextSize(mContext.getResources().getDimension(R.dimen.textsize));
        String[] params = {getItem(position).yelpBusinessImageUrl};
        new setImageTask().execute(params);
        return convertView;
    }

    private class instantiateList extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params) {
            if (!BusinessDatabase.getInstance(mContext).getBusinessDao().getBusinessesFromList(params[0]).isEmpty()) {
                Log.d(TAG, "Non empty");
                yelpBusiness = (ArrayList) BusinessDatabase.getInstance(mContext).getBusinessDao().getBusinessesFromList(params[0]);
                notifyDataSetChanged();
            } else {
                Log.d(TAG, "empty");
                yelpBusiness = new ArrayList<>();
            }
            return null;
        }
    }

    private class setImageTask extends AsyncTask<String, Void, Drawable> {
        @Override
        protected Drawable doInBackground(String... params) {
            Drawable drawable = null;
            try {
                InputStream is = (InputStream) new URL(params[0]).getContent();
                drawable = Drawable.createFromStream(is, "image");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return drawable;
        }
        @Override
        protected void onPostExecute(Drawable drawable) {
            holder.businessPicture.setImageDrawable(drawable);
        }
    }
}


