package com.example.civiladvocacy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private final ArrayList<Official> officialsList = new ArrayList<>();

    private OfficialAdapter mAdapter;

    private RequestQueue queue;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private String locationString = "Unable to fetch location";

    private TextView locationText;

    MenuItem locationMenu, infoMenu;

    private static final String apiURL = "https://www.googleapis.com/civicinfo/v2/representatives?key=<>="; //insert your api key instead of <>


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        locationText = findViewById(R.id.location);
        TextView noNetworkText = findViewById(R.id.noNetwork);
        noNetworkText.setVisibility(View.INVISIBLE);

        mAdapter = new OfficialAdapter(officialsList,this);
        recyclerView.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);

        //locationText.setText(locationString);

        queue = Volley.newRequestQueue(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (savedInstanceState != null && !savedInstanceState.isEmpty()){
            String officialLocation = savedInstanceState.getString("Location");
            locationText.setText(officialLocation);
            fetchData(officialLocation);
        }

        else
        {
            if (!hasNetworkConnection())
            {
                locationText.setText("No Data for Location");
                String text = "<b>No Network Connection</b>" + "<br>Data cannot be accessed/loaded without an internet connection.";
                noNetworkText.setVisibility(View.VISIBLE);
                noNetworkText.setText(Html.fromHtml(text));
                setTitle("Know Your Government");
                ActionBar actionBar;
                actionBar = getSupportActionBar();

                ColorDrawable colorDrawable
                        = new ColorDrawable(Color.parseColor("#FFBAA2BD"));

                assert actionBar != null;
                actionBar.setBackgroundDrawable(colorDrawable);
            }
            else
            {
                findLocation();
            }
        }

    }

    private void  findLocation()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;

        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    // Get last known location. In some situations this can be null.
                    if (location != null)
                    {
                        locationString = fetchLocation(location);
                        //fetchData(locationString.split("\n")[0]);
                        fetchData(locationString);
                        locationText.setText(locationString.split("\n")[0]);
                    }
                }).addOnFailureListener(this, e ->Toast.makeText(MainActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private String fetchLocation(Location loc)
    {
        // get the location by getting the list of address from geoder
        StringBuilder sb = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try
        {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String pinCode = addresses.get(0).getPostalCode();
            //sb.append(String.format(Locale.getDefault(),
            //        "%s, %s%n%nProvider: %s%n%n%.5f, %.5f",
             //       city, state, loc.getProvider(), loc.getLatitude(), loc.getLongitude()));
            sb.append(city.isEmpty()? state + " " + pinCode : city + ", " + state + " " + pinCode);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putString("Location", locationText.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // restoring the state as previous before there is change in orientation
        super.onRestoreInstanceState(savedInstanceState);

    }
    public void handleResult(ActivityResult result)
    {
        if (result.getResultCode() == RESULT_OK)
        {
            Intent data = result.getData();
            if (data == null)
                return;
        }
    }

    private boolean hasNetworkConnection()
    {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        locationMenu = menu.findItem(R.id.location);
        infoMenu = menu.findItem(R.id.info);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        // check the selection of the menu
        if(menuItem.getItemId() == R.id.location)
        {
            if (hasNetworkConnection()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // creating the dialog to prone the user to enter new location
                final EditText inputLocation = new EditText(MainActivity.this);
                inputLocation.setGravity(Gravity.CENTER_HORIZONTAL);

                inputLocation.setInputType(InputType.TYPE_CLASS_TEXT);

                // set OK button to handle
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String loc = inputLocation.getText().toString();
                        locationText.setText(loc);
                        fetchData(loc);
                    }
                });

                // set CANCEL button
                builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

                builder.setTitle("Enter Address");
                builder.setView(inputLocation);

                // display the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }

            else
            {
                Toast.makeText(this, "The function cannot be used when there is no network connection ", Toast.LENGTH_SHORT).show();
            }
        }
        else if(menuItem.getItemId() == R.id.info)
        {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }
        else
        {
            return super.onOptionsItemSelected(menuItem);
        }

        return true;
    }

    public void fetchData(String location)
    {
        String urlToUse = apiURL + location;

        // call the response listener to parse the api data
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response){
                try
                {
                    officialsList.clear();

                    String jLocationValue = "";
                    JSONObject jLocation = response.getJSONObject("normalizedInput");
                   if (jLocation.has("line1"))
                        jLocationValue = jLocationValue + jLocation.getString("line1")+" ";

                    if (jLocation.has("city"))
                        jLocationValue = jLocationValue + jLocation.getString("city")+" ,";

                    if (jLocation.has("state"))
                        jLocationValue = jLocationValue + jLocation.getString("state")+" ";

                    if (jLocation.has("zip"))
                        jLocationValue = jLocationValue + jLocation.getString("zip");


                    // Get the array of offices and official arrays
                    JSONArray jOfficesArray = response.getJSONArray("offices");
                    JSONArray jOfficialsArray = response.getJSONArray("officials");

                    for (int count = 0; count < jOfficesArray.length(); count++)
                    {
                        JSONObject jsonOfficesObject = jOfficesArray.getJSONObject(count);
                        String officeName = jsonOfficesObject.getString("name");

                        // get the indices of the officials data and get value to jump to the official data
                        JSONArray jOfficialIndicesArray = jsonOfficesObject.getJSONArray("officialIndices");
                        for (int counter = 0; counter < jOfficialIndicesArray.length(); counter++)
                        {
                            int val = jOfficialIndicesArray.getInt(counter);

                            // Get details of officials
                            JSONObject jOfficialsObject = jOfficialsArray.getJSONObject(val);
                            String officialName = jOfficialsObject.getString("name");

                            String officialAddress = "";
                            if (jOfficialsObject.has("address"))
                            {
                                JSONArray jAddressArray = jOfficialsObject.getJSONArray("address");
                                JSONObject jsonAddressObject = jAddressArray.getJSONObject(0);
                                officialAddress = jsonAddressObject.getString("line1") + "," + jsonAddressObject.getString("city")
                                        + "," + jsonAddressObject.getString("state")  + ","
                                        + jsonAddressObject.getString("zip");
                            }

                            String officialPartyName = "";
                            if (jOfficialsObject.has("party"))
                                officialPartyName = jOfficialsObject.getString("party");

                            String officialPhoneNum = "";
                            if (jOfficialsObject.has("phones"))
                            {
                                JSONArray jPhoneArray = jOfficialsObject.getJSONArray("phones");
                                officialPhoneNum =jPhoneArray.getString(0);
                            }

                            String officialWebUrl = "";
                            if (jOfficialsObject.has("urls"))
                            {
                                JSONArray jWebUrlArray = jOfficialsObject.getJSONArray("urls");
                                officialWebUrl = jWebUrlArray.getString(0);
                            }

                            String officialEmail = "";
                            if (jOfficialsObject.has("emails"))
                            {
                                JSONArray emails = jOfficialsObject.getJSONArray("emails");
                                officialEmail = emails.getString(0);
                            }

                            String officialPhotoUrl = "";
                            if (jOfficialsObject.has("photoUrl"))
                                officialPhotoUrl = jOfficialsObject.getString("photoUrl");

                            String officialYTLink="";
                            String officialFBLink="";
                            String officialTWLink="";

                            // get channels array and get links of youtube
                            if (jOfficialsObject.has("channels"))
                            {
                                JSONArray jChannelsArray = jOfficialsObject.getJSONArray("channels");
                                for (int channelCount=0; channelCount < jChannelsArray.length(); channelCount++)
                                {

                                    JSONObject jsonChannelObject = jChannelsArray.getJSONObject(channelCount);
                                    if (jsonChannelObject.getString("type").equals("Twitter"))
                                        officialTWLink = jsonChannelObject.getString("id");

                                    if (jsonChannelObject.getString("type").equals("Youtube"))
                                        officialYTLink =  jsonChannelObject.getString("id");

                                    if (jsonChannelObject.getString("type").equals("Facebook"))
                                        officialFBLink = jsonChannelObject.getString("id");

                                }
                            }

                            // Call the official class constructor
                            officialsList.add(new Official(officialName, officeName, officialPartyName, officialFBLink, officialTWLink,
                                    officialYTLink, officialWebUrl, officialAddress, officialEmail, officialPhoneNum, officialPhotoUrl));

                        }
                    }

                    mAdapter.notifyItemInserted(officialsList.size());
                    mAdapter.notifyDataSetChanged();
                    locationText.setText(jLocationValue);
                    locationString = jLocationValue;

                    setTitle("Civil Advocacy");
                    ActionBar actionBar;
                    actionBar = getSupportActionBar();

                    ColorDrawable colorDrawable
                            = new ColorDrawable(Color.parseColor("#431144"));

                    assert actionBar != null;
                    actionBar.setBackgroundDrawable(colorDrawable);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try
                {
                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                    Log.d(TAG, "OnErrorResponse: " + jsonObject);

                    locationText.setText("No Data for the location");
                    officialsList.clear();

                    mAdapter.notifyDataSetChanged();
                    setTitle("Know your Government");
                    ActionBar actionBar;
                    actionBar = getSupportActionBar();

                    ColorDrawable colorDrawable
                            = new ColorDrawable(Color.parseColor("#FFBAA2BD"));

                    assert actionBar != null;
                    actionBar.setBackgroundDrawable(colorDrawable);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse,
                null, listener, error);
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }

    @Override
    public void onClick(View view)
    {
        // On click of the recycle get its position and get specific item
        int position = recyclerView.getChildLayoutPosition(view);
        Official officialObject = officialsList.get(position);

        // start the official activity of clicked item
        Intent intentObject = new Intent(this, OfficialActivity.class);
        intentObject.putExtra("OFFICIAL_DETAILS", officialObject);
        intentObject.putExtra(Intent.EXTRA_TEXT, locationString);
        activityResultLauncher.launch(intentObject);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findLocation();
                } else {
                    Toast.makeText(this, "Location Access Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
