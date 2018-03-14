package com.example.hanyuany.myapplication;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private final static String TAG = "tag";
    final static String LIST_TAG = "list name";
    ArrayList<String> businessLists = new ArrayList<>();
    Button addListButton;
    ListView businessListsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addListButton = findViewById(R.id.addBusiness);
        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builderAddListAlertDialog = new AlertDialog.Builder(MainActivity.this);
                builderAddListAlertDialog.setTitle("Add List");
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builderAddListAlertDialog.setView(input);

                builderAddListAlertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String s = input.getText().toString();
                        businessLists.add(s);
                    }
                });
                builderAddListAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builderAddListAlertDialog.show();
            }
        });

        businessListsListView = findViewById(R.id.businessListListView);
        ArrayAdapter<String> businessListsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, businessLists);
        businessListsListView.setAdapter(businessListsAdapter);

        businessListsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent startBusinessListIntent = new Intent(MainActivity.this, BusinessListActivity.class);
                startBusinessListIntent.putExtra(LIST_TAG, businessListsListView.getItemAtPosition(i).toString());
                startActivity(startBusinessListIntent);
            }
        });
    }

    private class initiateBusinessList extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}
