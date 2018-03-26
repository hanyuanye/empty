package com.example.hanyuany.myapplication2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowBusinessActivity extends AppCompatActivity {
    private ListView listView;
    private String mLatitude;
    private String mLongitude;
    private Call<SearchResponse> mSearchCall;
    private YelpFusionApi mYelpFusionApi;
    private ArrayList<String> businessNameList;
    private ArrayList<BusinessData> businessDataList;
    private ShowBusinessAdapter mShowBusinessAdapter;
    private int counter;
    private int responseCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_business);
        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        try {
            mYelpFusionApi = apiFactory.createAPI(getString(R.string.API_KEY));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bundle extras = getIntent().getExtras();
        businessNameList = (ArrayList)extras.get("list");
        mLatitude = extras.getString("latitude");
        mLongitude = extras.getString("longitude");
        initiateViews();
    }

    private void initiateViews() {
        listView = findViewById(R.id.ListView);
        businessDataList = new ArrayList<>();
        HashMap<String, String> params;
        for (counter = 0; counter < businessNameList.size(); counter++) {
            params = new HashMap<>();
            params.put("latitude", mLatitude);
            params.put("longitude", mLongitude);
            params.put("term", businessNameList.get(counter));
            mSearchCall = mYelpFusionApi.getBusinessSearch(params);
            mSearchCall.enqueue(searchResponseCallback);
        }
    }

    Callback<SearchResponse> searchResponseCallback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
            responseCounter++;
            SearchResponse searchResponse = response.body();
            if (searchResponse.getBusinesses().isEmpty()) {
                if (responseCounter == businessNameList.size()) {
                    onAllApiCallsFinished();
                }
                return;
            }
            for (Business business : searchResponse.getBusinesses()) {
                if (business.getName() == businessNameList.get(counter)) {
                    BusinessData data = new BusinessData(business.getName(), business.getHours().get(0).getHoursType(), business.getDistance());
                    businessDataList.add(data);
                    if (responseCounter == businessNameList.size()) {
                        onAllApiCallsFinished();
                    }
                    return;
                }
            }
            if (responseCounter == businessNameList.size()) {
                onAllApiCallsFinished();
            }
        }

        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
        }
    };

    private void onAllApiCallsFinished() {
        mShowBusinessAdapter = new ShowBusinessAdapter(this);
        listView.setAdapter(mShowBusinessAdapter);
        mShowBusinessAdapter.initiate(businessDataList);
    }


    public class BusinessData {
        public BusinessData(String name, String hours, double distance) {
            this.name = name;
            this.hours = hours;
            this.distance = distance;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHours() {
            return hours;
        }

        public void setHours(String hours) {
            this.hours = hours;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        private String name;
        private String hours;
        private double distance;
    }

    public static class DataViewHolder {
        public static TextView hoursTextView;
        public static TextView distanceTextView;
        public static TextView nameTextView;
    }
}
