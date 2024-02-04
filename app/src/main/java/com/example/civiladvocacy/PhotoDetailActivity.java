package com.example.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity
{
    private static final String TAG = "PhotoDetailActivity";

    private ConstraintLayout photoActivityConstraintLayout;
    private TextView locationValue;
    private TextView officeValue;
    private TextView nameValue;

    private ImageView photoValue;
    private ImageView partyIconValue;

    private Official officialObject;

    private String partyName;
    private String imageURL;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        photoActivityConstraintLayout = findViewById(R.id.photoDetailConstraintLayout);

        locationValue = findViewById(R.id.location);
        officeValue = findViewById(R.id.office);
        nameValue = findViewById(R.id.name);

        photoValue = findViewById(R.id.photo);
        partyIconValue = findViewById(R.id.partyPhoto);

        Intent intentObject = getIntent();

        if(intentObject.hasExtra("OFFICIAL DETAILS"))
        {
            officialObject = (Official) intentObject.getSerializableExtra("OFFICIAL DETAILS");
        }

        partyName = officialObject.getParty();

        if(officialObject != null)
        {
            String imageLink = officialObject.getImageUrl();
            if(imageLink != null && !imageLink.isEmpty())
            {
                imageURL = imageLink;
            }
            else
            {
                imageURL= "";
            }
        }
       
        officeValue.setText(officialObject.getOffice());
        nameValue.setText(officialObject.getName());

        if(intentObject.hasExtra("LOCATION"))
        {
            String location = intentObject.getStringExtra("LOCATION");
            locationValue.setText(location);
        }

        if (partyName.equals("Democratic Party"))
        {
            partyIconValue.setImageResource(R.drawable.dem_logo);
            photoActivityConstraintLayout.setBackgroundColor(0xff0000ff);
        }

        else if(partyName.equals("Republican Party"))
        {
            partyIconValue.setImageResource(R.drawable.rep_logo);
            photoActivityConstraintLayout.setBackgroundColor(0xffff0000);
        }
        else
        {
            partyIconValue.setVisibility(ImageView.GONE);
            photoActivityConstraintLayout.setBackgroundColor(Color.BLACK);
        }

        downloadAndSetImage();
    }

    private void downloadAndSetImage()
    {
        if (networkCheck())
        {
            // download the official's image using picasso

            if(!imageURL.equals(""))
            {

                Picasso.get().load(imageURL).error(R.drawable.brokenimage).into(photoValue,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Successful load of the image");
                            }

                            @Override
                            public void onError(Exception exception) {
                                Log.d(TAG, "Error: Inside loadImages() function:" + exception.getMessage());
                            }
                        });
            }
            else
            {
                Picasso.get().load(R.drawable.missing).error(R.drawable.missing).into(photoValue,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Successful load of the image");
                            }

                            @Override
                            public void onError(Exception exception) {
                                Log.d(TAG, "Error: Inside loadImages() function:" + exception.getMessage());
                            }
                        });
            }
        }
        else
        {
            photoValue.setImageResource(R.drawable.brokenimage);
            return;
        }
    }

    private boolean networkCheck()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
        {
            Toast.makeText(this, "Cannot access Connectivity Manager", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork == null) ? false : activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void onClickPartyIcon(View view)
    {
        Intent intentObject = null;

        if(partyName.contains("Democratic"))
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse("https://democrats.org"));

        else if(partyName.contains("Republican"))
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gop.com"));

        else
            Log.d(TAG, "clickPartyIcon: ERROR!!!");

        startActivity(intentObject);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}