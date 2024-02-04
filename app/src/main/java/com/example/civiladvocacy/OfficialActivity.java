package com.example.civiladvocacy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Official officialObject;

    private Picasso picassoObject;

    private TextView locationValue;
    private TextView officeNameValue;
    private TextView officialNameValue;
    private TextView partyNameValue;

    private TextView addressValue;
    private TextView phoneNoValue;
    private TextView webURLValue;
    private TextView emailIdValue;

    private String imageURLValue = "";

    private ImageView officialImageValue;
    private ImageView partyIcon;

    private String fbLinkValue;
    private String twLinkValue;
    private String ytLinkValue;

    private ConstraintLayout photoConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        // intialize the controls
        locationValue = findViewById(R.id.location);
        officeNameValue = findViewById(R.id.officeName);
        officialNameValue = findViewById(R.id.officialName);
        partyNameValue = findViewById(R.id.partyName);
        addressValue = findViewById(R.id.address);
        phoneNoValue = findViewById(R.id.phoneNo);
        webURLValue = findViewById(R.id.webURL);
        emailIdValue = findViewById(R.id.emailID);

        // layout to change background later
        photoConstraintLayout = findViewById(R.id.officialActivityConstraint);

        // initialise the picasso object
        picassoObject = Picasso.get();
        picassoObject.setLoggingEnabled(true);

        officialImageValue = findViewById(R.id.officialImage);
        partyIcon = findViewById(R.id.partyIcon);

        Intent intentObject = getIntent();
        if(intentObject.hasExtra("OFFICIAL_DETAILS"))
        {
            officialObject = (Official) intentObject.getSerializableExtra("OFFICIAL_DETAILS");

            if(officialObject != null) {
                String imageLink = officialObject.getImageUrl();
                if(imageLink != null && !imageLink.isEmpty())
                {
                    imageURLValue = imageLink;
                }
                else
                {
                    imageURLValue = "";
                }
            }
        }

        if(intentObject.hasExtra(Intent.EXTRA_TEXT))
        {
            String strLocation = intentObject.getStringExtra(Intent.EXTRA_TEXT);
            locationValue.setText(strLocation);
        }

        setData();
        downloadAndSetImage();

        // link all the text views to handle hiding
        Linkify.addLinks(addressValue, Linkify.MAP_ADDRESSES);
        Linkify.addLinks(webURLValue, Linkify.ALL);
        Linkify.addLinks(phoneNoValue, Linkify.ALL);
        Linkify.addLinks(emailIdValue, Linkify.ALL);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);

    }


    private void setData()
    {
        officeNameValue.setText(officialObject.getOffice());
        officialNameValue.setText(officialObject.getName());

        // get all the attributes from the official object
        String strPartyName = officialObject.getParty();
        if(strPartyName != null && !strPartyName.isEmpty())
        {
            partyNameValue.setText("(" + strPartyName + ")");

            if (strPartyName.equals("Republican Party"))
            {
                photoConstraintLayout.setBackgroundColor(0xffff0000);
                partyIcon.setImageResource(R.drawable.rep_logo);
            }

            else if(strPartyName.equals("Democratic Party"))
            {

                photoConstraintLayout.setBackgroundColor(0xff0000ff);
                partyIcon.setImageResource(R.drawable.dem_logo);
            }
            else
            {
                photoConstraintLayout.setBackgroundColor(Color.BLACK);
                partyIcon.setVisibility(ImageView.GONE);
            }
        }

        String address = officialObject.getAddress();
        if (address != null && !address.isEmpty())
            addressValue.setText(address);
        else
        {
            TextView addressTitle = findViewById(R.id.addressTitle);
            addressTitle.setVisibility(TextView.GONE);
            addressValue.setVisibility(TextView.GONE);
        }


        String webUrl = officialObject.getWebUrl();
        if(webUrl != null && !webUrl.isEmpty())
            webURLValue.setText(webUrl);
        else
        {
            TextView webTitle = findViewById(R.id.webTitle);
            webTitle.setVisibility(TextView.GONE);
            webURLValue.setVisibility(TextView.GONE);
        }

        String emailId = officialObject.getEmail();
        if (emailId != null && !emailId.isEmpty())
            emailIdValue.setText(emailId);
        else
        {
            TextView emailTitle = findViewById(R.id.emailTitle);
            emailTitle.setVisibility(TextView.GONE);
            emailIdValue.setVisibility(TextView.GONE);
        }

        String phoneNo = officialObject.getPhoneNumber();
        if (phoneNo != null && !phoneNo.isEmpty())
            phoneNoValue.setText(phoneNo);
        else
        {
            TextView phoneTitle = findViewById(R.id.phoneTitle);
            phoneTitle.setVisibility(TextView.GONE);
            phoneNoValue.setVisibility(TextView.GONE);
        }

        String fbID = officialObject.getFbLink();
        if(fbID != null && !fbID.isEmpty())
            fbLinkValue = fbID;
        else
        {
            ImageView fbIcon = findViewById(R.id.facebook);
            fbIcon.setVisibility(ImageView.GONE);
        }

        String twID = officialObject.getTwLink();
        if(twID != null && !twID.isEmpty())
            twLinkValue = twID;
        else
        {
            ImageView tIcon = findViewById(R.id.twitter);
            tIcon.setVisibility(ImageView.GONE);
        }

        String ytID = officialObject.getYtLink();
        if(ytID != null && !ytID.isEmpty())
            ytLinkValue = ytID;
        else
        {
            ImageView tIcon = findViewById(R.id.youtube);
            tIcon.setVisibility(ImageView.GONE);
        }
    }

    private boolean networkCheck() {
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

    private void handleResult(ActivityResult result) {
    }
    private void downloadAndSetImage()
    {
        if (networkCheck())
        {
            // download the official's image using picasso
            if(!imageURLValue.equals(""))
            {

                Picasso.get().load(imageURLValue).error(R.drawable.brokenimage).into(officialImageValue,
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
                Picasso.get().load(R.drawable.missing).error(R.drawable.missing).into(officialImageValue,
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
            officialImageValue.setImageResource(R.drawable.brokenimage);
            return;
        }
    }

    public void OnClickTwitter(View v) {
        // get twitter package manager
        String twitterAppUrl = "twitter://user?screen_name=" + twLinkValue;
        String twitterWebUrl = "https://twitter.com/" + twLinkValue;

        Intent intentObject;

        try
        {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        }
        catch (Exception e)
        {
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }

        startActivity(intentObject);
    }

    public void onClickYoutube(View v)
    {
        Intent intentObject = null;
        try
        {
            // intent setting the link's URL
            intentObject = new Intent(Intent.ACTION_VIEW);
            intentObject.setPackage("com.google.android.youtube");
            intentObject.setData(Uri.parse("https://www.youtube.com/" + ytLinkValue));
            startActivity(intentObject);
        }
        catch (ActivityNotFoundException e)
        {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + ytLinkValue)));
        }
    }

    public void onClickFacebook(View v) {
        // get the facebook url
        String fbURL = "https://www.facebook.com/" + fbLinkValue;

        Intent intentObject;
        String urlToUse;
        try {
            // get fb package manager
            getPackageManager().getPackageInfo("com.facebook.katana",0);
            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;

            if(versionCode >= 3002850) {
                // newer version of Facebook app
                urlToUse = "fb://facewebmodal/f?href=" + fbURL;
            }
            else
            { // older
                urlToUse = "fb://page/" + fbLinkValue;
            }
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        }
        catch (Exception e)
        {
            // if no app, open through web browser
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse(fbURL));
        }

        startActivity(intentObject);
    }

    public void onPhotoClick(View view)
    {
        if(!officialObject.getImageUrl().equals(""))
        {
            Intent intentObject = new Intent(this, PhotoDetailActivity.class);
            String location = locationValue.getText().toString();

            // save the data into the intent
            intentObject.putExtra("OFFICIAL DETAILS", officialObject);
            intentObject.putExtra("LOCATION", location);
            activityResultLauncher.launch(intentObject);
        }
    }

    public void onClickPartyImage(View view)
    {
        Intent intentObject = null;

        if(officialObject.getParty().contains("Democratic"))
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse("https://democrats.org"));

        else if(officialObject.getParty().contains("Republican"))
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gop.com"));

        else
            Log.d(TAG, "clickPartyIcon: ERROR!");

        startActivity(intentObject);
    }

}