package com.example.hanyuany.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hanyuany.myapplication.MainActivity.LIST_TAG;

public class BusinessListActivity extends AppCompatActivity {
    private static final String TAG = "tag";
    private String listName;
    private BusinessListAdapter adapter;
    private  YelpFusionApi yelpFusionApi;
    Call<Business> call;
    Button addBusiness;
    Button searchBusiness;
    Button deleteBusiness;
    ListView businessListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_list);

        new deleteDatabaseTask().execute();

        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        try {
            yelpFusionApi = apiFactory.createAPI(getString(R.string.API_KEY));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        listName = extras.getString(LIST_TAG);
        addBusiness = findViewById(R.id.addBusiness);
        addBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builderAddListAlertDialog = new AlertDialog.Builder(BusinessListActivity.this);
                builderAddListAlertDialog.setTitle("Add List");
                final EditText input = new EditText(BusinessListActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builderAddListAlertDialog.setView(input);


                //TODO Set up YELP AutoComplete
            }
        });
        //TODO Set up Custom List Adapter
        adapter = new BusinessListAdapter(this);
        businessListView = findViewById(R.id.businessListListView);
        businessListView.setAdapter(adapter);
        adapter.initiate(listName);
        call = yelpFusionApi.getBusiness("four-barrel-coffee-san-francisco");
        call.enqueue(callback);


    }

    @Override
    protected void onDestroy() {
        call.cancel();
        super.onDestroy();
    }

    Callback<Business> callback = new Callback<Business>() {
        @Override
        public void onResponse(Call<Business> call, Response<Business> response) {
            Business business = response.body();
            String[] params = {listName, business.getName(), business.getId(), business.getImageUrl()};
            new insertBusinessTask().execute(params);
        }
        @Override
        public void onFailure(Call<Business> call, Throwable t) {

        }
    };

    private class insertBusinessTask extends AsyncTask<String, Void, BusinessEntity> {
        @Override
        protected BusinessEntity doInBackground(String... params) {
            BusinessEntity entity = new BusinessEntity(params[0], params[1], params[2], params[3]);
            BusinessDatabase.getInstance(BusinessListActivity.this).getBusinessDao().insertBusiness(entity);
            return entity;
        }

        @Override
        protected void onPostExecute(BusinessEntity entity) {
            adapter.addItem(entity);
        }
    }

    private class deleteDatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            BusinessDatabase.getInstance(BusinessListActivity.this).getBusinessDao().deleteAll();
            return null;
        }
    }

    public static class BusinessListViewHolder {
        public TextView businessName;
        public ImageView businessPicture;
        public TextView businessHours;
    }
}
