package com.example.hanyuany.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.AutoComplete;
import com.yelp.fusion.client.models.Business;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hanyuany.myapplication.MainActivity.LIST_TAG;

public class BusinessListActivity extends AppCompatActivity {
    private static final String TAG = "tag";
    private static final int AUTOCOMPLETE_THRESHOLD = 3;
    private String listName;
    private BusinessListAdapter adapter;
    private YelpFusionApi yelpFusionApi;
    private String[] autoCompleteArray;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location userLocation;
    private int requestCode = 1;
    Call<Business> call;
    Button addBusiness;
    Button searchBusiness;
    Button deleteBusiness;
    ListView businessListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_list);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
        }
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(6000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d(TAG, "location retrieved");
                    userLocation = location;
                } else {
                    Log.d(TAG, "location is null");
                }
            }
        });
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
                final AutoCompleteTextView input = new AutoCompleteTextView(BusinessListActivity.this);
                autoCompleteArray = new String[0];
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(BusinessListActivity.this, android.R.layout.simple_dropdown_item_1line, autoCompleteArray);
                input.setAdapter(adapter);
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() >= AUTOCOMPLETE_THRESHOLD) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("text", charSequence.toString());

                            params.put("latitude", Double.toString(userLocation.getLatitude()));
                            params.put("longitude", Double.toString(userLocation.getLongitude()));

                            Call<AutoComplete> call = yelpFusionApi.getAutocomplete(params);
                            call.enqueue(autoCompleteCallBack);

                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {


                    }
                });


                builderAddListAlertDialog.setView(input);

                builderAddListAlertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String s = input.getText().toString();
                    }
                });
                builderAddListAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builderAddListAlertDialog.show();


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

        mFusedLocationClient.getLastLocation();

    }

    @Override
    protected void onResume() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation();
        super.onResume();
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

    Callback<AutoComplete> autoCompleteCallBack = new Callback<AutoComplete>() {
        @Override
        public void onResponse(Call<AutoComplete> call, Response<AutoComplete> response) {
            AutoComplete autoComplete = response.body();
            autoCompleteArray = new String[autoComplete.getBusinesses().size()];
            for (int i = 0; i < autoComplete.getBusinesses().size(); i++) {
                autoCompleteArray[i] = autoComplete.getBusinesses().get(i).getName();
            }
            adapter.notifyDataSetChanged();
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
