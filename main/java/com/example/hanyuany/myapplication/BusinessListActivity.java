package com.example.hanyuany.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.AutoComplete;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hanyuany.myapplication.MainActivity.LIST_TAG;

public class BusinessListActivity extends AppCompatActivity {
    private static final String TAG = "tag";
    private static final int AUTOCOMPLETE_THRESHOLD = 3;

    private static final double TEST_LATITUDE = 43.765046;
    private static final double TEST_LONGITUDE = -79.4138610;

    private String mListName;
    private BusinessListAdapter mBusinessListAdapter;
    private YelpFusionApi mYelpFusionApi;
    private String[] mAutoCompleteArray;
    private LocationTracker mLocationTracker;
    private Location mUserLocation;
    private String mBusinessName;
    Call<Business> mBusinessCall;
    Call<SearchResponse> mSearchCall;
    Button addBusinessButton;
    Button searchBusinessButton;
    Button deleteBusinessButton;
    ListView mBusinessListView;
    ArrayAdapter<String> mAddBusinessAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_list);
        mAutoCompleteArray = new String[0];
        mLocationTracker = new LocationTracker(this, BusinessListActivity.this);
        mUserLocation = mLocationTracker.getLocation();

        new deleteDatabaseTask().execute();

        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        try {
            mYelpFusionApi = apiFactory.createAPI(getString(R.string.API_KEY));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mListName = extras.getString(LIST_TAG);
        addBusinessButton = findViewById(R.id.addBusiness);
        addBusinessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(BusinessListActivity.this);
                View promptView = layoutInflater.inflate(R.layout.dialog_add_business, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(BusinessListActivity.this);
                builder.setView(promptView);
                builder.setTitle("Add Restaurant");
                final AutoCompleteTextView input =(AutoCompleteTextView) promptView.findViewById(R.id.enterName);
                mAddBusinessAdapter = new ArrayAdapter<String>(BusinessListActivity.this, android.R.layout.simple_dropdown_item_1line, mAutoCompleteArray);
                input.setAdapter(mAddBusinessAdapter);
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() >= AUTOCOMPLETE_THRESHOLD) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("text", charSequence.toString());
                            params.put("latitude", Double.toString(TEST_LATITUDE));//userLocation.getLatitude()
                            params.put("longitude", Double.toString(TEST_LONGITUDE));//userLocation.getLongitude()
                            Call<AutoComplete> call = mYelpFusionApi.getAutocomplete(params);
                            call.enqueue(autoCompleteCallBack);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });


                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mBusinessName = input.getText().toString();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("term", mBusinessName);
                        params.put("latitude", Double.toString(TEST_LATITUDE));//userLocation.getLatitude()
                        params.put("longitude", Double.toString(TEST_LONGITUDE));//userLocation.getLongitude()
                        mSearchCall = mYelpFusionApi.getBusinessSearch(params);
                        mSearchCall.enqueue(searchResponseCallback);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();


                //TODO Set up YELP AutoComplete
            }
        });

        searchBusinessButton = findViewById(R.id.searchBusiness);
        searchBusinessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        deleteBusinessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(BusinessListActivity.this);
                builder.setTitle("Search");
                builder.setMessage("Enter Name of Business");
                final EditText input = new EditText(BusinessListActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                input.setLayoutParams(layoutParams);
                builder.setView(input);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] params = {input.getText().toString()};
                        new deleteBusinessTask().execute(params);
                        dialogInterface.cancel();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //TODO Set up Custom List Adapter
        mBusinessListAdapter = new BusinessListAdapter(this);
        mBusinessListView = findViewById(R.id.businessListListView);
        mBusinessListView.setAdapter(mBusinessListAdapter);
        mBusinessListAdapter.initiate(mListName);
        mBusinessCall = mYelpFusionApi.getBusiness("four-barrel-coffee-san-francisco");
        mBusinessCall.enqueue(callback);
    }

    @Override
    protected void onResume() {
        mLocationTracker.getLocation();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mBusinessCall.cancel();
        mSearchCall.cancel();
        super.onDestroy();
    }

    Callback<SearchResponse> searchResponseCallback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
            SearchResponse searchResponse = response.body();
            if (searchResponse.getBusinesses().isEmpty()) {
                return;
            }
            for (Business business : searchResponse.getBusinesses()) {
                if (business.getName() == mBusinessName) {
                    String[] params = {mListName, business.getName(), business.getId(), business.getImageUrl()};
                    new insertBusinessTask().execute(params);
                    return;
                }
            }
            Business business = searchResponse.getBusinesses().get(0);
            String[] params = {mListName, business.getName(), business.getId(), business.getImageUrl()};
            new insertBusinessTask().execute(params);
        }

        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {

        }
    };

    Callback<Business> callback = new Callback<Business>() {
        @Override
        public void onResponse(Call<Business> call, Response<Business> response) {
            Business business = response.body();
            String[] params = {mListName, business.getName(), business.getId(), business.getImageUrl()};
            new insertBusinessTask().execute(params);
        }
        @Override
        public void onFailure(Call<Business> call, Throwable t) {

        }
    };

    Callback<AutoComplete> autoCompleteCallBack = new Callback<AutoComplete>() {
        @Override
        public void onResponse(Call<AutoComplete> call, Response<AutoComplete> response) {
            AutoComplete autoComplete = response.body();
            mAutoCompleteArray = new String[autoComplete.getBusinesses().size()];
            for (int i = 0; i < autoComplete.getBusinesses().size(); i++) {
                mAutoCompleteArray[i] = autoComplete.getBusinesses().get(i).getName();
            }
            Log.d(TAG, Integer.toString(autoComplete.getBusinesses().size()));
            mAddBusinessAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(Call<AutoComplete> call, Throwable t) {

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
            mBusinessListAdapter.addItem(entity);
        }
    }

    private class deleteBusinessTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            mBusinessListAdapter.deleteItem(params[0]);
            return null;
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
    }
}
