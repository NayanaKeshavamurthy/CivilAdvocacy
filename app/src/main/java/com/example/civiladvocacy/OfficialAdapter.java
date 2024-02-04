package com.example.civiladvocacy;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder>
{
    private static final String TAG = "OfficialAdapter";

    private MainActivity mainActivityObject;
    private static RequestQueue reqQueueObject;

    private List<Official> officialListObject;

    public OfficialAdapter(List<Official> officialList, MainActivity mainActivity)
    {
        officialListObject = officialList;
        mainActivityObject = mainActivity;

        reqQueueObject = Volley.newRequestQueue(mainActivityObject);
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.official_list, parent, false);

        view.setOnClickListener(mainActivityObject);

        return new OfficialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position)
    {
        Official officialObject = officialListObject.get(position);

        String officeName = officialObject.getOffice();
        holder.officeName.setText(officeName);

        String nameParty = officialObject.getName() + " (" + officialObject.getParty() + ")";
        holder.officialPartyName.setText(nameParty);

        Response.Listener<Bitmap> listener = response ->
        {
            holder.imageView.setImageBitmap(response);
        };

        Response.ErrorListener error = error1 ->
                Log.d(TAG, "OnBindViewHolder:" + error1.getMessage());

        holder.imageView.setImageResource(R.drawable.brokenimage);

        String imageURL = officialObject.getImageUrl();

        if(imageURL.isEmpty())
        {
            holder.imageView.setImageResource(R.drawable.missing);
        }

        imageURL = imageURL.replace("http", "https");
        ImageRequest imageRequest = new ImageRequest(imageURL, listener, 0, 0,
                null, Bitmap.Config.RGB_565, error);

        reqQueueObject.add(imageRequest);
    }

    @Override
    public int getItemCount()
    {
        return officialListObject.size();
    }
}

