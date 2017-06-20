package com.example.manishchawley.computehelper.render;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.manishchawley.computehelper.R;
import com.example.manishchawley.computehelper.app.AppController;
import com.example.manishchawley.computehelper.component.OptionActivity;
import com.example.manishchawley.computehelper.component.TripCardAdapter;
import com.example.manishchawley.computehelper.model.Trip;
import com.example.manishchawley.computehelper.util.Constants;
import com.example.manishchawley.computehelper.util.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class TripListActivity extends OptionActivity{

    private final String TAG = TripListActivity.class.getName();
    private static final String url = "https://api.androidhive.info/json/movies.json";
    private ProgressDialog pDialog;
    private List<Trip> tripList = new ArrayList<Trip>();
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private TripCardAdapter tripCardAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        startNewTripActivity();

        setContentView(R.layout.activity_trip_list);

        fab = (FloatingActionButton) findViewById(R.id.activity_trip_list_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "New Trip");
                startNewTripActivity();
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        recyclerView = (RecyclerView) findViewById(R.id.activity_trip_list_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        tripCardAdapter = new TripCardAdapter(this, tripList);
        recyclerView.setAdapter(tripCardAdapter);

        AppController.getInstance().addToRequestQueue(getJSONRequest());
    }

    private void startNewTripActivity() {
        Intent intent = new Intent(this, TripDetailsActivity.class);
        intent.putExtra(Constants.TRIP_DETAILS_EDITABLE_KEY, true);
        intent.putExtra(Constants.TRIP_DETAILS_TYPE_KEY, Constants.TRIP_DETAILS_TYPE_N);
        startActivity(intent);
    }

    public JsonArrayRequest getJSONRequest() {
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e(TAG, response.toString());
                        Log.i(TAG, "Response received: " + response.length());
                        hidePDialog();
                        try {
                            for (int i=0; i<response.length();i++)
                                tripList.add(JSONUtils.getTripFromJson(response.getJSONObject(i)));

                            //tripList = JSONUtils.getTripsFromJson(response, JSONUtils.USE_DEFAULT);
                            //Log.e(TAG, "Trip List size: " + tripList.size());
                            //Log.e(TAG, "getTripsFromJson size: " + JSONUtils.getTripsFromJson(response, JSONUtils.USE_DEFAULT).size());
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                        tripCardAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage());
                        hidePDialog();
                    }
                });

        return request;
    }

    private void hidePDialog() {
        if(pDialog!=null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}


/*


Old version with custom listview adapter

public class TripListActivity extends OptionActivity {

    private final String TAG = TripListActivity.class.getName();
    private static final String url = "https://api.androidhive.info/json/movies.json";
    private ProgressDialog pDialog;
    private List<Trip> tripList = new ArrayList<Trip>();
    private ListView listView;
    private FloatingActionButton fab;
    private TripListAdapter tripListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        listView = (ListView) findViewById(R.id.activity_trip_list_listview);
        fab = (FloatingActionButton) findViewById(R.id.activity_trip_list_fab);

        tripListAdapter = new TripListAdapter(this, tripList);
        listView.setAdapter(tripListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "Item selected: " + position);
                Trip trip = tripList.get(position);
                Log.e(TAG, trip.getDestinationPlace());
                startTripDetailsActivity(trip);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "New Trip");
                startNewTripActivity();
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        AppController.getInstance().addToRequestQueue(getJSONRequest());
    }

    private void startTripDetailsActivity(Trip trip) {
        Intent intent = new Intent(this, TripDetailsActivity.class);
        intent.putExtra(Constants.TRIP_DETAILS_TRIP_KEY, trip);
        intent.putExtra(Constants.TRIP_DETAILS_EDITABLE_KEY, Constants.DEFAULT_TRIP_DETAILS_EDITABLE);
        startActivity(intent);
    }

    private void startNewTripActivity() {
        Intent intent = new Intent(this, TripDetailsActivity.class);
        intent.putExtra(Constants.TRIP_DETAILS_EDITABLE_KEY, true);
        intent.putExtra(Constants.TRIP_DETAILS_TYPE_KEY, Constants.TRIP_DETAILS_TYPE_N);
        startActivity(intent);
    }

    private void hidePDialog() {
        if(pDialog!=null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public JsonArrayRequest getJSONRequest() {
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e(TAG, response.toString());
                        Log.e(TAG, "Response received: " + response.length());
                        hidePDialog();
                        try {
                            for (int i=0; i<response.length();i++)
                                tripList.add(JSONUtils.getTripFromJson(response.getJSONObject(i)));

                            //tripList = JSONUtils.getTripsFromJson(response, JSONUtils.USE_DEFAULT);
                            //Log.e(TAG, "Trip List size: " + tripList.size());
                            //Log.e(TAG, "getTripsFromJson size: " + JSONUtils.getTripsFromJson(response, JSONUtils.USE_DEFAULT).size());
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                        tripListAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage());
                        hidePDialog();
                    }
                });

        return request;
    }
}
*/